use base64::Engine as _;
use rusqlite::{params, Connection, ErrorCode, OptionalExtension};
use serde::{Deserialize, Serialize};
use serde_json::Value;
use std::path::{Path, PathBuf};
use std::thread;
use std::time::{Duration, SystemTime, UNIX_EPOCH};
use tauri::{Manager, State};

const DB_BUSY_TIMEOUT_MS: u64 = 4_000;
const DB_RETRY_DELAYS_MS: [u64; 3] = [120, 280, 600];
const BACKUP_KEEP_FILES: usize = 60;
const SYNC_BACKUP_PREFIX: &str = "finanz-tracker-sync-";
const SYNC_BACKUP_LATEST_FILE: &str = "finanz-tracker-sync-latest.json";

struct AppDb {
  path: PathBuf,
}

#[derive(Debug, Clone, Serialize, Deserialize, Default)]
struct CloudConfig {
  sync_folder_path: String,
}

#[derive(Debug, Clone, Serialize)]
struct SyncStatus {
  configured: bool,
  folder_path: String,
}

fn ensure_db(path: &Path) -> Result<(), String> {
  if let Some(parent) = path.parent() {
    std::fs::create_dir_all(parent).map_err(|e| format!("Datenordner konnte nicht erstellt werden: {e}"))?;
  }

  let conn = Connection::open(path).map_err(|e| format!("SQLite konnte nicht geöffnet werden: {e}"))?;
  configure_conn(&conn)?;

  conn
    .execute(
      "CREATE TABLE IF NOT EXISTS app_state (
      id INTEGER PRIMARY KEY CHECK (id = 1),
      payload TEXT NOT NULL,
      updated_at TEXT NOT NULL DEFAULT (datetime('now'))
    )",
      [],
    )
    .map_err(|e| format!("SQLite-Schema konnte nicht erstellt werden: {e}"))?;

  Ok(())
}

fn configure_conn(conn: &Connection) -> Result<(), String> {
  conn
    .busy_timeout(Duration::from_millis(DB_BUSY_TIMEOUT_MS))
    .map_err(|e| format!("SQLite busy_timeout konnte nicht gesetzt werden: {e}"))?;

  conn
    .execute_batch(
      "PRAGMA journal_mode = WAL;
       PRAGMA synchronous = NORMAL;
       PRAGMA foreign_keys = ON;",
    )
    .map_err(|e| format!("SQLite PRAGMA konnte nicht gesetzt werden: {e}"))?;

  Ok(())
}

fn open_conn(db: &AppDb) -> Result<Connection, String> {
  let conn = Connection::open(&db.path).map_err(|e| format!("SQLite konnte nicht geöffnet werden: {e}"))?;
  configure_conn(&conn)?;
  Ok(conn)
}

fn is_locked_error(err: &rusqlite::Error) -> bool {
  match err {
    rusqlite::Error::SqliteFailure(code, _) => {
      matches!(code.code, ErrorCode::DatabaseBusy | ErrorCode::DatabaseLocked)
    }
    _ => false,
  }
}

fn with_retry_sqlite<T, F>(action: &str, mut op: F) -> Result<T, String>
where
  F: FnMut() -> rusqlite::Result<T>,
{
  let mut attempt = 0usize;

  loop {
    match op() {
      Ok(value) => return Ok(value),
      Err(err) => {
        if is_locked_error(&err) && attempt < DB_RETRY_DELAYS_MS.len() {
          let delay = DB_RETRY_DELAYS_MS[attempt];
          attempt += 1;
          thread::sleep(Duration::from_millis(delay));
          continue;
        }

        if is_locked_error(&err) {
          return Err(format!(
            "{action} fehlgeschlagen: Datenbank ist gesperrt. Bitte kurz warten und erneut versuchen. ({err})"
          ));
        }

        return Err(format!("{action} fehlgeschlagen: {err}"));
      }
    }
  }
}

fn sanitize_filename_stem(name: &str) -> String {
  let mut cleaned = String::with_capacity(name.len());
  for ch in name.chars() {
    let invalid = matches!(ch, '<' | '>' | ':' | '"' | '/' | '\\' | '|' | '?' | '*');
    cleaned.push(if invalid { '_' } else { ch });
  }

  cleaned.trim().trim_matches('.').to_string()
}

fn sanitize_csv_filename(name: &str) -> String {
  let trimmed = sanitize_filename_stem(name);
  if trimmed.is_empty() {
    "auswertung.csv".to_string()
  } else if trimmed.to_lowercase().ends_with(".csv") {
    trimmed
  } else {
    format!("{trimmed}.csv")
  }
}

fn sanitize_report_filename(name: &str, default_ext: &str) -> String {
  let trimmed = sanitize_filename_stem(name);
  if trimmed.is_empty() {
    format!("auswertung.{default_ext}")
  } else if Path::new(&trimmed).extension().is_some() {
    trimmed
  } else {
    format!("{trimmed}.{default_ext}")
  }
}

fn sanitize_json_filename(name: &str) -> String {
  let trimmed = sanitize_filename_stem(name);
  if trimmed.is_empty() {
    "state-backup.json".to_string()
  } else if trimmed.to_lowercase().ends_with(".json") {
    trimmed
  } else {
    format!("{trimmed}.json")
  }
}

fn app_data_root(db: &AppDb) -> Result<PathBuf, String> {
  db.path
    .parent()
    .map(PathBuf::from)
    .ok_or_else(|| "App-Datenpfad konnte nicht bestimmt werden.".to_string())
}

fn app_backup_dir(db: &AppDb) -> Result<PathBuf, String> {
  Ok(app_data_root(db)?.join("backups"))
}

fn cloud_config_path(db: &AppDb) -> Result<PathBuf, String> {
  Ok(app_data_root(db)?.join("cloud_config.json"))
}

fn atomic_write(path: &Path, content: &str) -> Result<(), String> {
  let parent = path
    .parent()
    .ok_or_else(|| "Ungültiger Dateipfad für atomisches Schreiben.".to_string())?;
  std::fs::create_dir_all(parent).map_err(|e| format!("Ordner konnte nicht erstellt werden: {e}"))?;

  let nanos = SystemTime::now()
    .duration_since(UNIX_EPOCH)
    .map(|d| d.as_nanos())
    .unwrap_or(0);
  let tmp = parent.join(format!("tmp_{nanos}.json"));

  std::fs::write(&tmp, content).map_err(|e| format!("Temporäre Datei konnte nicht geschrieben werden: {e}"))?;
  if path.exists() {
    let _ = std::fs::remove_file(path);
  }
  std::fs::rename(&tmp, path).map_err(|e| format!("Datei konnte nicht finalisiert werden: {e}"))?;
  Ok(())
}

fn read_cloud_config(db: &AppDb) -> Result<CloudConfig, String> {
  let path = cloud_config_path(db)?;
  if !path.exists() {
    return Ok(CloudConfig::default());
  }

  let raw = std::fs::read_to_string(&path).map_err(|e| format!("Cloud-Konfiguration konnte nicht gelesen werden: {e}"))?;

  if let Ok(parsed) = serde_json::from_str::<CloudConfig>(&raw) {
    return Ok(parsed);
  }

  // Backward compatibility for older config formats (e.g. OAuth-only config).
  let value: Value = serde_json::from_str(&raw)
    .map_err(|e| format!("Cloud-Konfiguration ist ungültig: {e}"))?;

  let sync_folder_path = value
    .get("sync_folder_path")
    .and_then(|v| v.as_str())
    .unwrap_or_default()
    .trim()
    .to_string();

  Ok(CloudConfig { sync_folder_path })
}

fn write_cloud_config(db: &AppDb, cfg: &CloudConfig) -> Result<(), String> {
  let path = cloud_config_path(db)?;
  let content = serde_json::to_string_pretty(cfg)
    .map_err(|e| format!("Cloud-Konfiguration konnte nicht serialisiert werden: {e}"))?;
  atomic_write(&path, &content)
}

fn prune_backup_files(dir: &Path, keep: usize) -> Result<(), String> {
  let mut files = Vec::new();

  for entry in std::fs::read_dir(dir).map_err(|e| format!("Backup-Ordner konnte nicht gelesen werden: {e}"))? {
    let entry = entry.map_err(|e| format!("Backup-Datei konnte nicht gelesen werden: {e}"))?;
    let path = entry.path();

    if !path.is_file() {
      continue;
    }

    let name = path
      .file_name()
      .and_then(|n| n.to_str())
      .unwrap_or_default()
      .to_lowercase();

    if !name.ends_with(".json") {
      continue;
    }

    let modified = std::fs::metadata(&path)
      .and_then(|m| m.modified())
      .unwrap_or(UNIX_EPOCH);

    files.push((path, modified));
  }

  files.sort_by(|a, b| b.1.cmp(&a.1));

  for (idx, (path, _)) in files.into_iter().enumerate() {
    if idx < keep {
      continue;
    }
    let _ = std::fs::remove_file(path);
  }

  Ok(())
}

fn resolve_sync_folder(db: &AppDb) -> Result<PathBuf, String> {
  let cfg = read_cloud_config(db)?;
  let trimmed = cfg.sync_folder_path.trim();
  if trimmed.is_empty() {
    return Err("Sync-Ordner ist noch nicht konfiguriert.".to_string());
  }

  let path = PathBuf::from(trimmed);
  std::fs::create_dir_all(&path)
    .map_err(|e| format!("Sync-Ordner konnte nicht erstellt werden: {e}"))?;

  if !path.is_dir() {
    return Err("Der konfigurierte Sync-Pfad ist kein Ordner.".to_string());
  }

  Ok(path)
}

fn latest_sync_backup_file(folder: &Path) -> Result<Option<PathBuf>, String> {
  let mut files: Vec<(PathBuf, SystemTime)> = Vec::new();

  for entry in std::fs::read_dir(folder).map_err(|e| format!("Sync-Ordner konnte nicht gelesen werden: {e}"))? {
    let entry = entry.map_err(|e| format!("Sync-Datei konnte nicht gelesen werden: {e}"))?;
    let path = entry.path();
    if !path.is_file() {
      continue;
    }

    let name = path
      .file_name()
      .and_then(|n| n.to_str())
      .unwrap_or_default()
      .to_lowercase();

    if !name.ends_with(".json") {
      continue;
    }

    if !name.starts_with(SYNC_BACKUP_PREFIX) {
      continue;
    }

    let modified = std::fs::metadata(&path)
      .and_then(|m| m.modified())
      .unwrap_or(UNIX_EPOCH);

    files.push((path, modified));
  }

  files.sort_by(|a, b| b.1.cmp(&a.1));
  Ok(files.into_iter().next().map(|x| x.0))
}

#[tauri::command]
fn db_load_state(db: State<AppDb>) -> Result<Option<String>, String> {
  let conn = open_conn(&db)?;

  with_retry_sqlite("SQLite-Lesen", || {
    conn
      .query_row("SELECT payload FROM app_state WHERE id = 1", [], |row| row.get::<_, String>(0))
      .optional()
  })
}

#[tauri::command]
fn db_save_state(payload: String, db: State<AppDb>) -> Result<(), String> {
  let parsed: Value = serde_json::from_str(&payload)
    .map_err(|e| format!("Ungültiges JSON für Speicherung: {e}"))?;

  if !parsed.is_object() {
    return Err("Ungültige Datenstruktur: Root muss ein JSON-Objekt sein.".to_string());
  }

  let conn = open_conn(&db)?;

  with_retry_sqlite("SQLite-Schreiben", || {
    conn.execute(
      "INSERT INTO app_state (id, payload, updated_at)
       VALUES (1, ?1, datetime('now'))
       ON CONFLICT(id) DO UPDATE
       SET payload = excluded.payload,
           updated_at = datetime('now')",
      params![payload],
    )
  })?;

  Ok(())
}

#[tauri::command]
fn db_write_backup(filename: String, payload: String, db: State<AppDb>) -> Result<String, String> {
  let parsed: Value = serde_json::from_str(&payload)
    .map_err(|e| format!("Ungültiges JSON für Backup: {e}"))?;

  if !parsed.is_object() {
    return Err("Ungültige Datenstruktur für Backup: Root muss ein JSON-Objekt sein.".to_string());
  }

  let backup_dir = app_backup_dir(&db)?;
  std::fs::create_dir_all(&backup_dir)
    .map_err(|e| format!("Backup-Ordner konnte nicht erstellt werden: {e}"))?;

  let backup_name = sanitize_json_filename(&filename);
  let target = backup_dir.join(backup_name);

  atomic_write(&target, &payload)?;
  let _ = prune_backup_files(&backup_dir, BACKUP_KEEP_FILES);

  Ok(target.to_string_lossy().to_string())
}

#[tauri::command]
fn sync_pick_folder() -> Result<Option<String>, String> {
  let selected = rfd::FileDialog::new().pick_folder();
  Ok(selected.map(|p| p.to_string_lossy().to_string()))
}

#[tauri::command]
fn sync_get_status(db: State<AppDb>) -> Result<SyncStatus, String> {
  let cfg = read_cloud_config(&db)?;
  let folder_path = cfg.sync_folder_path.trim().to_string();
  Ok(SyncStatus {
    configured: !folder_path.is_empty(),
    folder_path,
  })
}

#[tauri::command]
fn sync_set_folder(folder_path: String, db: State<AppDb>) -> Result<(), String> {
  let trimmed = folder_path.trim();
  if trimmed.is_empty() {
    return Err("Bitte einen gültigen Sync-Ordner angeben.".to_string());
  }

  let path = PathBuf::from(trimmed);
  std::fs::create_dir_all(&path)
    .map_err(|e| format!("Sync-Ordner konnte nicht erstellt werden: {e}"))?;

  if !path.is_dir() {
    return Err("Der angegebene Pfad ist kein Ordner.".to_string());
  }

  let canonical = path
    .canonicalize()
    .unwrap_or(path)
    .to_string_lossy()
    .to_string();

  let mut cfg = read_cloud_config(&db)?;
  cfg.sync_folder_path = canonical;
  write_cloud_config(&db, &cfg)
}

#[tauri::command]
fn sync_write_backup(payload: String, db: State<AppDb>) -> Result<String, String> {
  let parsed: Value = serde_json::from_str(&payload)
    .map_err(|e| format!("Ungültiges JSON für Sync-Backup: {e}"))?;

  if !parsed.is_object() {
    return Err("Ungültige Datenstruktur für Sync-Backup: Root muss ein JSON-Objekt sein.".to_string());
  }

  let folder = resolve_sync_folder(&db)?;
  let target = folder.join(SYNC_BACKUP_LATEST_FILE);

  atomic_write(&target, &payload)?;

  Ok(target.to_string_lossy().to_string())
}

#[tauri::command]
fn sync_restore_latest(db: State<AppDb>) -> Result<String, String> {
  let folder = resolve_sync_folder(&db)?;

  let latest_fixed = folder.join(SYNC_BACKUP_LATEST_FILE);
  let path = if latest_fixed.exists() {
    latest_fixed
  } else {
    latest_sync_backup_file(&folder)?
      .ok_or_else(|| "Im Sync-Ordner wurde keine Sicherungsdatei gefunden.".to_string())?
  };

  let raw = std::fs::read_to_string(&path)
    .map_err(|e| format!("Sync-Sicherung konnte nicht gelesen werden: {e}"))?;

  let parsed: Value = serde_json::from_str(&raw)
    .map_err(|e| format!("Sync-Sicherung ist ungültig: {e}"))?;

  if !parsed.is_object() {
    return Err("Sync-Sicherung hat ein ungültiges Format (Root muss ein JSON-Objekt sein).".to_string());
  }

  Ok(raw)
}

#[tauri::command]
fn write_report_csv(filename: String, content: String) -> Result<String, String> {
  let safe_name = sanitize_csv_filename(&filename);

  let picked = rfd::FileDialog::new()
    .set_title("CSV speichern")
    .set_file_name(&safe_name)
    .add_filter("CSV", &["csv"])
    .save_file();

  let target = if let Some(path) = picked {
    path
  } else {
    return Err("EXPORT_CANCELED".to_string());
  };

  std::fs::write(&target, content).map_err(|e| format!("Datei konnte nicht geschrieben werden: {e}"))?;

  Ok(target.to_string_lossy().to_string())
}

#[tauri::command]
fn write_report_binary(filename: String, content_base64: String) -> Result<String, String> {
  let safe_name = sanitize_report_filename(&filename, "bin");
  let ext = Path::new(&safe_name)
    .extension()
    .and_then(|e| e.to_str())
    .unwrap_or("")
    .to_lowercase();

  let mut dialog = rfd::FileDialog::new()
    .set_title("Export speichern")
    .set_file_name(&safe_name);

  if ext == "xlsx" {
    dialog = dialog.add_filter("Excel", &["xlsx"]);
  } else if ext == "pdf" {
    dialog = dialog.add_filter("PDF", &["pdf"]);
  }

  let target = if let Some(path) = dialog.save_file() {
    path
  } else {
    return Err("EXPORT_CANCELED".to_string());
  };

  let bytes = base64::engine::general_purpose::STANDARD
    .decode(content_base64)
    .map_err(|e| format!("Exportinhalt konnte nicht decodiert werden: {e}"))?;

  std::fs::write(&target, bytes).map_err(|e| format!("Datei konnte nicht geschrieben werden: {e}"))?;

  Ok(target.to_string_lossy().to_string())
}

#[tauri::command]
fn open_external_url(url: String) -> Result<(), String> {
  let trimmed = url.trim();
  if trimmed.is_empty() {
    return Err("URL ist leer.".to_string());
  }

  webbrowser::open(trimmed)
    .map(|_| ())
    .map_err(|e| format!("Externer Browser konnte nicht geöffnet werden: {e}"))
}

#[cfg_attr(mobile, tauri::mobile_entry_point)]
pub fn run() {
  tauri::Builder::default()
    .setup(|app| {
      let app_data_dir = app
        .path()
        .app_data_dir()
        .map_err(|e| format!("App-Datenpfad konnte nicht ermittelt werden: {e}"))?;
      let db_path = app_data_dir.join("finanz_tracker.sqlite3");

      ensure_db(&db_path)?;
      app.manage(AppDb { path: db_path });

      if cfg!(debug_assertions) {
        app.handle().plugin(
          tauri_plugin_log::Builder::default()
            .level(log::LevelFilter::Info)
            .build(),
        )?;
      }
      Ok(())
    })
    .invoke_handler(tauri::generate_handler![
      db_load_state,
      db_save_state,
      db_write_backup,
      sync_pick_folder,
      sync_get_status,
      sync_set_folder,
      sync_write_backup,
      sync_restore_latest,
      write_report_csv,
      write_report_binary,
      open_external_url
    ])
    .run(tauri::generate_context!())
    .expect("error while running tauri application");
}

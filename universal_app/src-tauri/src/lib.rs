use rusqlite::{params, Connection, ErrorCode, OptionalExtension};
use serde_json::Value;
use std::path::{Path, PathBuf};
use std::thread;
use std::time::{Duration, SystemTime, UNIX_EPOCH};
use tauri::{Manager, State};

const DB_BUSY_TIMEOUT_MS: u64 = 4_000;
const DB_RETRY_DELAYS_MS: [u64; 3] = [120, 280, 600];
const BACKUP_KEEP_FILES: usize = 60;

struct AppDb {
  path: PathBuf,
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

fn app_backup_dir(db: &AppDb) -> Result<PathBuf, String> {
  let parent = db
    .path
    .parent()
    .ok_or_else(|| "Backup-Pfad konnte nicht bestimmt werden.".to_string())?;
  Ok(parent.join("backups"))
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

  let nanos = SystemTime::now()
    .duration_since(UNIX_EPOCH)
    .map(|d| d.as_nanos())
    .unwrap_or(0);
  let temp = backup_dir.join(format!("backup_tmp_{nanos}.json"));

  std::fs::write(&temp, payload).map_err(|e| format!("Backup-Datei konnte nicht geschrieben werden: {e}"))?;

  if target.exists() {
    let _ = std::fs::remove_file(&target);
  }

  std::fs::rename(&temp, &target).map_err(|e| format!("Backup-Datei konnte nicht finalisiert werden: {e}"))?;

  let _ = prune_backup_files(&backup_dir, BACKUP_KEEP_FILES);

  Ok(target.to_string_lossy().to_string())
}

#[tauri::command]
fn write_report_csv(filename: String, content: String) -> Result<String, String> {
  let mut target = dirs::download_dir()
    .or_else(dirs::document_dir)
    .ok_or_else(|| "Kein Download- oder Dokumente-Ordner gefunden.".to_string())?;

  let safe_name = sanitize_csv_filename(&filename);
  target.push(safe_name);

  std::fs::write(&target, content).map_err(|e| format!("Datei konnte nicht geschrieben werden: {e}"))?;

  Ok(target.to_string_lossy().to_string())
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
    .invoke_handler(tauri::generate_handler![db_load_state, db_save_state, db_write_backup, write_report_csv])
    .run(tauri::generate_context!())
    .expect("error while running tauri application");
}

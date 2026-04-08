# Backup & Restore

## Desktop
- Desktop data is persisted in SQLite.
- Daily JSON backups are created automatically by desktop backend.
- Optional folder sync file:
  - `finanz-tracker-sync-latest.json`

## Android
- Local app state file:
  - internal `state.json` (app-private storage)
- Optional folder sync file:
  - `finanz-tracker-sync-latest.json` via SAF-selected folder

## Recommended backup workflow
1. Configure same sync folder on desktop and Android.
2. Use external sync tool (FolderSync/Syncthing/Drive Desktop).
3. Keep two-way sync and deleted-file sync enabled.
4. Export periodic reports for archival (CSV/PDF/XLSX).

## Restore workflow
1. Ensure sync folder contains latest `finanz-tracker-sync-latest.json`.
2. Open app and select same sync folder.
3. App auto-loads sync state at startup.
4. Validate users/bookings and report totals.

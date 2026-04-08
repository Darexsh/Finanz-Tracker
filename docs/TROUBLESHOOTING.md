# Troubleshooting Guide

## Android build fails
- Error: `JAVA_HOME is not set`
  - Install JDK 17.
  - Set `JAVA_HOME` to JDK path.
  - Reopen terminal/Android Studio.

## Sync file not updating
- Verify sync folder is selected in Android `Synchronisierung`.
- Ensure app has persisted SAF read/write permission.
- In external sync tool, enable sync of deleted files (two-way setups).

## Categories differ between Android/Desktop
- Use latest app versions.
- Re-save booking on Android to apply category normalization.
- Trigger manual sync restore on desktop.

## Export failed on Android
- Ensure file picker permission is granted.
- Retry with a different target folder.
- Test CSV first, then PDF/XLSX.

## UI shows stale data after sync
- Restart app once.
- Confirm external sync app finished transfer before opening app.

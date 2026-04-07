# Mobile App (Android)

Android app workspace for Finanz Tracker.

## Current Status
- Android project scaffold is in place.
- Stack: Kotlin + Jetpack Compose + Groovy Gradle (build.gradle).
- applicationId: com.darexsh.finanztracker
- minSdk: 26

## Included Foundation
- UI text is fully resource-based (values/strings.xml + values-de/strings.xml) with automatic locale selection.
- Basic app shell with bottom navigation (Overview/Bookings/Reports/Sync or Übersicht/Buchungen/Auswertung/Synchronisierung, depending on system language).
- Local JSON state persistence (state.json in app-internal storage).
- ViewModel-based state handling.
- Initial booking creation flow.
- Initial report/year-comparison preview screen.
- Sync tab with SAF folder picker (select/change/clear) and persisted URI permission.
- Read/write of finanz-tracker-sync-latest.json via selected SAF folder URI.
- Auto-load from sync file on app start and auto-save to sync file on data changes.
- Persisted selected sync-folder URI in app state.

## Open In Android Studio
1. Open folder: apps/mobile
2. Let Android Studio run Gradle sync
3. Select an emulator or physical device
4. Run app

## Notes
- Gradle wrapper files are not committed yet; Android Studio can sync using installed/embedded Gradle.
- For two-way sync tools (for example FolderSync), enable the setting to sync deleted files as well; otherwise deleted files can come back after sync.

## Next Steps
- Extract shared business logic from desktop into ../../shared/.
- Implement full multi-user management.
- Implement booking edit/delete/filter and tax-flag behavior.
- Implement full report/export parity.

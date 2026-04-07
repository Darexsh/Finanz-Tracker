# Mobile App (Android)

Android app workspace for Finanz Tracker.

## Current Status
- Android project scaffold is in place.
- Stack: Kotlin + Jetpack Compose + Groovy Gradle (`build.gradle`).
- `applicationId`: `com.darexsh.finanztracker`
- `minSdk`: `26`

## Included Foundation
- UI text is fully resource-based (Android `strings.xml`) with automatic locale selection (`values` + `values-de`).
- Basic app shell with bottom navigation (`Übersicht`, `Buchungen`, `Auswertung`, `Synchronisierung`)
- Current mobile UI language: German
- Local JSON state persistence (`state.json` in app-internal storage)
- ViewModel-based state handling
- Initial booking creation flow
- Initial report/year-comparison preview screen
- Sync screen placeholder for upcoming SAF folder integration

## Open In Android Studio
1. Open folder: `apps/mobile`
2. Let Android Studio run Gradle sync
3. Select an emulator or physical device
4. Run `app`

## Notes
- Gradle wrapper files are not committed yet; Android Studio can sync using installed/embedded Gradle.
- Next implementation step is feature parity with desktop plus folder-based sync via Android SAF.
- For two-way sync tools (e.g., FolderSync), enable the setting to sync deleted files as well; otherwise deleted files can come back after sync.

## Next Steps
- Extract shared business logic from desktop into `../../shared/`
- Implement full multi-user management
- Implement booking edit/delete/filter and tax-flag behavior
- Implement export flows and sync-folder read/write (`finanz-tracker-sync-latest.json`)

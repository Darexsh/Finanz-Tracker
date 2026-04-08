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
- Service layer between ViewModel/UI and repository/data access.
- Centralized sync API contract (JSON keys, tx-type mapping, date normalization) shared across mobile sync parsing/writing.
- Shared finance catalog module for categories/accounts/keyword suggestion and category normalization.
- Multi-user management (select, add, rename, delete with booking cleanup for deleted profile).
- Booking workflow with create, edit, delete (single and multi-select), type/category/account/text filters, and tax-declaration flag handling.
- Dashboard with desktop-like KPI cards (including monthly surplus), top-categories month selector, and monthly trend bars.
- Report screen with desktop-like panel/table structure, selectable year, year-over-year comparison, and monthly income/expense/balance breakdown.
- Report export with desktop-like scope selection and save dialog (CSV/PDF/XLSX for summary, year-comparison, year-bookings, month-bookings).
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
- Release runbook: see `RELEASE.md`.

## Next Steps
- Extract cross-platform business logic from desktop and Android into `../../shared/` where language/runtime allows.

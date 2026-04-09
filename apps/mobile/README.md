# Mobile App (Android)

Android app workspace for Finanz Tracker.

## Current Status
- Android app is final.
- All features listed below are implemented and in place.
- Stack: Kotlin + Jetpack Compose + Groovy Gradle (build.gradle).
- applicationId: com.darexsh.finanztracker
- minSdk: 26

## Implemented Features
- UI text is fully resource-based with automatic locale selection (English and German resources).
- Basic app shell with bottom navigation (Overview/Bookings/Reports/Sync/Settings).
- Local JSON state persistence (state.json in app-internal storage).
- ViewModel-based state handling.
- Service layer between ViewModel/UI and repository/data access.
- Centralized sync API contract (JSON keys, tx-type mapping, date normalization) shared across mobile sync parsing/writing.
- Shared finance catalog module for categories/accounts/keyword suggestion and category normalization.
- Multi-user management (select, add, rename, delete with booking cleanup for deleted profile).
- Booking workflow with create, edit, delete (single and multi-select), type/category/account/text filters, and tax-declaration flag handling.
- Custom category management from the booking form (new/rename/delete category) with desktop-like built-in-category protection, persistence, and sync export.
- Dashboard with desktop-like KPI cards (including monthly surplus), top-categories month selector, and desktop-style monthly cashflow chart (axis/grid + bar overlay style, tap on month bar shows income/expense/balance details).
- Report screen with desktop-like panel/table structure, selectable year, year-over-year comparison, and monthly income/expense/balance breakdown.
- Report export with desktop-like scope selection and save dialog (CSV/PDF/XLSX for summary, year-comparison, year-bookings, month-bookings, tax-declaration-bookings in selected year).
- Settings screen with persistent app options (language, date format, currency, booking sort/suggestions, font size, navigation animation style, start tab, default export format, system app lock, backup export/import).
- Settings include date formats `DD.MM.YYYY`, `YYYY-MM-DD`, `MM/DD/YYYY` and currencies `EUR` / `USD`.
- Settings includes an App-Info button (`i`) with version/details and quick action links (email, social media, Telegram bot, GitHub profile, Buy Me a Coffee).
- App lock uses Android system authentication (biometric/device credential), blocks content with lock overlay, and requires re-authentication before disabling the lock.
- Sync tab with SAF folder picker (select/change/clear) and persisted URI permission.
- Read/write of finanz-tracker-sync-latest.json via selected SAF folder URI.
- Auto-load from sync file on app start, background auto-refresh while app is open, and auto-save to sync file on data changes.
- Persisted selected sync-folder URI in app state.
- Animated app background is optimized for smooth scrolling (color-shift only, step-based update cadence instead of full 60fps path animation).

## Installation / Run
### Prerequisites
- Android Studio (latest stable)
- Android SDK + platform tools
- At least one emulator image or a physical device with USB debugging

### Android Studio
1. Open folder: `apps/mobile`
2. Let Android Studio run Gradle sync
3. Select an emulator or physical device
4. Run app (`app` configuration)

### Optional CLI
```bash
cd "apps/mobile"
./gradlew :app:assembleDebug
```

## Notes
- Gradle wrapper files are not committed yet; Android Studio can sync using installed/embedded Gradle.
- For two-way sync tools (for example FolderSync), enable the setting to sync deleted files as well; otherwise deleted files can come back after sync.
- Release runbook: see `RELEASE.md`.

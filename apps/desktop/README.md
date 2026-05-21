# Desktop App

Desktop application built with Tauri.

## Contents
- Web frontend: `app.js`, `index.html`, `styles.css`
- Tauri backend: `src-tauri/`
- Build/start scripts: `package.json`, `scripts/`
- Local vendor libraries for export features: `vendor/`

## Core Capabilities
- Multi-user finance tracking
- Bookings CRUD with inline validation
- Dashboard, yearly reporting, year-over-year comparison
- Export (PDF, XLSX, CSV) including yearly summaries, year comparison, yearly bookings, monthly bookings, and tax-declaration bookings for the selected year
- Folder-based sync workflow (no OAuth)
- Settings tab with persistent desktop options (language, date format, currency, booking sort direction, start tab, default export format, keep-date-after-save, navigation animation style, category suggestions, extended font-size levels, app lock, backup export/import)
- Dedicated Info tab with app details, app icon, app-style action icons, and quick links (opened in the external browser)

## Performance Notes
- Cached user-bookings and report aggregates
- One-pass dashboard aggregation
- Lazy rendering for large booking tables
- Queued write-batching for persistence

## Typical Commands
- `npm install`
- `npm run prepare:dist`
- `npm run tauri:dev`
- `npm run tauri:build`
- Build artifact aliases after `tauri:build`: `src-tauri/target/release/FinanzTracker_portable.exe` and `src-tauri/target/release/bundle/nsis/FinanzTracker_installer.exe` (via `scripts/rename-artifacts.mjs`).

## App Icon Update
- Place a square source PNG in `src-tauri/` and run:
- `npx @tauri-apps/cli icon ./your-square-icon.png`
- Rebuild the desktop app after icon generation.
## Privacy
- Privacy Policy: [PRIVACY_POLICY.md](PRIVACY_POLICY.md)


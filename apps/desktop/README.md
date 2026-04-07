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
- Export (PDF, XLSX, CSV) including yearly summaries, year comparison, yearly bookings, monthly bookings
- Folder-based sync workflow (no OAuth)

## Performance Notes
- Cached user-bookings and report aggregates
- One-pass dashboard aggregation
- Lazy rendering for large booking tables
- Queued write-batching for persistence

## Typical Commands
- `npm install`
- `npm run prepare:dist`
- `npm run tauri:dev`

## What Can Be Added Next
- Additional profiling/benchmark tooling
- Packaging/release automation

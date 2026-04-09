* * *

<div align="center">

💸 Finanz Tracker
============================

**Cross-platform finance tracking app for Desktop (Tauri) and Android**  
⚡📊🧾💾🔄

![Status](https://img.shields.io/badge/Status-Final-brightgreen) ![License](https://img.shields.io/badge/License-NonCommercial-blue) ![Version](https://img.shields.io/badge/Version-1.0-orange)

![Platform](https://img.shields.io/badge/Platform-Desktop_(Tauri)%20%2B%20Android-blue) ![Storage](https://img.shields.io/badge/Storage-SQLite%20%2B%20JSON-orange)

[![Telegram Bot](https://img.shields.io/badge/Telegram-Bot-2AABEE?logo=telegram&logoColor=white)](https://t.me/darexsh_bot) [![Buy Me a Coffee](https://img.shields.io/badge/Buy%20Me%20a%20Coffee-yellow?logo=buy-me-a-coffee)](https://buymeacoffee.com/darexsh)  
<sub>Check out my bot in Telegram for an easy project overview.<br>If you want to support more projects, you can leave a small donation for a coffee.</sub>

</div>


* * *

✨ Authors
---------

| Name | GitHub | Role | Contact | Contributions |
| --- | --- | --- | --- | --- |
| **[Darexsh by Daniel Sichler](https://github.com/Darexsh)** | [Link](https://github.com/Darexsh?tab=repositories) | Product Owner, UX direction, development | 📧 [E-Mail](mailto:sichler.daniel@gmail.com) | Product vision, architecture, implementation, UX design, feature delivery, testing, and release preparation |

* * *

🤝 Scope & Credits
------------------

This project is an independent personal/open-source finance tracker.

- No affiliation with Google Drive, FolderSync, Syncthing, Tauri, or other third-party tools.
- External sync tools are used only as optional transport layer for folder-based sync.

* * *

🚀 About the Project
===================

**Finanz Tracker** is an app to manage personal finances with multiple separated user profiles, fast booking workflows, dashboard insights, yearly reports, and optional cross-device sync via a local sync folder.

The app is built with Tauri for desktop and includes an Android app (Kotlin + Compose) with SAF-based sync folder selection, sync-file auto-load/auto-save, background sync refresh while app is open, extended bookings workflow (create/edit/delete/filter/tax-flag/date input/custom-category management), and persistent settings (language, date format, currency, sorting/suggestions, appearance, navigation animation style, system app lock, and backup/import).

* * *

✨ Features
----------

* 👥 **Multi-user profiles**: Separate datasets per user.

* 💾 **SQLite persistence**: Main storage in Tauri backend with migration from legacy `localStorage`.

* 🧠 **Smart categories**: Keyword-based auto-categorization plus learning from past booking behavior, including custom category management (add/rename/delete) with built-in-category protection and sync-safe persistence.

* 🧾 **Fast booking workflow**: Double-click row to load, edit, and save with automatic new/update handling.

* ✅ **Inline validation**: Field-level validation with clear feedback and success toasts.

* 🗑️ **Bulk operations**: Multi-select and delete multiple bookings at once.

* 🧷 **Tax declaration flag**: Mark bookings as tax-relevant directly in table; highlighted in yellow.

* 📊 **Dashboard analytics**: KPI cards (`Current Balance`, `Income (Month)`, `Expense (Month)`, `Monthly Surplus`), selectable month for top categories, and monthly income/expense chart with tooltip details.

* 🧮 **Yearly reports**: Monthly yearly summary plus year-over-year comparison (selected year vs. previous year), and flexible export content: yearly summary, year comparison, all bookings in a selected year, all bookings in a selected month, or tax-declaration bookings in the selected year (CSV, XLSX, PDF with Save dialog). Export feedback is shown as toast notifications. CSV includes metadata and totals.

* 🔎 **Live filters**: Filter by month, year, type, category, account, and text with quick reset (desktop and Android parity).

* 📦 **Backup & recovery**: Daily JSON backups + fallback/recovery behavior for storage errors.

* ⚙️ **Desktop settings + info**: Persistent desktop options for language, date format, currency display, booking sort direction, start tab, default export format, keep-date-after-save, navigation animation style, category suggestion toggle, extended font-size presets, desktop app lock, and full backup export/import, plus a dedicated **Info** tab with matching app-style icons and quick links.

* 🔄 **Folder-based sync**: Optional sync using `finanz-tracker-sync-latest.json` in a user-selected folder.

* * *

📥 Installation
---------------

### ⚡ Quick Install (Recommended)

Download the prebuilt app from the release page and run it directly:

* Desktop: use the `.exe` installer
* Android: install the `.apk` file

### Desktop (Tauri)

1. Install **Node.js 20+** and **npm**.

2. Install **Rust** (`rustup` + `cargo`).

3. On Windows install **Visual Studio Build Tools** with C++ workload.

4. Ensure **Microsoft Edge WebView2 Runtime** is installed.

5. Install project dependencies:

```bash
cd "Finanz-Tracker/apps/desktop"
npm install
```

6. Prepare frontend dist:

```bash
npm run prepare:dist
```

7. Start desktop app in dev mode:

```bash
npm run tauri:dev
```

`tauri:dev` now runs `prepare:dist` automatically first, so the app always starts with the latest frontend changes.

Alternative:

```bash
npx tauri dev
```

### Android (Kotlin + Compose)

1. Install **Android Studio** (latest stable) with Android SDK + Emulator.

2. Open project folder:

```text
Finanz-Tracker/apps/mobile
```

3. Let Android Studio complete Gradle sync.

4. Select a device (emulator or physical phone) and run the `app` configuration.

Optional CLI build from project root:

```bash
cd "Finanz-Tracker/apps/mobile"
./gradlew :app:assembleDebug
```

* * *

📝 Usage
--------

1. 👤 **Select or create user profile** in the header.

2. 🧾 **Add bookings** in `Bookings -> New booking`.

3. ✏️ **Update existing booking** via double-click on a table row, edit values, then `Save`.

4. 🧷 **Toggle tax declaration** directly in the booking table checkbox column.

5. 🔎 **Filter bookings live** using month/year/type/category/account/text.

6. 📊 **View dashboard** for KPI and monthly chart.

7. 📁 **Use report export** in `Reports`: choose year (report updates automatically on blur/Enter), export content (Year summary / Year comparison / Year bookings / Month bookings / Tax declaration bookings in year), and format (PDF / XLSX / CSV).

8. 🔄 **Configure sync folder** in `Sync` for optional cross-device workflow.

9. ⚙️ **Adjust desktop settings** in `Settings` (language/date/currency/sorting/start tab/export defaults/suggestions/font size/navigation/app lock/backup).
10. ℹ️ **Use Info tab** for app details and quick actions (email, social links, Telegram bot, profile, support link).

* * *

☁️ Sync Workflow (No OAuth)
---------------------------

The app intentionally uses a **folder-based sync model**.

- The app does not perform Google OAuth login.
- You select a local sync folder once.
- The app reads/writes `finanz-tracker-sync-latest.json` there.
- External tools sync that folder between devices.
- Desktop app performs a short startup retry for sync-restore to handle delayed folder-sync arrival.
- While running, desktop also checks sync updates in the background so changes appear without manual refresh.
- Android app auto-loads sync state on startup and also checks sync updates in the background while running.
- Desktop invalidates render caches and reapplies full UI state on sync-restore so loaded changes are shown immediately without manual F5.
- After sync-restore, desktop skips one immediate sync write-back to reduce timestamp churn/conflicts with external folder sync tools.

Typical setup:

- Desktop: Google Drive Desktop, Syncthing, or similar
- Android: FolderSync, Syncthing, or similar
- If you use two-way sync (for example in FolderSync), enable the option to also sync deleted files. Otherwise, deleted files can reappear on the next sync cycle.

* * *

🧰 Troubleshooting
-----------------

* **`tauri` command not found**: Run `npm install` in `apps/desktop`, then retry `npm run tauri:dev`.

* **`failed to run 'cargo metadata' ... program not found`**: Rust/Cargo missing in PATH. Reinstall Rust and restart terminal/PC.

* **UI changes not visible**: Run `npm run prepare:dist` and restart dev app.

* **Sync file not found on second device**: Verify external sync tool has completed folder sync first.

* * *

⚙️ Technical Details
--------------------

* Tauri desktop shell with web frontend.

* Backend persistence via SQLite commands (`db_load_state` / `db_save_state`).

* Daily JSON backups and cleanup policy.

* Optional sync commands for folder-based backup/restore.

* Current data model supports separated users, bookings, and custom categories.

* Runtime performance optimizations: cached user-bookings and report aggregates, one-pass dashboard aggregation, lazy-rendering for large booking lists (incremental loading), and queued write-batching for persistence.

* * *

📜 License
----------

This project is licensed under the **Non-Commercial Software License (MIT-style) v1.0** and was developed as a personal/educational project. You are free to use, modify, and distribute the code for **non-commercial purposes only**, and must credit the author:

**Copyright (c) 2026 Darexsh by Daniel Sichler**

Please include the following notice with any use or distribution:

> Developed by Daniel Sichler aka Darexsh. Licensed under the Non-Commercial Software License (MIT-style) v1.0. See `LICENSE` for details.

The full license is available in the [LICENSE](LICENSE) file.

* * *

<div align="center"> <sub>Created with ❤️ by Daniel Sichler</sub> </div>

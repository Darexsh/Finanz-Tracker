* * *

<div align="center">

💸 Finanz Tracker
============================

**Modern desktop finance tracking app with Tauri (Web UI + native shell)**  
⚡📊🧾💾🔄

![Projekt-Status](https://img.shields.io/badge/Status-Aktiv-brightgreen) ![License](https://img.shields.io/badge/License-NonCommercial-blue) ![Version](https://img.shields.io/badge/Version-1.0-orange)

![Platform](https://img.shields.io/badge/Platform-Desktop_(Tauri)-blue) ![Storage](https://img.shields.io/badge/Storage-SQLite-orange)

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

**Finanz Tracker** is a desktop app to manage personal finances with multiple separated user profiles, fast booking workflows, dashboard insights, yearly reports, and optional cross-device sync via a local sync folder.

The app is built with Tauri and currently optimized for desktop usage (Windows-first), while Android is planned after desktop stabilization.

* * *

✨ Features
----------

* 👥 **Multi-user profiles**: Separate datasets per user.

* 💾 **SQLite persistence**: Main storage in Tauri backend with migration from legacy `localStorage`.

* 🧠 **Smart categories**: Keyword-based auto-categorization plus learning from past booking behavior.

* 🧾 **Fast booking workflow**: Double-click row to load, edit, and save with automatic new/update handling.

* ✅ **Inline validation**: Field-level validation with clear feedback and success toasts.

* 🗑️ **Bulk operations**: Multi-select and delete multiple bookings at once.

* 🧷 **Tax declaration flag**: Mark bookings as tax-relevant directly in table; highlighted in yellow.

* 📊 **Dashboard analytics**: KPI cards, top categories, and monthly income/expense chart with tooltip details.

* 🧮 **Yearly reports**: Monthly yearly summary plus year-over-year comparison (selected year vs. previous year), and flexible export content: yearly summary, year comparison, all bookings in a selected year, or all bookings in a selected month (CSV, XLSX, PDF with Save dialog). Export feedback is shown as toast notifications. CSV includes metadata and totals.

* 🔎 **Live filters**: Filter by month, year, type, category, account, and text with quick reset.

* 📦 **Backup & recovery**: Daily JSON backups + fallback/recovery behavior for storage errors.

* 🔄 **Folder-based sync**: Optional sync using `finanz-tracker-sync-latest.json` in a user-selected folder.

* * *

📥 Installation
---------------

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

Alternative:

```bash
npx tauri dev
```

* * *

📝 Usage
--------

1. 👤 **Select or create user profile** in the header.

2. 🧾 **Add bookings** in `Buchungen → Neue Buchung`.

3. ✏️ **Update existing booking** via double-click on a table row, edit values, then `Speichern`.

4. 🧷 **Toggle tax declaration** directly in the booking table checkbox column.

5. 🔎 **Filter bookings live** using month/year/type/category/account/text.

6. 📊 **View dashboard** for KPI and monthly chart.

7. 📁 **Use report export** in `Auswertung`: choose year (report updates automatically on blur/Enter), export content (Jahresübersicht / Jahresvergleich / Jahresbuchungen / Monatsbuchungen), and format (PDF / XLSX / CSV).

8. 🔄 **Configure sync folder** in `Synchronisierung` for optional cross-device workflow.

* * *

☁️ Sync Workflow (No OAuth)
---------------------------

The app intentionally uses a **folder-based sync model**.

- The app does not perform Google OAuth login.
- You select a local sync folder once.
- The app reads/writes `finanz-tracker-sync-latest.json` there.
- External tools sync that folder between devices.

Typical setup:

- Desktop: Google Drive Desktop, Syncthing, or similar
- Android: FolderSync, Syncthing, or similar

* * *

📂 Project Structure
--------------------

```text
Finanz-Tracker/
├─ apps/
│  ├─ desktop/             # Tauri desktop app (active)
│  └─ mobile/              # Android placeholder (planned)
├─ shared/
│  ├─ domain/              # shared domain logic (planned)
│  └─ utils/               # shared utilities (planned)
├─ docs/                   # optional docs
├─ TODO.md                 # roadmap and task tracking
└─ Ausgaben im Jahr.xlsx   # original source spreadsheet
```

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

🧭 Scope
--------

* Desktop-first product focus until stability goals are finished.

* Android app implementation is intentionally postponed to later roadmap phases.

* Sync conflict handling is intentionally simple for non-parallel usage.

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

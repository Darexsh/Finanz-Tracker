# Privacy Policy – Finanz Tracker (Mobile / Android)

Last updated: 2026-05-21

## 1. Controller
Daniel Sichler (Darexsh)  
Email: sichler.daniel@gmail.com

## 2. Scope
This policy applies to the Android app in `apps/mobile` with applicationId `com.darexsh.finanztracker`.

## 3. Data processed by the app
Finanz Tracker processes and stores finance app data you create in the app, including for example:
- profiles/users
- bookings (date, description, amount, category, account, note, tax flag)
- app settings (for example language, format, UI preferences)
- optional sync folder reference/URI for configured folder sync

## 4. Local storage
Core app data is stored locally on the device in app-internal storage (`state.json`).

## 5. Backup / export / import and optional folder sync
The app supports manual backup export/import and optional folder-based synchronization (when configured by the user via Android SAF folder selection).

When sync is configured, data may be read from/written to a user-selected folder URI (for example a folder mirrored by a third-party sync app).

## 6. Permissions
Based on current implementation, the app uses Android storage/document access mechanisms (SAF) for user-selected folder access in sync/backup flows.

The Android manifest currently sets `android:allowBackup="false"` for system backup of app data.

## 7. Analytics, tracking, and crash reporting
The current mobile app module does not include third-party analytics SDKs or third-party crash-reporting SDKs (such as Firebase Analytics/Crashlytics, Sentry, Mixpanel, etc.).

## 8. Network and external links
The App-Info dialog can open external links on user action, including:
- email (`mailto:`)
- Linktree
- Telegram
- GitHub
- Buy Me a Coffee

When you open external links, the privacy policies of those third-party services apply.

### Third-party services referenced by links
- GitHub: https://docs.github.com/en/site-policy/privacy-policies/github-privacy-statement
- Telegram: https://telegram.org/privacy
- Linktree: https://linktr.ee/s/privacy
- Buy Me a Coffee: https://www.buymeacoffee.com/privacy-policy

## 9. Data sharing
Based on the current implementation, the app does not automatically send your finance data to the developer.

If you use external sync tools/services, data processing by those tools/services is governed by their own policies.

## 10. Backup/sync disclaimer
Backup/export/import/sync features are provided without guarantee of successful restore/sync in every environment.

You are responsible for verifying data integrity and compatibility of backups/sync setups before relying on them.

## 11. Your choices
- You can use the app locally without enabling folder sync.
- You can choose whether to export/import data.
- You can remove/replace configured sync folders.
- You can avoid opening external links.

## 12. Changes to this policy
This policy may be updated if app functionality or legal requirements change.


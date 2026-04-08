# Android Release Process

This runbook defines a repeatable release flow for `apps/mobile`.

## 1. Pre-checks
- Update version in `app/build.gradle`:
  - `versionCode` increase by 1
  - `versionName` set to new semantic version
- Update root `CHANGELOG.md` with release notes.
- Run app smoke tests:
  - Multi-user add/rename/delete
  - Booking create/edit/delete/filter/tax
  - Sync Android <-> Desktop
  - Report export (CSV/PDF/XLSX)

## 2. Build artifacts
- Debug APK:
  - `./gradlew :app:assembleDebug`
- Release APK:
  - `./gradlew :app:assembleRelease`

## 3. Signing setup (Release)
- Create `keystore.properties` in `apps/mobile/` (not committed):
  - `storeFile=...`
  - `storePassword=...`
  - `keyAlias=...`
  - `keyPassword=...`
- Configure `signingConfigs.release` in Gradle to load these values.

## 4. Verify output
- Install release artifact on a clean device/emulator.
- Verify startup, navigation tabs, and sync permission flow.
- Validate export files open correctly on external apps.

## 5. Publish checklist
- Tag release in git (`vX.Y.Z`).
- Attach APK/AAB to release entry.
- Link changelog section.

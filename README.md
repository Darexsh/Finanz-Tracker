# Finanz Tracker

Modernisierte Finanz-Desktop-App mit Tauri (Web-UI + native Desktop-Hülle).

Diese README ist die zentrale und einzige Projektdokumentation.

## Projektstatus

- `apps/desktop/`: aktuelle Desktop-Basis (Tauri)
- `apps/mobile/`: Platzhalter für die spätere Android-App
- `shared/`: gemeinsame Module für Desktop + Android (Vorbereitung)
- `docs/`: zusätzliche Projekt-Dokumente (optional)

## Projektstruktur

```text
Finanz Tracker/
├─ apps/
│  ├─ desktop/             # Desktop-App (Tauri)
│  └─ mobile/              # Android-App (Vorbereitung)
├─ shared/
│  ├─ domain/              # gemeinsame Fachlogik (Vorbereitung)
│  └─ utils/               # gemeinsame Hilfsfunktionen (Vorbereitung)
├─ docs/                   # zusätzliche Doku (optional)
└─ Ausgaben im Jahr.xlsx   # Ursprungs-Excel
```

## Aktueller Funktionsstand (Desktop)

### Benutzerverwaltung

- Mehrere Benutzerprofile, strikt getrennte Buchungen je Benutzer
- Letzten aktiven Benutzer beim Start automatisch laden
- Standardbenutzer wird nur erstellt, wenn noch kein Benutzer existiert
- Benutzer anlegen, umbenennen, löschen
- Sicherheitsdialog beim Löschen von Benutzern mit vorhandenen Buchungen

### Buchungen

- Buchungen mit vollem Datum im Format `TT.MM.JJJJ`
- Auto-Kategorisierung anhand Beschreibung (Keyword + lernfähige Regeln aus bisherigen Buchungen), manuell übersteuerbar
- Benutzerdefinierte Kategorien direkt in der GUI anlegen, umbenennen, löschen
- Buchung per Doppelklick in Formular laden, ändern und mit "Speichern" sichern (automatisch Neu/Update)
- Mehrfachauswahl und Sammellöschung von Buchungen
- Sortierung: neuere Daten oben; bei gleichem Datum zuletzt angelegte Buchung zuerst

### Filter und Auswertung

- Filter nach Monat, Jahr, Typ, Kategorie (inkl. benutzerdefinierter Kategorien), Konto und Textsuche
- Jahresauswertung mit Monatszeilen (Einnahmen, Ausgaben, Saldo)
- CSV-Export der Jahresauswertung
- CSV-Ziel: primär Download-Ordner, Fallback Dokumente

### Dashboard

- KPI-Karten (Saldo, Monatseinnahmen, Monatsausgaben, Monatsüberschuss)
- Top-Kategorien für Ausgaben im aktuellen Monat
- Monatsbalkenchart (aktuelles Jahr):
  - nur positive Richtung
  - grün = Einnahmen
  - rot = Ausgabenanteil im Balken
  - Hover/Klick zeigt Monatsdetails (Einnahmen, Ausgaben, Saldo)
  - dynamische Y-Achsen-Legende

### Persistenz, Recovery und Backup

- Primäre Persistenz über SQLite im Tauri-Backend
- Migration bestehender Browser-Daten (`localStorage`) nach SQLite beim Start
- Recovery-Logik bei DB-Lock/Schreibfehlern/beschädigten Daten
- Lokaler Fallback bei SQLite-Fehlern
- Daily-Backup als JSON im App-Datenordner unter `backups/`
- Dateimuster: `state-backup-YYYY-MM-DD.json`
- Automatisches Aufräumen: es bleiben die neuesten 60 Backups

## Cloud-/Sync-Workflow (final)

### Entscheidung

- Kein OAuth in der App.
- Kein Google-Login innerhalb von Finanz Tracker.
- Sync läuft über externe Sync-Tools:
  - Windows: Google Drive für Desktop
  - Android: FolderSync

### Warum dieser Ansatz

- Kein OAuth-Review/Verifizierung für die App nötig
- Kein Token-Handling innerhalb der App
- Einfachere öffentliche Veröffentlichung (Open Source/GitHub)
- Gleiches Datenprinzip für Desktop und spätere Android-App

### Zielbild

- App arbeitet nur mit einem lokalen Sync-Ordner.
- Dieser Ordner wird von Google Drive Desktop in die Cloud synchronisiert.
- Android synchronisiert denselben Drive-Ordner per FolderSync auf lokalen Gerätespeicher.
- Die spätere Android-App nutzt denselben lokalen Ordner-Ansatz.

### Konkreter Setup-Ablauf

1. In Google Drive einen Ordner erstellen, z. B. `FinanzTrackerSync`.
2. Auf Windows `Google Drive für Desktop` installieren und anmelden.
3. Den Drive-Ordner lokal verfügbar machen (Spiegelung/Offline verfügbar).
4. In der Desktop-App im Tab `Synchronisierung` den lokalen Sync-Ordnerpfad auswählen (wird automatisch gespeichert).
5. Die App lädt beim Start automatisch aus dem Sync-Ordner (falls Datei vorhanden).
6. Änderungen werden automatisch in die Sync-Datei zurückgeschrieben (Overwrite).
7. Auf Android FolderSync einrichten:
   - Konto: Google Drive
   - Remote-Ordner: `FinanzTrackerSync`
   - Lokaler Ordner: z. B. `Android/data/.../FinanzTrackerSync`
   - Sync-Richtung: Two-way
8. Optional: in FolderSync Zeitplan für regelmäßigen Sync aktivieren.

### App-Verhalten (Synchronisierung)

- Eigener Tab: `Synchronisierung`
- Ordnerauswahl per `Durchsuchen` (kein manuelles Pfad-Raten nötig)
- Live-Anzeige: Sync-Status, letzte Sync-Sicherung, letzte Wiederherstellung
- Sync-Datei: `finanz-tracker-sync-latest.json`

### Konflikt-Hinweis

- Bei gleichzeitigen Änderungen auf zwei Geräten können Konflikte entstehen.
- Aktuell gilt praktisch: `finanz-tracker-sync-latest.json` ist die führende Datei (wird überschrieben).
- Empfehlung: vor Gerätewechsel kurz manuell synchronisieren.

## Voraussetzungen

### Für Desktop-Entwicklung (Tauri)

- Node.js 20+
- npm
- Rust (rustup + cargo)
- (Windows) Visual Studio Build Tools mit C++ Workload
- (Windows) Microsoft Edge WebView2 Runtime

## Installation

### A) Ohne CLI-Installer (GUI-Weg, Windows)

1. Node.js LTS installieren: https://nodejs.org/
2. Rust installieren: https://www.rust-lang.org/tools/install  
   (Download `rustup-init.exe`, ausführen, Standardinstallation)
3. Visual Studio Build Tools installieren: https://visualstudio.microsoft.com/de/visual-cpp-build-tools/  
   Workload: `Desktop development with C++` (inkl. MSVC + Windows SDK)
4. WebView2 Runtime installieren (falls nicht vorhanden): https://developer.microsoft.com/microsoft-edge/webview2/

### B) Projektabhängigkeiten installieren

```bash
cd "Finanz Tracker/apps/desktop"
npm install
```

## Entwicklung und Tests (vor Release-Build)

### 1) Frontend vorbereiten

```bash
npm run prepare:dist
```

### 2) Desktop-App im Dev-Modus starten

```bash
npx tauri dev
```

Alternativ:

```bash
npm run tauri:dev
```

## Release-Build

```bash
npm run tauri:build
```

Output liegt danach in:

- `apps/desktop/src-tauri/target/release/bundle/`
- Linux: `.AppImage`, `.deb`, `.rpm`
- Windows: `.msi` (oder je nach Bundle-Config weitere Installer)

## Hinweise

- Android folgt als nächster Schritt nach Desktop-Stabilisierung
- Für größere UI-Änderungen: erst `tauri dev` testen, dann `tauri build`

## Roadmap

- Vollständige Aufgabenliste: `TODO.md`

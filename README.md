# Finanz Tracker

Modernisierte Finanz-Desktop-App mit Tauri (Web-UI + native Desktop-Hülle).

## Projektstatus

- `universal_app/`: aktuelle Desktop-Basis (Tauri)
- `python_gui_old/`: alte/refaktorierte Python-Tkinter-Version (Legacy)

## Projektstruktur

```text
Finanz Tracker/
├─ universal_app/          # Neue Desktop-App (Tauri)
├─ python_gui_old/         # Alte Python-GUI (Legacy)
├─ finanzen.db             # Lokale Datenbank (Bestandsdatei)
├─ settings.json           # Einstellungen (Bestandsdatei)
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
- Auto-Kategorisierung anhand Beschreibung (z. B. Keyword-Mapping), manuell übersteuerbar
- Buchung per Doppelklick in Formular laden, ändern und mit "Speichern" sichern (automatisch Neu/Update)
- Mehrfachauswahl und Sammellöschung von Buchungen (Einzellöschung über Formular entfernt)
- Sortierung: neuere Daten oben; bei gleichem Datum zuletzt angelegte Buchung zuerst

### Filter und Auswertung

- Filter nach Monat, Jahr, Typ, Kategorie, Konto und Textsuche
- Jahresauswertung mit Monatszeilen (Einnahmen, Ausgaben, Saldo)
- CSV-Export der Jahresauswertung
- CSV-Ziel: primär Download-Ordner, Fallback Dokumente
- Auswertungsbereich im Report-Tab visuell entzerrt (mehr Abstand zu KPI-Karten)

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
cd "Finanz Tracker/universal_app"
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

Alternativ (wenn CLI lokal korrekt aufgelöst wird):

```bash
npm run tauri:dev
```

## Release-Build

```bash
npm run tauri:build
```

Output liegt danach in:

- `universal_app/src-tauri/target/release/bundle/`
- Linux: `.AppImage`, `.deb`, `.rpm`
- Windows: `.msi` (oder je nach Bundle-Config weitere Installer)

## Alte Python-GUI (Legacy)

Die bisherigen Python-Dateien liegen jetzt in:

- `python_gui_old/`

Start (optional, Legacy):

```bash
cd "Finanz Tracker/python_gui_old"
python finance_tracker.py
```

## Hinweise

- Keine Cloud-Synchronisierung implementiert (lokale Nutzung)
- Android folgt als nächster Schritt nach Desktop-Stabilisierung
- Für größere UI-Änderungen bitte zuerst `tauri dev` testen, dann erst `tauri build`

## Roadmap

- Vollständige Aufgabenliste: `TODO.md`

# Finanz Tracker Desktop (Tauri)

Dieser Ordner enthält die aktuelle Desktop-App.

## Schnellstart

```bash
npm install
npm run prepare:dist
npx tauri dev
```

## Build

```bash
npm run tauri:build
```

Artefakte liegen anschließend unter:

- `src-tauri/target/release/bundle/`

## Aktueller Stand (Kurzüberblick)

- Multi-User mit getrennten Daten und Last-User-Load
- Startet maximiert (Desktop-Fenster)
- Buchungen mit `TT.MM.JJJJ`
- Doppelklick auf Buchung lädt den Datensatz ins Formular; Speichern erkennt automatisch Neu/Update
- Mehrfachlöschung in Buchungen (Checkboxen + "Ausgewählte löschen", kein separater Formular-Löschen-Button)
- Filter: Monat/Jahr/Typ/Kategorie/Konto/Suche
- Dashboard-Balkenchart mit Hover/Klick-Details und dynamischer Y-Achse
- Jahresreport + CSV-Export
- Report-Spacing optimiert (mehr Abstand zwischen Jahresauswertung und KPI-Karten)
- SQLite-Persistenz mit Recovery-Fallback
- Daily JSON-Backup unter `backups/`

## Backup und Recovery

- SQLite ist die primäre Persistenz.
- Bei Schreib-/Lock-Fehlern greift die App auf lokalen Fallback zurück.
- Ein Daily-Backup wird als JSON im App-Datenordner unter `backups/` erstellt.
- Dateiname: `state-backup-YYYY-MM-DD.json`.
- Es bleiben automatisch die neuesten 60 Backup-Dateien erhalten.

## Vollständige Dokumentation

Die komplette Projekt-Dokumentation ist im Root:

- `../README.md`

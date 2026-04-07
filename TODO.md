# TODO - Finanz Tracker

## 1. Kritisch (als Nächstes)

- [x] Persistenz von `localStorage` auf SQLite im Tauri-Backend umstellen.
- [x] Datenmigration bauen: bestehende `localStorage`-Daten beim ersten Start sicher in SQLite übernehmen.
- [x] Fehler- und Recovery-Logik ergänzen (DB-locked, Schreibfehler, beschädigte Daten).
- [x] Autosave/Backup-Mechanismus lokal ergänzen (z. B. tägliche JSON- oder SQLite-Kopie).

## 2. Cloud-/Sync-Anbindung

- [x] Cloud-Sync-Konzept definieren: One-way Backup vs. Two-way Sync.
- [x] OAuth-/Google-Login wieder entfernt (Entscheidung: kein OAuth-Zwang).
- [x] Sync-Ordner-MVP in Desktop-App: Ordner speichern + manuelles Backup/Restore.
- [x] Automatisches Laden der Sync-Datei beim App-Start (wenn vorhanden).
- [x] Dokumentierte Einrichtung von Google Drive Desktop + FolderSync als offizieller Nutzer-Workflow.
- [x] Synchronisierung in eigenem Tab inkl. Statusanzeigen (letzte Sicherung/Wiederherstellung).
- [x] Automatische Sync-Schreibung bei Änderungen (Desktop-App, Overwrite auf latest-Datei).
- [x] Hintergrund-Sync-Timer bewusst nicht umgesetzt (für aktuellen Use-Case nicht nötig).
- [x] Konfliktstrategie bewusst einfach gehalten (Use-Case: keine parallele Nutzung).
- [x] Verschlüsselung aktuell bewusst nicht nötig (privater Use-Case, externe Sync-Tools).
- [x] Sync-Status ist für den Use-Case ausreichend (Status + letzte Sicherung/Wiederherstellung).

## 3. Kategorien und Fachlogik

- [x] Kategorien aus `Ausgaben im Jahr.xlsx` systematisch übernehmen.
- [x] Deduplizierung und Normalisierung der Kategorien (z. B. Synonyme zusammenführen).
- [x] Keyword-Mapping (`KEYWORD_MAP`) deutlich erweitern auf Basis der Excel-Daten.
- [x] Benutzerdefinierte Kategorien erlauben (anlegen/umbenennen/löschen).
- [x] Kategorie-Regeln lernfähig machen (Beschreibung -> Kategorie aus Nutzungsverhalten).

## 4. UX und Bedienung

- [x] Dashboard um eine Graphen-Darstellung wie in der früheren GUI erweitern (Monatsverlauf/Balkenchart).
- [x] Dialogsystem weiter verbessern (validierungsnahe Hinweise, Success-Feedback als Toast).
- [x] Tabellen-Usability verbessern (Doppelklick lädt Buchung, Mehrfachauswahl, klare Auswahlzustände).
- [x] Form-Validierung visuell pro Feld anzeigen (nicht nur globaler Dialog).
- [x] Filterleiste ausbauen (Jahr + kompakte Standardfilter ohne Preset-/Speicher-Overhead).
- [x] Leere Zustände und Hilfetexte verbessern (Onboarding für Erstnutzer).
- [x] Steuer-Checkbox pro Buchung ergänzt + gelbe Hervorhebung in der Tabelle.
- [x] Zeilenauswahl in Buchungen per Klick außerhalb wieder aufheben.
- [x] Layout von "Neue Buchung" neu angeordnet (kompakter und übersichtlicher).

## 5. Reports und Export

- [ ] CSV-Export mit Zielpfad-Auswahl (statt nur Downloads/Dokumente-Fallback).
- [ ] Exportformate erweitern (XLSX, PDF).
- [ ] Jahresvergleich (aktuelles Jahr vs. Vorjahr).
- [ ] Budget-vs-Ist-Bericht pro Kategorie.
- [ ] Wiederkehrende Ausgaben separat auswerten.

## 6. Performance

- [ ] Große Buchungslisten virtualisieren (UI bleibt schnell bei vielen Datensätzen).
- [ ] Aggregationen cachen (Dashboard/Reports) für schnellere Wechsel.
- [ ] Schreibzugriffe bündeln (Batching), wo sinnvoll.

## 7. Android-Vorbereitung (nach Desktop-Stabilisierung)

- [ ] Service-Schicht entkoppeln (UI vs. Datenzugriff klar trennen).
- [ ] Datenmodell und Validierung in gemeinsam nutzbare Module überführen.
- [ ] API-Verträge für mobile Nutzung festziehen.
- [ ] Build- und Release-Prozess für Android definieren.

## 8. Release/Operations

- [ ] Versionierung und Changelog-Prozess einführen.
- [ ] Windows-Installer-Flow finalisieren (MSI/Signierung optional).
- [ ] Standard-Backup- und Restore-Anleitung in Doku ergänzen.
- [ ] Troubleshooting-Guide (häufige Fehler + Fix).

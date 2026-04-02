# TODO - Finanz Tracker

## 1. Kritisch (als Nächstes)

- [x] Persistenz von `localStorage` auf SQLite im Tauri-Backend umstellen.
- [x] Datenmigration bauen: bestehende `localStorage`-Daten beim ersten Start sicher in SQLite übernehmen.
- [x] Fehler- und Recovery-Logik ergänzen (DB-locked, Schreibfehler, beschädigte Daten).
- [x] Autosave/Backup-Mechanismus lokal ergänzen (z. B. tägliche JSON- oder SQLite-Kopie).

## 2. Cloud-Anbindung

- [ ] Cloud-Sync-Konzept definieren: One-way Backup vs. Two-way Sync.
- [ ] Google-Drive-Integration vorbereiten (OAuth2 Login + Token-Speicherung).
- [ ] Export/Import der Datenbank in Google Drive (MVP: manuelles Backup/Restore).
- [ ] Konfliktstrategie definieren (neueste Version, Merge-Regeln, manuelle Konfliktauflösung).
- [ ] Verschlüsselung vor Upload (mindestens optional, besser Standard).
- [ ] Sync-Status in UI anzeigen (letzter Upload, letzter Download, Fehlerstatus).

## 3. Kategorien und Fachlogik

- [ ] Kategorien aus `Ausgaben im Jahr.xlsx` systematisch übernehmen.
- [ ] Deduplizierung und Normalisierung der Kategorien (z. B. Synonyme zusammenführen).
- [ ] Keyword-Mapping (`KEYWORD_MAP`) deutlich erweitern auf Basis der Excel-Daten.
- [ ] Benutzerdefinierte Kategorien erlauben (anlegen/umbenennen/löschen).
- [ ] Kategorie-Regeln lernfähig machen (Beschreibung -> Kategorie aus Nutzungsverhalten).
- [ ] Kategorie-Statistiken erweitern (monatlich/jährlich, Top-Kategorien, Trends).

## 4. UX und Bedienung

- [x] Dashboard um eine Graphen-Darstellung wie in der Python-GUI erweitern (Monatsverlauf/Balkenchart).
- [ ] Dialogsystem weiter verbessern (validierungsnahe Hinweise, Success-Feedback als Toast).
- [x] Tabellen-Usability verbessern (Doppelklick lädt Buchung, Mehrfachauswahl, klare Auswahlzustände).
- [ ] Form-Validierung visuell pro Feld anzeigen (nicht nur globaler Dialog).
- [ ] Filterleiste ausbauen (Jahr ist drin, als Nächstes Presets und gespeicherte Filter).
- [ ] Leere Zustände und Hilfetexte verbessern (Onboarding für Erstnutzer).

## 5. Reports und Export

- [ ] CSV-Export mit Zielpfad-Auswahl (statt nur Downloads/Dokumente-Fallback).
- [ ] Exportformate erweitern (XLSX, PDF).
- [ ] Jahresvergleich (aktuelles Jahr vs. Vorjahr).
- [ ] Budget-vs-Ist-Bericht pro Kategorie.
- [ ] Wiederkehrende Ausgaben separat auswerten.

## 6. Multi-User und Rechte

- [ ] Optionaler PIN-Schutz pro Benutzerprofil.
- [ ] Benutzer-Archiv statt Hard-Delete (optional).
- [ ] Rollenmodell vorbereiten (z. B. Admin/Readonly für Familienmodus).

## 7. Qualität und Sicherheit

- [ ] Unit-Tests für Kernlogik (Parsing, Filter, Kategorien, Reports).
- [ ] Integrations-Tests für Tauri-Commands (Export, DB-Zugriffe).
- [ ] E2E-Smoke-Tests für Hauptflows (Benutzer, Buchungen, Report, Export).
- [ ] Logging-Strategie für Produktion ergänzen (rotierende Logs + Fehlercodes).

## 8. Performance

- [ ] Große Buchungslisten virtualisieren (UI bleibt schnell bei vielen Datensätzen).
- [ ] Aggregationen cachen (Dashboard/Reports) für schnellere Wechsel.
- [ ] Schreibzugriffe bündeln (Batching), wo sinnvoll.

## 9. Android-Vorbereitung (nach Desktop-Stabilisierung)

- [ ] Service-Schicht entkoppeln (UI vs. Datenzugriff klar trennen).
- [ ] Datenmodell und Validierung in gemeinsam nutzbare Module überführen.
- [ ] API-Verträge für mobile Nutzung festziehen.
- [ ] Build- und Release-Prozess für Android definieren.

## 10. Release/Operations

- [ ] Versionierung und Changelog-Prozess einführen.
- [ ] Windows-Installer-Flow finalisieren (MSI/Signierung optional).
- [ ] Standard-Backup- und Restore-Anleitung in Doku ergänzen.
- [ ] Troubleshooting-Guide (häufige Fehler + Fix).

## Backlog (optional)

- [ ] OCR-Belegimport (Foto/PDF -> Buchungsvorschlag).
- [ ] Regeln für automatische Kontozuordnung.
- [ ] Erinnerungen für wiederkehrende Buchungen.
- [ ] Mehrsprachigkeit (DE/EN).

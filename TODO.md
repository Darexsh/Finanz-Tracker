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
- [x] Desktop-Startlogik für Auto-Restore mit erweitertem Retry + verzögertem Fallback-Versuch ergänzt (verhindert zweiten App-Start bei verzögertem Ordner-Sync).
- [x] Desktop-Live-Aktualisierung ergänzt: periodischer Hintergrund-Restore im laufenden Betrieb (kein manuelles F5 nötig).
- [x] Desktop-Rendercache beim Sync-Restore invalidiert (UI zeigt neue Daten sofort ohne F5).
- [x] Sync-Restore wendet vollständigen UI-State neu an (inkl. Tabellen-/Chart-State), damit neue Einträge ohne manuelles F5 sichtbar sind.
- [x] Dev-Start abgesichert: `tauri:dev` und `beforeDevCommand` führen automatisch `prepare:dist` aus (kein veraltetes `web-dist` mehr).
- [x] Direktes Zurückschreiben nach Sync-Restore unterdrückt (reduziert ModifiedTimeDifference/Sync-Konflikte durch Timestamp-Churn).

## 3. Kategorien und Fachlogik

- [x] Kategorien aus `Ausgaben im Jahr.xlsx` systematisch übernehmen.
- [x] Deduplizierung und Normalisierung der Kategorien (z. B. Synonyme zusammenführen).
- [x] Keyword-Mapping (`KEYWORD_MAP`) deutlich erweitern auf Basis der Excel-Daten.
- [x] Benutzerdefinierte Kategorien erlauben (anlegen/umbenennen/löschen).
- [x] Kategorie-Regeln lernfähig machen (Beschreibung -> Kategorie aus Nutzungsverhalten).

## 4. UX und Bedienung

- [x] In Auswertung: automatisches Aktualisieren bei Jahreswechsel (on blur/Enter), kein separater "Auswertung laden"-Button mehr.
- [x] Export-Rückmeldungen als Toast statt Dialog (Erfolg/Fehler/Abbruch).
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

- [x] Jahresvergleich als Export-Inhalt (CSV/XLSX/PDF).
- [x] CSV-Export mit Zielpfad-Auswahl (statt nur Downloads/Dokumente-Fallback).
- [x] Exportformate erweitern (XLSX, PDF).
- [x] Export-Inhalt erweitern: Jahresübersicht sowie vollständige Buchungslisten für einzelnes Jahr oder einzelnen Monat (CSV/XLSX/PDF).
- [x] Jahresvergleich (aktuelles Jahr vs. Vorjahr).

## 6. Performance

- [x] Große Buchungslisten virtualisieren (UI bleibt schnell bei vielen Datensätzen).
- [x] Aggregationen cachen (Dashboard/Reports) für schnellere Wechsel.
- [x] Schreibzugriffe bündeln (Batching), wo sinnvoll.

## 7. Android - Funktionalität zuerst (vor UI-Parity)

- [x] Android-Projektgrundlage erstellt (Kotlin, Compose, Gradle Groovy, minSdk 26, applicationId com.darexsh.finanztracker).
- [x] Mobile UI-Basis auf Deutsch lokalisiert (Navigation + Screen-Texte).
- [x] Android-Lokalisierung via resources umgesetzt (`values` + `values-de`, automatische Systemsprache).
- [x] SAF-Ordnerauswahl für Sync-Ordner implementiert (persistente URI-Permissions).
- [x] `finanz-tracker-sync-latest.json` auf Android lesen/schreiben (gleicher Dateiname/Schema wie Desktop).
- [x] Auto-Load beim Start + Auto-Save bei Änderungen auf Android analog Desktop umgesetzt.
- [ ] Service-Schicht entkoppeln (UI vs. Datenzugriff klar trennen).
- [ ] Datenmodell und Validierung in gemeinsam nutzbare Module überführen.
- [ ] Buchungen auf Desktop-Niveau bringen (Bearbeiten, Löschen, Mehrfachauswahl, Filter).
- [ ] Auswertung auf Desktop-Niveau bringen (inkl. Jahresvergleich-Logik).
- [ ] API-Verträge für mobile Nutzung festziehen.
- [ ] Build- und Release-Prozess für Android definieren.

## 8. Android - UI-Parity (nach Funktionalität)

- [ ] Design-Tokens von Desktop auf Android übertragen (Farben, Typografie, Spacing).
- [ ] Formular-/Listenlayout visuell an Desktop angleichen.
- [ ] Dashboard visuell angleichen (inkl. Graphen-Darstellung im gleichen Stil).
- [ ] Interaktionsdetails angleichen (Toasts, Fokus, Zustandsfeedback, Empty States).

## 9. Release/Operations

- [ ] Versionierung und Changelog-Prozess einführen.
- [ ] Windows-Installer-Flow finalisieren (MSI/Signierung optional).
- [ ] Standard-Backup- und Restore-Anleitung in Doku ergänzen.
- [ ] Troubleshooting-Guide (häufige Fehler + Fix).

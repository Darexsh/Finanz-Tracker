export function createReportBuilders(deps) {
  function reportExportMoney(value) {
    return Number(value || 0).toFixed(2).replace(".", ",");
  }

  function reportExportPercent(value) {
    if (value === null || value === undefined || Number.isNaN(Number(value))) return "-";
    const num = Number(value);
    const sign = num > 0 ? "+" : "";
    return sign + num.toFixed(1).replace(".", ",") + "%";
  }

  function csvCell(value) {
    const raw = String(value ?? "");
    return '"' + raw.replace(/"/g, '""') + '"';
  }

  function csvLine(fields) {
    return fields.map(csvCell).join(";");
  }

  function buildSummaryExportModel(year) {
    const rows = deps.reportRows(year);
    const totalIncome = rows.reduce((sum, row) => sum + row.income, 0);
    const totalExpense = rows.reduce((sum, row) => sum + row.expense, 0);
    const totalNet = totalIncome - totalExpense;

    return {
      kind: "summary",
      year,
      createdAt: new Date().toLocaleString("de-DE"),
      currency: deps.currency(),
      rows,
      totals: { income: totalIncome, expense: totalExpense, net: totalNet }
    };
  }

  function buildBookingsExportModel(year, month = null, options = {}) {
    const onlyTax = Boolean(options.onlyTax);
    const rows = deps.collectExportBookings(year, month, onlyTax).map(entry => ({
      date: deps.formatCanonicalDate(entry.month),
      description: entry.description,
      category: deps.normalizeCategory(entry.category, deps.customCategories()),
      txType: entry.txType,
      amount: Number(entry.amount || 0),
      account: entry.account || "",
      note: entry.note || "",
      taxDeclaration: Boolean(entry.taxDeclaration)
    }));

    const income = rows.filter(row => row.txType === "Einnahme").reduce((sum, row) => sum + row.amount, 0);
    const expense = rows.filter(row => row.txType === "Ausgabe").reduce((sum, row) => sum + row.amount, 0);

    return {
      kind: "bookings",
      exportVariant: onlyTax ? "tax-year-bookings" : "bookings",
      year,
      month,
      createdAt: new Date().toLocaleString("de-DE"),
      currency: deps.currency(),
      rows,
      totals: {
        count: rows.length,
        income,
        expense,
        net: income - expense
      }
    };
  }

  function buildComparisonExportModel(year) {
    const prevYear = year - 1;
    const currentRows = deps.reportRows(year);
    const prevRows = deps.reportRows(prevYear);

    const currentIncome = currentRows.reduce((sum, row) => sum + row.income, 0);
    const currentExpense = currentRows.reduce((sum, row) => sum + row.expense, 0);
    const currentNet = currentIncome - currentExpense;

    const prevIncome = prevRows.reduce((sum, row) => sum + row.income, 0);
    const prevExpense = prevRows.reduce((sum, row) => sum + row.expense, 0);
    const prevNet = prevIncome - prevExpense;

    let currentCount = 0;
    let prevCount = 0;
    deps.userBookings().forEach(entry => {
      const y = deps.getDateParts(entry.month)?.yyyy;
      if (y === year) currentCount += 1;
      else if (y === prevYear) prevCount += 1;
    });

    const toRow = (label, prev, current, isCurrency = true) => {
      const delta = current - prev;
      const pct = prev === 0 ? null : (delta / prev) * 100;
      return { label, prev, current, delta, pct, isCurrency };
    };

    return {
      kind: "comparison",
      year,
      prevYear,
      createdAt: new Date().toLocaleString("de-DE"),
      currency: deps.currency(),
      rows: [
        toRow("Einnahmen", prevIncome, currentIncome, true),
        toRow("Ausgaben", prevExpense, currentExpense, true),
        toRow("Saldo", prevNet, currentNet, true),
        toRow("Buchungen", prevCount, currentCount, false)
      ]
    };
  }

  function buildReportExportModel(year, scope, month) {
    if (scope === "year-bookings") return buildBookingsExportModel(year, null);
    if (scope === "month-bookings") return buildBookingsExportModel(year, month);
    if (scope === "tax-year-bookings") return buildBookingsExportModel(year, null, { onlyTax: true });
    if (scope === "year-comparison") return buildComparisonExportModel(year);
    return buildSummaryExportModel(year);
  }

  function buildCsvSummaryContent(model) {
    const lines = [
      csvLine(["Export", "Finanz Tracker Jahresauswertung"]),
      csvLine(["Jahr", String(model.year)]),
      csvLine(["Erstellt am", model.createdAt]),
      csvLine(["Währung", model.currency]),
      "",
      csvLine(["Monat", "Einnahmen", "Ausgaben", "Saldo"]),
      ...model.rows.map(row => csvLine([
        deps.monthNamesForUi()[row.month - 1],
        reportExportMoney(row.income),
        reportExportMoney(row.expense),
        reportExportMoney(row.net)
      ])),
      "",
      csvLine([
        "Gesamtsumme",
        reportExportMoney(model.totals.income),
        reportExportMoney(model.totals.expense),
        reportExportMoney(model.totals.net)
      ])
    ];

    return "\uFEFF" + lines.join("\n");
  }

  function buildCsvBookingsContent(model) {
    const isTaxScope = model.exportVariant === "tax-year-bookings";
    const scopeLabel = isTaxScope
      ? "Steuererklärung-Buchungen " + model.year
      : (model.month ? deps.reportExportMonthLabel(model.month) + " " + model.year : String(model.year));
    const exportTitle = isTaxScope ? "Finanz Tracker Steuererklärung-Buchungsliste" : "Finanz Tracker Buchungsliste";

    const lines = [
      csvLine(["Export", exportTitle]),
      csvLine(["Zeitraum", scopeLabel]),
      csvLine(["Erstellt am", model.createdAt]),
      csvLine(["Währung", model.currency]),
      "",
      csvLine(["Datum", "Beschreibung", "Kategorie", "Typ", "Betrag", "Konto", "Steuererklärung", "Notiz"]),
      ...model.rows.map(row => csvLine([
        row.date,
        row.description,
        row.category,
        row.txType,
        reportExportMoney(row.amount),
        row.account,
        row.taxDeclaration ? "Ja" : "Nein",
        row.note
      ])),
      "",
      csvLine(["Anzahl Buchungen", String(model.totals.count)]),
      csvLine(["Summe Einnahmen", reportExportMoney(model.totals.income)]),
      csvLine(["Summe Ausgaben", reportExportMoney(model.totals.expense)]),
      csvLine(["Saldo", reportExportMoney(model.totals.net)])
    ];

    return "\uFEFF" + lines.join("\n");
  }

  function buildCsvComparisonContent(model) {
    const lines = [
      csvLine(["Export", "Finanz Tracker Jahresvergleich"]),
      csvLine(["Jahr", String(model.year)]),
      csvLine(["Vorjahr", String(model.prevYear)]),
      csvLine(["Erstellt am", model.createdAt]),
      csvLine(["Währung", model.currency]),
      "",
      csvLine(["Kennzahl", String(model.prevYear), String(model.year), "Veränderung", "Veränderung %"]),
      ...model.rows.map(row => {
        const prevValue = row.isCurrency ? reportExportMoney(row.prev) : String(row.prev);
        const currentValue = row.isCurrency ? reportExportMoney(row.current) : String(row.current);
        const deltaValue = row.isCurrency ? reportExportMoney(row.delta) : String(row.delta);
        return csvLine([row.label, prevValue, currentValue, deltaValue, reportExportPercent(row.pct)]);
      })
    ];

    return "\uFEFF" + lines.join("\n");
  }

  function buildCsvContent(model) {
    if (model.kind === "bookings") return buildCsvBookingsContent(model);
    if (model.kind === "comparison") return buildCsvComparisonContent(model);
    return buildCsvSummaryContent(model);
  }

  async function buildXlsxSheetBytes(sheetName, aoa, columnWidths = []) {
    const ExcelJS = deps.getExcelJS();
    if (!ExcelJS?.Workbook) throw new Error("XLSX-Bibliothek wurde nicht geladen.");

    const wb = new ExcelJS.Workbook();
    const ws = wb.addWorksheet(String(sheetName || "Auswertung").slice(0, 31));
    aoa.forEach(row => ws.addRow(row));

    if (Array.isArray(columnWidths) && columnWidths.length > 0) {
      ws.columns = columnWidths.map(width => ({ width: Number(width) || 12 }));
    }

    const buffer = await wb.xlsx.writeBuffer();
    return new Uint8Array(buffer);
  }

  async function buildXlsxSummaryBytes(model) {
    const aoa = [
      ["Export", "Finanz Tracker Jahresauswertung"],
      ["Jahr", String(model.year)],
      ["Erstellt am", model.createdAt],
      ["Währung", model.currency],
      [],
      ["Monat", "Einnahmen", "Ausgaben", "Saldo"],
      ...model.rows.map(row => [
        deps.monthNamesForUi()[row.month - 1],
        Number(row.income || 0),
        Number(row.expense || 0),
        Number(row.net || 0)
      ]),
      [],
      ["Gesamtsumme", Number(model.totals.income || 0), Number(model.totals.expense || 0), Number(model.totals.net || 0)]
    ];

    return buildXlsxSheetBytes("Auswertung " + model.year, aoa, [20, 16, 16, 16]);
  }

  async function buildXlsxBookingsBytes(model) {
    const isTaxScope = model.exportVariant === "tax-year-bookings";
    const scopeLabel = isTaxScope
      ? "Steuererklärung-Buchungen " + model.year
      : (model.month ? deps.reportExportMonthLabel(model.month) + " " + model.year : String(model.year));
    const exportTitle = isTaxScope ? "Finanz Tracker Steuererklärung-Buchungsliste" : "Finanz Tracker Buchungsliste";

    const aoa = [
      ["Export", exportTitle],
      ["Zeitraum", scopeLabel],
      ["Erstellt am", model.createdAt],
      ["Währung", model.currency],
      [],
      ["Datum", "Beschreibung", "Kategorie", "Typ", "Betrag", "Konto", "Steuererklärung", "Notiz"],
      ...model.rows.map(row => [
        row.date,
        row.description,
        row.category,
        row.txType,
        Number(row.amount || 0),
        row.account,
        row.taxDeclaration ? "Ja" : "Nein",
        row.note
      ]),
      [],
      ["Anzahl Buchungen", Number(model.totals.count || 0)],
      ["Summe Einnahmen", Number(model.totals.income || 0)],
      ["Summe Ausgaben", Number(model.totals.expense || 0)],
      ["Saldo", Number(model.totals.net || 0)]
    ];
    const name = isTaxScope
      ? "Steuer " + model.year
      : (model.month
        ? "Buchungen " + String(model.month).padStart(2, "0") + "." + model.year
        : "Buchungen " + model.year);

    return buildXlsxSheetBytes(name, aoa, [12, 30, 22, 12, 14, 18, 16, 36]);
  }

  async function buildXlsxComparisonBytes(model) {
    const aoa = [
      ["Export", "Finanz Tracker Jahresvergleich"],
      ["Jahr", String(model.year)],
      ["Vorjahr", String(model.prevYear)],
      ["Erstellt am", model.createdAt],
      ["Währung", model.currency],
      [],
      ["Kennzahl", String(model.prevYear), String(model.year), "Veränderung", "Veränderung %"],
      ...model.rows.map(row => [
        row.label,
        Number(row.prev || 0),
        Number(row.current || 0),
        Number(row.delta || 0),
        reportExportPercent(row.pct)
      ])
    ];

    return buildXlsxSheetBytes("Vergleich " + model.year, aoa, [20, 16, 16, 16, 14]);
  }

  async function buildXlsxBytes(model) {
    if (model.kind === "bookings") return await buildXlsxBookingsBytes(model);
    if (model.kind === "comparison") return await buildXlsxComparisonBytes(model);
    return await buildXlsxSummaryBytes(model);
  }

  function buildPdfSummaryBytes(model) {
    const jsPdfNs = deps.getJsPdf();
    if (!jsPdfNs?.jsPDF) throw new Error("PDF-Bibliothek wurde nicht geladen.");

    const doc = new jsPdfNs.jsPDF({ unit: "pt", format: "a4" });

    doc.setFont("helvetica", "bold");
    doc.setFontSize(14);
    doc.text("Finanz Tracker Jahresauswertung", 40, 44);

    doc.setFont("helvetica", "normal");
    doc.setFontSize(10);
    doc.text("Jahr: " + model.year, 40, 66);
    doc.text("Erstellt am: " + model.createdAt, 40, 82);
    doc.text("Währung: " + model.currency, 40, 98);

    const body = model.rows.map(row => [
      deps.monthNamesForUi()[row.month - 1],
      reportExportMoney(row.income),
      reportExportMoney(row.expense),
      reportExportMoney(row.net)
    ]);

    if (typeof doc.autoTable === "function") {
      doc.autoTable({
        startY: 116,
        head: [["Monat", "Einnahmen", "Ausgaben", "Saldo"]],
        body,
        styles: { font: "helvetica", fontSize: 9 },
        headStyles: { fillColor: [15, 118, 110] }
      });

      const endY = doc.lastAutoTable?.finalY || 116;
      doc.setFont("helvetica", "bold");
      doc.text(
        "Gesamtsumme  Einnahmen: " + reportExportMoney(model.totals.income) +
          "   Ausgaben: " + reportExportMoney(model.totals.expense) +
          "   Saldo: " + reportExportMoney(model.totals.net),
        40,
        endY + 24
      );
    }

    const arr = doc.output("arraybuffer");
    return new Uint8Array(arr);
  }

  function buildPdfBookingsBytes(model) {
    const jsPdfNs = deps.getJsPdf();
    if (!jsPdfNs?.jsPDF) throw new Error("PDF-Bibliothek wurde nicht geladen.");

    const doc = new jsPdfNs.jsPDF({ unit: "pt", format: "a4", orientation: "landscape" });

    const isTaxScope = model.exportVariant === "tax-year-bookings";
    const scopeLabel = isTaxScope
      ? "Steuererklärung-Buchungen " + model.year
      : (model.month ? deps.reportExportMonthLabel(model.month) + " " + model.year : String(model.year));
    const exportTitle = isTaxScope ? "Finanz Tracker Steuererklärung-Buchungsliste" : "Finanz Tracker Buchungsliste";

    doc.setFont("helvetica", "bold");
    doc.setFontSize(14);
    doc.text(exportTitle, 40, 44);

    doc.setFont("helvetica", "normal");
    doc.setFontSize(10);
    doc.text("Zeitraum: " + scopeLabel, 40, 66);
    doc.text("Erstellt am: " + model.createdAt, 40, 82);
    doc.text("Währung: " + model.currency, 40, 98);

    const body = model.rows.map(row => [
      row.date,
      row.description,
      row.category,
      row.txType,
      reportExportMoney(row.amount),
      row.account,
      row.taxDeclaration ? "Ja" : "Nein",
      row.note
    ]);

    if (typeof doc.autoTable === "function") {
      doc.autoTable({
        startY: 116,
        head: [["Datum", "Beschreibung", "Kategorie", "Typ", "Betrag", "Konto", "Steuererklärung", "Notiz"]],
        body,
        styles: { font: "helvetica", fontSize: 8, cellPadding: 4 },
        headStyles: { fillColor: [15, 118, 110] },
        columnStyles: {
          0: { cellWidth: 64 },
          1: { cellWidth: 160 },
          2: { cellWidth: 112 },
          3: { cellWidth: 58 },
          4: { cellWidth: 68 },
          5: { cellWidth: 90 },
          6: { cellWidth: 78 },
          7: { cellWidth: 118 }
        }
      });

      const endY = doc.lastAutoTable?.finalY || 116;
      doc.setFont("helvetica", "bold");
      doc.text(
        "Buchungen: " + model.totals.count +
          "   Einnahmen: " + reportExportMoney(model.totals.income) +
          "   Ausgaben: " + reportExportMoney(model.totals.expense) +
          "   Saldo: " + reportExportMoney(model.totals.net),
        40,
        endY + 24
      );
    }

    const arr = doc.output("arraybuffer");
    return new Uint8Array(arr);
  }

  function buildPdfComparisonBytes(model) {
    const jsPdfNs = deps.getJsPdf();
    if (!jsPdfNs?.jsPDF) throw new Error("PDF-Bibliothek wurde nicht geladen.");

    const doc = new jsPdfNs.jsPDF({ unit: "pt", format: "a4" });

    doc.setFont("helvetica", "bold");
    doc.setFontSize(14);
    doc.text("Finanz Tracker Jahresvergleich", 40, 44);

    doc.setFont("helvetica", "normal");
    doc.setFontSize(10);
    doc.text("Jahr: " + model.year, 40, 66);
    doc.text("Vorjahr: " + model.prevYear, 40, 82);
    doc.text("Erstellt am: " + model.createdAt, 40, 98);
    doc.text("Währung: " + model.currency, 40, 114);

    const body = model.rows.map(row => {
      const prevValue = row.isCurrency ? reportExportMoney(row.prev) : String(row.prev);
      const currentValue = row.isCurrency ? reportExportMoney(row.current) : String(row.current);
      const deltaValue = row.isCurrency ? reportExportMoney(row.delta) : String(row.delta);
      return [row.label, prevValue, currentValue, deltaValue, reportExportPercent(row.pct)];
    });

    if (typeof doc.autoTable === "function") {
      doc.autoTable({
        startY: 132,
        head: [["Kennzahl", String(model.prevYear), String(model.year), "Veränderung", "Veränderung %"]],
        body,
        styles: { font: "helvetica", fontSize: 9 },
        headStyles: { fillColor: [15, 118, 110] }
      });
    }

    const arr = doc.output("arraybuffer");
    return new Uint8Array(arr);
  }

  function buildPdfBytes(model) {
    if (model.kind === "bookings") return buildPdfBookingsBytes(model);
    if (model.kind === "comparison") return buildPdfComparisonBytes(model);
    return buildPdfSummaryBytes(model);
  }

  function reportExportFilename(model, format) {
    const ext = String(format || "csv").toLowerCase();
    if (model.kind === "bookings") {
      if (model.exportVariant === "tax-year-bookings") return "steuererklaerung_buchungen_" + model.year + "." + ext;
      if (model.month) return "buchungen_" + String(model.month).padStart(2, "0") + "_" + model.year + "." + ext;
      return "buchungen_" + model.year + "." + ext;
    }
    if (model.kind === "comparison") return "jahresvergleich_" + model.year + "_vs_" + model.prevYear + "." + ext;
    return "auswertung_" + model.year + "." + ext;
  }

  return {
    buildReportExportModel,
    buildCsvContent,
    buildXlsxBytes,
    buildPdfBytes,
    reportExportFilename
  };
}

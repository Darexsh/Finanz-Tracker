export function createReportExporter(deps) {
  return async function exportReport() {
    const year = deps.parseYear(deps.reportYearInputValue());
    if (!year) {
      deps.showToast(
        deps.isEnglish() ? "Please enter a valid year first." : "Bitte zuerst ein gültiges Jahr eintragen.",
        "error"
      );
      return;
    }

    const scope = deps.getReportExportScope();
    const month = scope === "month-bookings" ? deps.getReportExportMonth() : null;
    if (scope === "month-bookings" && month === null) {
      deps.showToast(
        deps.isEnglish() ? "Please choose a valid month for export." : "Bitte einen gültigen Monat für den Export wählen.",
        "error"
      );
      return;
    }

    const model = deps.buildReportExportModel(year, scope, month);
    const format = deps.reportExportFormatValue();

    if (format === "xlsx") {
      const filename = deps.reportExportFilename(model, "xlsx");
      let bytes;
      try {
        bytes = await deps.buildXlsxBytes(model);
      } catch (_) {
        deps.showToast(deps.isEnglish() ? "Could not generate XLSX." : "XLSX konnte nicht erstellt werden.", "error");
        return;
      }
      await deps.writeBinaryWithFallback({
        filename,
        bytes,
        mime: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
        successPrefixEn: "XLSX exported: ",
        successPrefixDe: "XLSX exportiert: ",
        fallbackMsgEn: "XLSX browser download started.",
        fallbackMsgDe: "XLSX als Browser-Download gestartet."
      });
      return;
    }

    if (format === "pdf") {
      const filename = deps.reportExportFilename(model, "pdf");
      let bytes;
      try {
        bytes = deps.buildPdfBytes(model);
      } catch (_) {
        deps.showToast(deps.isEnglish() ? "Could not generate PDF." : "PDF konnte nicht erstellt werden.", "error");
        return;
      }
      await deps.writeBinaryWithFallback({
        filename,
        bytes,
        mime: "application/pdf",
        successPrefixEn: "PDF exported: ",
        successPrefixDe: "PDF exportiert: ",
        fallbackMsgEn: "PDF browser download started.",
        fallbackMsgDe: "PDF als Browser-Download gestartet."
      });
      return;
    }

    const filename = deps.reportExportFilename(model, "csv");
    const content = deps.buildCsvContent(model);
    await deps.writeTextWithFallback({
      filename,
      content,
      mime: "text/csv;charset=utf-8",
      successPrefixEn: "CSV exported: ",
      successPrefixDe: "CSV exportiert: ",
      fallbackMsgEn: "CSV browser download started.",
      fallbackMsgDe: "CSV als Browser-Download gestartet."
    });
  };
}


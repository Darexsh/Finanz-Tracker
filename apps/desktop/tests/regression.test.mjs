import test from "node:test";
import assert from "node:assert/strict";

import { sanitizeDesktopSettings } from "../modules/state.js";
import { parseMonth, formatCanonicalDate, monthSortKey } from "../modules/bookings.js";
import { normalizeSyncFolderPath } from "../modules/sync.js";
import { createReportExporter } from "../modules/reports/export.js";

test("settings persistence sanitization keeps only supported values", () => {
  const sanitized = sanitizeDesktopSettings({
    language: "en",
    dateFormat: "YYYY-MM-DD",
    currency: "usd",
    startTab: "info",
    defaultExportFormat: "xlsx",
    keepDateAfterSave: false,
    categorySuggestions: false,
    fontSize: "xxlarge",
    navigationAnimationStyle: "zoom",
    appLockEnabled: true,
    appLockPin: "1234"
  });

  assert.equal(sanitized.language, "en");
  assert.equal(sanitized.dateFormat, "YYYY-MM-DD");
  assert.equal(sanitized.currency, "USD");
  assert.equal(sanitized.startTab, "info");
  assert.equal(sanitized.defaultExportFormat, "xlsx");
  assert.equal(sanitized.keepDateAfterSave, false);
  assert.equal(sanitized.categorySuggestions, false);
  assert.equal(sanitized.fontSize, "xxlarge");
  assert.equal(sanitized.navigationAnimationStyle, "zoom");
  assert.equal(sanitized.appLockEnabled, true);
  assert.equal(sanitized.appLockPin, "1234");
});

test("bookings date parsing/format/sort are stable", () => {
  assert.equal(parseMonth("2026-04-08"), "08.04.2026");
  assert.equal(parseMonth("08.04.2026"), "08.04.2026");
  assert.equal(parseMonth("04/08/2026"), "08.04.2026");
  assert.equal(formatCanonicalDate("08.04.2026", "YYYY-MM-DD"), "2026-04-08");
  assert.equal(formatCanonicalDate("08.04.2026", "MM/DD/YYYY"), "04/08/2026");
  assert.equal(monthSortKey("08.04.2026") > monthSortKey("02.04.2025"), true);
});

test("sync path normalization trims safely", () => {
  assert.equal(normalizeSyncFolderPath("  C:\\\\Sync\\\\Finanz  "), "C:\\\\Sync\\\\Finanz");
  assert.equal(normalizeSyncFolderPath(""), "");
});

test("report exporter dispatches format flows behind one interface", async () => {
  let binaryCalls = 0;
  let textCalls = 0;
  const exporter = createReportExporter({
    parseYear: value => Number(value),
    reportYearInputValue: () => "2026",
    getReportExportScope: () => "summary",
    getReportExportMonth: () => 4,
    buildReportExportModel: () => ({ year: 2026 }),
    reportExportFormatValue: () => "xlsx",
    reportExportFilename: () => "out.xlsx",
    buildXlsxBytes: async () => new Uint8Array([1, 2, 3]),
    buildPdfBytes: () => new Uint8Array([4, 5]),
    buildCsvContent: () => "a;b",
    writeBinaryWithFallback: async () => {
      binaryCalls += 1;
    },
    writeTextWithFallback: async () => {
      textCalls += 1;
    },
    showToast: () => {},
    isEnglish: () => true
  });

  await exporter();
  assert.equal(binaryCalls, 1);
  assert.equal(textCalls, 0);
});


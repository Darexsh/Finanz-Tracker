export const START_TABS = ["dashboard", "bookings", "reports", "synchronisierung", "settings", "info"];
export const DATE_FORMATS = ["DD.MM.YYYY", "YYYY-MM-DD", "MM/DD/YYYY"];
export const EXPORT_FORMATS = ["pdf", "xlsx", "csv"];
export const FONT_SIZES = ["normal", "large", "xlarge", "xxlarge"];
export const NAV_ANIMATIONS = ["slide", "fade", "zoom", "pop", "rotate", "none"];

export const DEFAULT_DESKTOP_SETTINGS = Object.freeze({
  language: "system",
  dateFormat: "DD.MM.YYYY",
  currency: "EUR",
  sortDirection: "desc",
  startTab: "dashboard",
  defaultExportFormat: "pdf",
  keepDateAfterSave: true,
  categorySuggestions: true,
  fontSize: "normal",
  navigationAnimationStyle: "slide",
  appLockEnabled: false,
  appLockPin: ""
});

export function sanitizeDesktopSettings(raw) {
  const source = raw && typeof raw === "object" ? raw : {};
  const language = ["system", "de", "en"].includes(String(source.language || "system"))
    ? String(source.language || "system")
    : DEFAULT_DESKTOP_SETTINGS.language;
  const dateFormat = DATE_FORMATS.includes(String(source.dateFormat))
    ? String(source.dateFormat)
    : DEFAULT_DESKTOP_SETTINGS.dateFormat;
  const currency = ["EUR", "USD"].includes(String(source.currency || "").toUpperCase())
    ? String(source.currency).toUpperCase()
    : DEFAULT_DESKTOP_SETTINGS.currency;
  const sortDirection = source.sortDirection === "asc" ? "asc" : "desc";
  const startTab = START_TABS.includes(String(source.startTab || ""))
    ? String(source.startTab)
    : DEFAULT_DESKTOP_SETTINGS.startTab;
  const defaultExportFormat = EXPORT_FORMATS.includes(String(source.defaultExportFormat || "").toLowerCase())
    ? String(source.defaultExportFormat).toLowerCase()
    : DEFAULT_DESKTOP_SETTINGS.defaultExportFormat;
  const keepDateAfterSave = source.keepDateAfterSave !== false;
  const categorySuggestions = source.categorySuggestions !== false;
  const fontSize = FONT_SIZES.includes(String(source.fontSize))
    ? String(source.fontSize)
    : DEFAULT_DESKTOP_SETTINGS.fontSize;
  const navigationAnimationStyle = NAV_ANIMATIONS.includes(String(source.navigationAnimationStyle || "").toLowerCase())
    ? String(source.navigationAnimationStyle).toLowerCase()
    : DEFAULT_DESKTOP_SETTINGS.navigationAnimationStyle;
  const appLockEnabled = source.appLockEnabled === true;
  const appLockPin = typeof source.appLockPin === "string" ? source.appLockPin : "";

  return {
    language,
    dateFormat,
    currency,
    sortDirection,
    startTab,
    defaultExportFormat,
    keepDateAfterSave,
    categorySuggestions,
    fontSize,
    navigationAnimationStyle,
    appLockEnabled,
    appLockPin
  };
}


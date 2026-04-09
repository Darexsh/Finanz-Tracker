const STORAGE_KEY = "finanz-universal-v1";
const LEGACY_MIGRATED_KEY = "finanz-universal-migrated-to-sqlite-v1";
const RECOVERY_FALLBACK_KEY = "finanz-universal-recovery-fallback-v1";
const DAILY_BACKUP_KEY = "finanz-universal-daily-backup-v1";
const SYNC_LAST_WRITE_KEY = "finanz-universal-sync-last-write-v1";
const SYNC_LAST_RESTORE_KEY = "finanz-universal-sync-last-restore-v1";
const DEFAULT_DESKTOP_SETTINGS = Object.freeze({
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

const CATEGORIES = [
  "Miete", "Nebenkosten", "Strom/Gas", "Internet/Handy", "Lebensmittel", "Drogerie",
  "Haushalt", "Mobilität", "Auto", "Parken", "ÖPNV", "Versicherung", "Abgaben/Beiträge",
  "Gesundheit", "Shopping", "Kleidung", "Elektronik", "Freizeit", "Gaming/Medien",
  "Gastronomie", "Reisen", "Bildung", "Geschenke", "Kinder", "Haustiere", "Abo",
  "Steuern/Gebühren", "Gehalt", "Nebenverdienst", "Transfer", "Sonstiges"
];

const ACCOUNTS = ["Girokonto", "Kreditkarte", "Paypal", "Bargeld", "Extra Konto", "Sonstiges"];

const MONTH_NAMES = [
  "Januar", "Februar", "März", "April", "Mai", "Juni",
  "Juli", "August", "September", "Oktober", "November", "Dezember"
];

const MONTH_NAMES_EN = [
  "January", "February", "March", "April", "May", "June",
  "July", "August", "September", "October", "November", "December"
];

const I18N = {
  de: {
    heroSubtitle: "Ein Codebase für Desktop und Android",
    profile: "Profil",
    add: "Neu",
    rename: "Umbenennen",
    delete: "Löschen",
    dashboard: "Dashboard",
    bookings: "Buchungen",
    reports: "Auswertung",
    sync: "Synchronisierung",
    settings: "Einstellungen",
    settingsTitle: "Einstellungen",
    settingsIntro: "Alle App-Einstellungen sind hier zentral gruppiert.",
    settingsGroupGeneral: "Allgemein",
    settingsGroupBehavior: "Buchungsverhalten",
    settingsGroupAppearance: "Darstellung",
    settingsGroupSecurity: "Sicherheit",
    settingsLanguage: "Sprache",
    settingsDateFormat: "Datumsformat",
    settingsCurrency: "Währung",
    settingsSort: "Buchungssortierung",
    settingsStartTab: "Start-Tab",
    settingsDefaultExportFormat: "Standard Export-Format",
    settingsKeepDateAfterSave: "Datum nach Speichern beibehalten",
    settingsSuggestions: "Kategorie-Vorschläge aktivieren",
    active: "Aktiv",
    inactive: "Inaktiv",
    settingsFontSize: "Schriftgröße",
    settingsNavigationAnimation: "Navigationsanimation",
    settingsAppLock: "App-Sperre aktivieren",
    settingsBackup: "Backup",
    settingsBackupNote: "Exportiert bzw. importiert den kompletten App-Status (Profile, Buchungen, Kategorien, Einstellungen).",
    newestFirst: "Neueste zuerst",
    oldestFirst: "Älteste zuerst",
    normal: "Normal",
    large: "Groß",
    xlarge: "Sehr groß",
    xxlarge: "Extra groß",
    lockTitle: "App gesperrt",
    lockMsg: "Zum Entsperren bitte PIN eingeben.",
    unlock: "Entsperren",
    pinPromptSet: "PIN für App-Sperre festlegen (mindestens 4 Zeichen):",
    pinPromptDisable: "PIN eingeben, um App-Sperre zu deaktivieren:",
    pinPromptUnlock: "PIN eingeben, um App zu entsperren:",
    pinMismatch: "PIN ist falsch.",
    pinTooShort: "PIN ist zu kurz (mindestens 4 Zeichen).",
    lockEnabled: "App-Sperre aktiviert.",
    lockDisabled: "App-Sperre deaktiviert.",
    backupExported: "Backup exportiert",
    backupImported: "Backup importiert.",
    exportCanceled: "Export abgebrochen.",
    backupImportConfirm: "Backup importieren?\nAktuelle Daten werden vollständig überschrieben.",
    backupImportTitle: "Backup importieren",
    backupImportError: "Backup konnte nicht importiert werden.\nBitte eine gültige JSON-Datei wählen.",
    backupTitle: "Backup",
    datePlaceholder: "02.04.2026",
    dateLabel: "Datum (TT.MM.JJJJ)",
    monthAll: "Monat: Alle",
    yearAll: "Jahr: Alle",
    categoryAll: "Kategorie: Alle",
    accountAll: "Konto: Alle",
    dateInvalid: "Bitte Datum im ausgewählten Datumsformat eingeben.",
    topCategoriesMonthTitle: "Top Kategorien (Monat)",
    month: "Monat",
    monthlyFlowTitle: "Monatsverlauf (Einnahmen und Ausgaben)",
    year: "Jahr",
    chartLegend: "Grün = Einnahmen, Rot im Balken = Ausgaben. Hover oder Klick zeigt Details.",
    newBooking: "Neue Buchung",
    bookingHelpEdit: "Tipp: Für bestehende Einträge in der Tabelle doppelklicken, anpassen und einfach wieder speichern.",
    description: "Beschreibung",
    date: "Datum",
    category: "Kategorie",
    amount: "Betrag (€)",
    type: "Typ",
    account: "Konto",
    note: "Notiz",
    taxDeclarationBooking: "Buchung für Steuererklärung",
    taxDeclaration: "Steuererklärung",
    save: "Speichern",
    clear: "Leeren",
    manageCategories: "Kategorien verwalten",
    filter: "Filter",
    filterHelp: "Filter greifen sofort auf die Buchungstabelle. Mit \"Reset\" setzt du alle Filter wieder zurück.",
    typeAll: "Typ: Alle",
    expense: "Ausgabe",
    income: "Einnahme",
    search: "Suche",
    reset: "Reset",
    bookingsTable: "Buchungen",
    deleteSelected: "Ausgewählte löschen",
    selectedCount: "{count} ausgewählt",
    selectedCountShown: "{count} ausgewählt · {shown}/{total} angezeigt",
    reportYearlyTitle: "Jahresauswertung",
    exportContent: "Export-Inhalt",
    yearSummary: "Jahresübersicht",
    yearComparison: "Jahresvergleich (Jahr vs. Vorjahr)",
    allBookingsYear: "Alle Buchungen im Jahr",
    allBookingsMonth: "Alle Buchungen im Monat",
    taxBookingsYear: "Steuererklärung-Buchungen im Jahr",
    export: "Exportieren",
    metric: "Kennzahl",
    currentYear: "Aktuelles Jahr",
    change: "Veränderung",
    balance: "Saldo",
    months: "Monate",
    syncTitle: "Synchronisierung",
    syncIntro: "Hier werden Ordner-Auswahl, Sync-Status und manuelle Sync-Aktionen zentral verwaltet.",
    syncAndroidHint: "Android-Hinweis: Auf dem Smartphone eine beliebige App für Ordner-Synchronisierung einrichten (z. B. FolderSync, Syncthing oder andere), denselben Cloud-/Netzwerk-Ordner mit einem lokalen Handy-Ordner koppeln und Two-way-Sync aktivieren. Google Drive ist nur eine mögliche Option, nicht verpflichtend.",
    syncFolderTitle: "Sync-Ordner",
    folderPath: "Ordnerpfad",
    browse: "Durchsuchen",
    backupNow: "Jetzt sichern",
    restoreLatest: "Neueste Sicherung laden",
    backupExportBtn: "Backup exportieren",
    backupImportBtn: "Backup importieren",
    about: "Info",
    appInfoTitle: "App-Info",
    appInfoVersion: "Version 1.0.0",
    appInfoDescription: "Finanz Tracker ist eine Finanz-App mit einer gemeinsamen Codebase für Desktop und Android. Sie unterstützt Profile, Buchungen, Auswertungen, Exporte und ordnerbasierte Synchronisierung.",
    appInfoDeveloper: "Entwickler: Darexsh by Daniel Sichler",
    appInfoActionsTitle: "Aktionen",
    appInfoOpenEmail: "E-Mail schreiben",
    appInfoOpenGithub: "Soziale Medien öffnen",
    appInfoOpenTelegram: "Telegram-Bot öffnen",
    appInfoOpenProfile: "GitHub-Profil",
    appInfoOpenCoffee: "Kaffee spendieren",
    statusNotConfigured: "Sync: nicht konfiguriert",
    lastSyncBackup: "Letzte Sync-Sicherung: {value}",
    lastSyncRestore: "Letzte Wiederherstellung: {value}"
  },
  en: {
    heroSubtitle: "One codebase for desktop and Android",
    profile: "Profile",
    add: "New",
    rename: "Rename",
    delete: "Delete",
    dashboard: "Dashboard",
    bookings: "Bookings",
    reports: "Reports",
    sync: "Sync",
    settings: "Settings",
    settingsTitle: "Settings",
    settingsIntro: "All app settings are grouped here in one place.",
    settingsGroupGeneral: "General",
    settingsGroupBehavior: "Booking behavior",
    settingsGroupAppearance: "Appearance",
    settingsGroupSecurity: "Security",
    settingsLanguage: "Language",
    settingsDateFormat: "Date format",
    settingsCurrency: "Currency",
    settingsSort: "Booking sort direction",
    settingsStartTab: "Start tab",
    settingsDefaultExportFormat: "Default export format",
    settingsKeepDateAfterSave: "Keep date after save",
    settingsSuggestions: "Enable category suggestions",
    active: "Active",
    inactive: "Inactive",
    settingsFontSize: "Font size",
    settingsNavigationAnimation: "Navigation animation",
    settingsAppLock: "Enable app lock",
    settingsBackup: "Backup",
    settingsBackupNote: "Exports or imports the full app state (profiles, bookings, categories, settings).",
    newestFirst: "Newest first",
    oldestFirst: "Oldest first",
    normal: "Normal",
    large: "Large",
    xlarge: "Very large",
    xxlarge: "Extra large",
    lockTitle: "App locked",
    lockMsg: "Enter PIN to unlock.",
    unlock: "Unlock",
    pinPromptSet: "Set app-lock PIN (minimum 4 characters):",
    pinPromptDisable: "Enter PIN to disable app lock:",
    pinPromptUnlock: "Enter PIN to unlock app:",
    pinMismatch: "PIN is incorrect.",
    pinTooShort: "PIN is too short (minimum 4 characters).",
    lockEnabled: "App lock enabled.",
    lockDisabled: "App lock disabled.",
    backupExported: "Backup exported",
    backupImported: "Backup imported.",
    exportCanceled: "Export canceled.",
    backupImportConfirm: "Import backup?\nCurrent data will be fully overwritten.",
    backupImportTitle: "Import backup",
    backupImportError: "Backup could not be imported.\nPlease select a valid JSON file.",
    backupTitle: "Backup",
    datePlaceholder: "04/02/2026",
    dateLabel: "Date (MM/DD/YYYY)",
    monthAll: "Month: All",
    yearAll: "Year: All",
    categoryAll: "Category: All",
    accountAll: "Account: All",
    dateInvalid: "Please enter a date in the selected date format.",
    topCategoriesMonthTitle: "Top Categories (Month)",
    month: "Month",
    monthlyFlowTitle: "Monthly Flow (Income and Expense)",
    year: "Year",
    chartLegend: "Green = income, red in bar = expense. Hover or click shows details.",
    newBooking: "New Booking",
    bookingHelpEdit: "Tip: Double-click an existing table row, adjust values, then save again.",
    description: "Description",
    date: "Date",
    category: "Category",
    amount: "Amount (€)",
    type: "Type",
    account: "Account",
    note: "Note",
    taxDeclarationBooking: "Booking for tax declaration",
    taxDeclaration: "Tax Declaration",
    save: "Save",
    clear: "Clear",
    manageCategories: "Manage categories",
    filter: "Filter",
    filterHelp: "Filters apply instantly to the bookings table. Use \"Reset\" to clear all filters.",
    typeAll: "Type: All",
    expense: "Expense",
    income: "Income",
    search: "Search",
    reset: "Reset",
    bookingsTable: "Bookings",
    deleteSelected: "Delete selected",
    selectedCount: "{count} selected",
    selectedCountShown: "{count} selected · {shown}/{total} shown",
    reportYearlyTitle: "Yearly Report",
    exportContent: "Export Content",
    yearSummary: "Year Summary",
    yearComparison: "Year Comparison (Year vs. Previous Year)",
    allBookingsYear: "All Bookings in Year",
    allBookingsMonth: "All Bookings in Month",
    taxBookingsYear: "Tax-Declaration Bookings in Year",
    export: "Export",
    metric: "Metric",
    currentYear: "Current Year",
    change: "Change",
    balance: "Balance",
    months: "Months",
    syncTitle: "Sync",
    syncIntro: "Folder selection, sync status, and manual sync actions are managed here.",
    syncAndroidHint: "Android note: Set up any folder-sync app on your smartphone (for example FolderSync, Syncthing, or others), connect the same cloud/network folder with a local phone folder, and enable two-way sync. Google Drive is only one possible option, not required.",
    syncFolderTitle: "Sync Folder",
    folderPath: "Folder Path",
    browse: "Browse",
    backupNow: "Backup Now",
    restoreLatest: "Restore Latest Backup",
    backupExportBtn: "Export Backup",
    backupImportBtn: "Import Backup",
    about: "About",
    appInfoTitle: "App info",
    appInfoVersion: "Version 1.0.0",
    appInfoDescription: "Finanz Tracker is a finance app with one shared codebase for Desktop and Android. It supports profiles, bookings, reports, exports, and folder-based sync.",
    appInfoDeveloper: "Developer: Darexsh by Daniel Sichler",
    appInfoActionsTitle: "Actions",
    appInfoOpenEmail: "Write email",
    appInfoOpenGithub: "Open social media",
    appInfoOpenTelegram: "Open Telegram bot",
    appInfoOpenProfile: "GitHub profile",
    appInfoOpenCoffee: "Buy me a coffee",
    statusNotConfigured: "Sync: not configured",
    lastSyncBackup: "Last sync backup: {value}",
    lastSyncRestore: "Last restore: {value}"
  }
};

const KEYWORD_MAP = [
  ["miete", "Miete"], ["nebenkosten", "Nebenkosten"], ["strom", "Strom/Gas"], ["gas", "Strom/Gas"],
  ["simon", "Internet/Handy"], ["simon mobile", "Internet/Handy"], ["internet", "Internet/Handy"], ["handy", "Internet/Handy"],
  ["lidl", "Lebensmittel"], ["aldi", "Lebensmittel"], ["rewe", "Lebensmittel"], ["edeka", "Lebensmittel"], ["einkauf", "Lebensmittel"],
  ["dm", "Drogerie"], ["rossmann", "Drogerie"], ["nagellack", "Drogerie"], ["entfetter", "Drogerie"],
  ["staubsauger", "Haushalt"], ["schrauben", "Haushalt"], ["regenschirm", "Haushalt"], ["backfolie", "Haushalt"], ["backofenlampe", "Haushalt"], ["batterien", "Haushalt"], ["teelicht", "Haushalt"], ["teelichter", "Haushalt"], ["kerze", "Haushalt"], ["kerzen", "Haushalt"],
  ["bahn", "ÖPNV"], ["deutschlandticket", "ÖPNV"],
  ["tanken", "Auto"], ["tank", "Auto"], ["benzin", "Auto"], ["aral", "Auto"],
  ["parken", "Parken"],
  ["versicherung", "Versicherung"], ["rechtsschutz", "Versicherung"], ["adac", "Versicherung"], ["zahnzusatz", "Versicherung"], ["auslandskrankenversicherung", "Versicherung"],
  ["gez", "Abgaben/Beiträge"], ["rundfunk", "Abgaben/Beiträge"],
  ["arzt", "Gesundheit"], ["apotheke", "Gesundheit"], ["zahn", "Gesundheit"],
  ["temu", "Shopping"], ["shein", "Shopping"], ["aliexpress", "Shopping"], ["banggood", "Shopping"], ["tedi", "Shopping"], ["action", "Shopping"], ["amazon", "Shopping"],
  ["socken", "Kleidung"], ["schuhe", "Kleidung"], ["jacke", "Kleidung"], ["winterjacke", "Kleidung"],
  ["pc", "Elektronik"], ["cpu", "Elektronik"], ["kühler", "Elektronik"], ["splitter", "Elektronik"], ["sata", "Elektronik"], ["tapo", "Elektronik"], ["etikettierer", "Elektronik"],
  ["solo leveling", "Gaming/Medien"], ["geisterakten", "Gaming/Medien"],
  ["too good to go", "Gastronomie"], ["burger king", "Gastronomie"], ["mcdonald", "Gastronomie"], ["essen", "Gastronomie"], ["schaschlik", "Gastronomie"], ["holy", "Gastronomie"],
  ["urlaub", "Reisen"],
  ["geschenk", "Geschenke"],
  ["netflix", "Abo"], ["spotify", "Abo"], ["chatgpt", "Abo"], ["chatgpt plus", "Abo"],
  ["gehalt", "Gehalt"], ["arbeit", "Gehalt"],
  ["extra konto", "Transfer"], ["paypal", "Transfer"]
];

const CATEGORY_ALIAS_MAP = new Map([
  ["mobilitaet", "Mobilität"],
  ["mobilität", "Mobilität"],
  ["gebuhren", "Steuern/Gebühren"],
  ["gebühren", "Steuern/Gebühren"],
  ["steuern", "Steuern/Gebühren"],
  ["gebuhr", "Steuern/Gebühren"],
  ["beitrage", "Abgaben/Beiträge"],
  ["beiträge", "Abgaben/Beiträge"],
  ["abgabe", "Abgaben/Beiträge"],
  ["abgaben", "Abgaben/Beiträge"],
  ["verpflegung", "Gastronomie"],
  ["restaurant", "Gastronomie"],
  ["food", "Gastronomie"],
  ["markt", "Lebensmittel"],
  ["supermarkt", "Lebensmittel"],
  ["technik", "Elektronik"],
  ["hardware", "Elektronik"],
  ["kleider", "Kleidung"],
  ["mode", "Kleidung"],
  ["sonstige", "Sonstiges"]
]);

const CATEGORY_LABEL_EN = new Map([
  ["Miete", "Rent"],
  ["Nebenkosten", "Utilities"],
  ["Strom/Gas", "Electricity/Gas"],
  ["Internet/Handy", "Internet/Phone"],
  ["Lebensmittel", "Groceries"],
  ["Drogerie", "Drugstore"],
  ["Haushalt", "Household"],
  ["Mobilität", "Mobility"],
  ["Auto", "Car"],
  ["Parken", "Parking"],
  ["ÖPNV", "Public Transport"],
  ["Versicherung", "Insurance"],
  ["Abgaben/Beiträge", "Fees/Contributions"],
  ["Gesundheit", "Health"],
  ["Shopping", "Shopping"],
  ["Kleidung", "Clothing"],
  ["Elektronik", "Electronics"],
  ["Freizeit", "Leisure"],
  ["Gaming/Medien", "Gaming/Media"],
  ["Gastronomie", "Dining"],
  ["Reisen", "Travel"],
  ["Bildung", "Education"],
  ["Geschenke", "Gifts"],
  ["Kinder", "Children"],
  ["Haustiere", "Pets"],
  ["Abo", "Subscription"],
  ["Steuern/Gebühren", "Taxes/Fees"],
  ["Gehalt", "Salary"],
  ["Nebenverdienst", "Side Income"],
  ["Transfer", "Transfer"],
  ["Sonstiges", "Other"]
]);

const ACCOUNT_LABEL_EN = new Map([
  ["Girokonto", "Checking Account"],
  ["Kreditkarte", "Credit Card"],
  ["Paypal", "PayPal"],
  ["Bargeld", "Cash"],
  ["Extra Konto", "Extra Account"],
  ["Sonstiges", "Other"]
]);

function sanitizeCustomCategories(list) {
  if (!Array.isArray(list)) return [];
  const seen = new Set();
  const result = [];

  list.forEach(item => {
    const name = String(item || "").trim();
    if (!name) return;
    if (CATEGORIES.includes(name)) return;
    const key = name.toLowerCase();
    if (seen.has(key)) return;
    seen.add(key);
    result.push(name);
  });

  result.sort((a, b) => a.localeCompare(b, "de"));
  return result;
}

function allCategories(customCategories = []) {
  const merged = [...CATEGORIES, ...sanitizeCustomCategories(customCategories)];
  const seen = new Set();
  return merged.filter(item => {
    const key = item.toLowerCase();
    if (seen.has(key)) return false;
    seen.add(key);
    return true;
  });
}

function normalizeCategory(value, customCategories = []) {
  const raw = String(value || "").trim();
  if (!raw) return "Sonstiges";

  const categories = allCategories(customCategories);
  if (categories.includes(raw)) return raw;
  const caseMatch = categories.find(item => item.toLowerCase() === raw.toLowerCase());
  if (caseMatch) return caseMatch;

  const simple = raw
    .normalize("NFKD")
    .replace(/[̀-ͯ]/g, "")
    .replace(/[^a-zA-Z0-9/ ]+/g, "")
    .toLowerCase()
    .trim();

  if (CATEGORY_ALIAS_MAP.has(simple)) {
    return CATEGORY_ALIAS_MAP.get(simple) || "Sonstiges";
  }

  const mappedByKeyword = KEYWORD_MAP.find(([keyword]) => simple.includes(keyword));
  if (mappedByKeyword) return mappedByKeyword[1];

  return "Sonstiges";
}

function normalizeBookingCategory(entry, customCategories = []) {
  if (!entry || typeof entry !== "object") return entry;
  return { ...entry, category: normalizeCategory(entry.category, customCategories) };
}


const state = createDefaultState();
let stateReady = false;
let saveTimer = null;
let persistInFlight = false;
let persistQueued = false;
let categoryManuallyOverridden = false;
let lastAutoCategory = null;
let selectedBookingId = null;
let activeTabId = "dashboard";
let recoveryNotice = null;
let syncConfigured = false;
let syncAutoBackupTimer = null;
let syncAutoRestoreTimer = null;
let lastSyncPayloadSnapshot = null;
let skipNextSyncBackupWrite = false;
const selectedBookingIds = new Set();
const monthlyChartState = { bars: [], rows: [], year: null, hoverIndex: -1, pinnedIndex: null };
const BOOKING_RENDER_INITIAL = 250;
const BOOKING_RENDER_STEP = 200;
const bookingRenderState = { key: "", visibleCount: BOOKING_RENDER_INITIAL };

let stateDataVersion = 0;
const perfCache = {
  version: -1,
  userId: null,
  userBookings: [],
  reportRowsByYear: new Map(),
  filteredSorted: { key: "", value: [] }
};

const el = {
  userSelect: document.getElementById("userSelect"),
  addUserBtn: document.getElementById("addUserBtn"),
  renameUserBtn: document.getElementById("renameUserBtn"),
  deleteUserBtn: document.getElementById("deleteUserBtn"),
  statsCards: document.getElementById("statsCards"),
  topCategories: document.getElementById("topCategories"),
  monthlyChart: document.getElementById("monthlyChart"),
  monthlyChartTooltip: document.getElementById("monthlyChartTooltip"),
  monthlyChartEmpty: document.getElementById("monthlyChartEmpty"),
  dashboardYearSelect: document.getElementById("dashboardYearSelect"),
  dashboardTopMonthSelect: document.getElementById("dashboardTopMonthSelect"),

  bookingForm: document.getElementById("bookingForm"),
  monthInput: document.getElementById("monthInput"),
  monthPickerBtn: document.getElementById("monthPickerBtn"),
  monthPickerNative: document.getElementById("monthPickerNative"),
  descriptionInput: document.getElementById("descriptionInput"),
  categoryInput: document.getElementById("categoryInput"),
  addCategoryBtn: document.getElementById("addCategoryBtn"),
  renameCategoryBtn: document.getElementById("renameCategoryBtn"),
  deleteCategoryBtn: document.getElementById("deleteCategoryBtn"),
  amountInput: document.getElementById("amountInput"),
  typeInput: document.getElementById("typeInput"),
  typeExpenseOption: document.getElementById("typeExpenseOption"),
  typeIncomeOption: document.getElementById("typeIncomeOption"),
  accountInput: document.getElementById("accountInput"),
  noteInput: document.getElementById("noteInput"),
  taxDeclarationInput: document.getElementById("taxDeclarationInput"),

  fMonth: document.getElementById("fMonth"),
  fYear: document.getElementById("fYear"),
  fType: document.getElementById("fType"),
  fCategory: document.getElementById("fCategory"),
  fAccount: document.getElementById("fAccount"),
  fSearch: document.getElementById("fSearch"),
  resetFiltersBtn: document.getElementById("resetFiltersBtn"),

  bookingsBody: document.getElementById("bookingsBody"),
  bookingsTableWrap: document.querySelector("#bookings .table-wrap"),
  deleteSelectedBookingsBtn: document.getElementById("deleteSelectedBookingsBtn"),
  selectAllBookings: document.getElementById("selectAllBookings"),
  selectedBookingsInfo: document.getElementById("selectedBookingsInfo"),

  reportYearInput: document.getElementById("reportYearInput"),
  exportReportBtn: document.getElementById("exportReportBtn"),
  reportExportFormat: document.getElementById("reportExportFormat"),
  reportExportScope: document.getElementById("reportExportScope"),
  reportExportMonth: document.getElementById("reportExportMonth"),
  reportStatsCards: document.getElementById("reportStatsCards"),
  reportPrevYearHead: document.getElementById("reportPrevYearHead"),
  reportYearHead: document.getElementById("reportYearHead"),
  reportCompareBody: document.getElementById("reportCompareBody"),
  reportBody: document.getElementById("reportBody"),

  syncFolderInput: document.getElementById("syncFolderInput"),
  browseSyncFolderBtn: document.getElementById("browseSyncFolderBtn"),
  syncBackupNowBtn: document.getElementById("syncBackupNowBtn"),
  syncRestoreBtn: document.getElementById("syncRestoreBtn"),
  cloudStatus: document.getElementById("cloudStatus"),
  syncLastWrite: document.getElementById("syncLastWrite"),
  syncLastRestore: document.getElementById("syncLastRestore"),
  settingsLanguage: document.getElementById("settingsLanguage"),
  settingsDateFormat: document.getElementById("settingsDateFormat"),
  settingsCurrency: document.getElementById("settingsCurrency"),
  settingsSortDirection: document.getElementById("settingsSortDirection"),
  settingsStartTab: document.getElementById("settingsStartTab"),
  settingsDefaultExportFormat: document.getElementById("settingsDefaultExportFormat"),
  settingsKeepDateAfterSave: document.getElementById("settingsKeepDateAfterSave"),
  settingsCategorySuggestions: document.getElementById("settingsCategorySuggestions"),
  settingsFontSize: document.getElementById("settingsFontSize"),
  settingsNavigationAnimationStyle: document.getElementById("settingsNavigationAnimationStyle"),
  settingsAppLock: document.getElementById("settingsAppLock"),
  backupExportBtn: document.getElementById("backupExportBtn"),
  backupImportBtn: document.getElementById("backupImportBtn"),
  backupImportInput: document.getElementById("backupImportInput"),
  appLockOverlay: document.getElementById("appLockOverlay"),
  appLockPinInput: document.getElementById("appLockPinInput"),
  appLockUnlockBtn: document.getElementById("appLockUnlockBtn"),
  infoEmailBtn: document.getElementById("infoEmailBtn"),
  infoGithubBtn: document.getElementById("infoGithubBtn"),
  infoTelegramBtn: document.getElementById("infoTelegramBtn"),
  infoProfileBtn: document.getElementById("infoProfileBtn"),
  infoCoffeeBtn: document.getElementById("infoCoffeeBtn"),

  dialogOverlay: document.getElementById("dialogOverlay"),
  dialogTitle: document.getElementById("dialogTitle"),
  dialogMessage: document.getElementById("dialogMessage"),
  dialogInput: document.getElementById("dialogInput"),
  dialogOkBtn: document.getElementById("dialogOkBtn"),
  dialogCancelBtn: document.getElementById("dialogCancelBtn"),

  toastContainer: document.getElementById("toastContainer")
};

const BOOKING_VALIDATION_FIELDS = ["monthInput", "descriptionInput", "amountInput"];

init().catch(err => {
  console.error("Init fehlgeschlagen", err);
});

async function init() {
  bindTabs();
  initBookingFormErrorSlots();
  initSelectOptions();
  bindEvents();
  await hydrateStateFromStorage();
  ensureActiveUser();
  setDefaultMonth();
  setDefaultReportYear();
  window.__FT_UNLOCKED__ = !desktopSettings().appLockEnabled;
  applyDesktopSettings();
  applyLanguageToUi();
  applyAppLockState();
  activateTab(desktopSettings().startTab, { animate: false });
  render();
  stateReady = true;
  await flushRecoveryNotice();
  updateSyncMetaDisplay();
  await refreshSyncStatus();
  await tryAutoRestoreFromSyncWithStartupRetry();
}

function defaultUser() {
  return { id: uid(), name: "Standard" };
}

function createDefaultState() {
  const user = defaultUser();
  return {
    users: [user],
    activeUserId: user.id,
    bookings: [],
    customCategories: [],
    desktopSettings: { ...DEFAULT_DESKTOP_SETTINGS }
  };
}

function sanitizeDesktopSettings(raw) {
  const source = raw && typeof raw === "object" ? raw : {};
  const language = ["system", "de", "en"].includes(String(source.language || "system"))
    ? String(source.language || "system")
    : DEFAULT_DESKTOP_SETTINGS.language;
  const dateFormat = ["DD.MM.YYYY", "YYYY-MM-DD", "MM/DD/YYYY"].includes(String(source.dateFormat))
    ? String(source.dateFormat)
    : DEFAULT_DESKTOP_SETTINGS.dateFormat;
  const currency = ["EUR", "USD"].includes(String(source.currency || "").toUpperCase())
    ? String(source.currency).toUpperCase()
    : DEFAULT_DESKTOP_SETTINGS.currency;
  const sortDirection = source.sortDirection === "asc" ? "asc" : "desc";
  const startTab = ["dashboard", "bookings", "reports", "synchronisierung", "settings", "info"].includes(String(source.startTab || ""))
    ? String(source.startTab)
    : DEFAULT_DESKTOP_SETTINGS.startTab;
  const defaultExportFormat = ["pdf", "xlsx", "csv"].includes(String(source.defaultExportFormat || "").toLowerCase())
    ? String(source.defaultExportFormat).toLowerCase()
    : DEFAULT_DESKTOP_SETTINGS.defaultExportFormat;
  const keepDateAfterSave = source.keepDateAfterSave !== false;
  const categorySuggestions = source.categorySuggestions !== false;
  const fontSize = ["normal", "large", "xlarge", "xxlarge"].includes(String(source.fontSize))
    ? String(source.fontSize)
    : "normal";
  const navigationAnimationStyle = ["slide", "fade", "zoom", "pop", "rotate", "none"].includes(String(source.navigationAnimationStyle || "").toLowerCase())
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

function desktopSettings() {
  if (!state.desktopSettings) {
    state.desktopSettings = { ...DEFAULT_DESKTOP_SETTINGS };
  }
  return state.desktopSettings;
}

function applyDesktopSettings() {
  const settings = desktopSettings();
  const root = document.documentElement;
  let rootFontSize = "16px";
  if (settings.fontSize === "large") rootFontSize = "18px";
  if (settings.fontSize === "xlarge") rootFontSize = "20px";
  if (settings.fontSize === "xxlarge") rootFontSize = "22px";
  root.style.fontSize = rootFontSize;
}

function resolvedLangCode() {
  const selected = desktopSettings().language;
  if (selected === "de" || selected === "en") return selected;
  const system = (navigator.language || "en").toLowerCase();
  return system.startsWith("de") ? "de" : "en";
}

function t(key) {
  const lang = resolvedLangCode();
  return I18N[lang]?.[key] ?? I18N.de[key] ?? key;
}

function tf(key, vars = {}) {
  return Object.entries(vars).reduce(
    (acc, [name, value]) => acc.replaceAll(`{${name}}`, String(value)),
    t(key)
  );
}

function monthNamesForUi() {
  return resolvedLangCode() === "en" ? MONTH_NAMES_EN : MONTH_NAMES;
}

function categoryLabelForUi(categoryName) {
  const raw = String(categoryName || "");
  if (resolvedLangCode() !== "en") return raw;
  return CATEGORY_LABEL_EN.get(raw) || raw;
}

function accountLabelForUi(accountName) {
  const raw = String(accountName || "");
  if (resolvedLangCode() !== "en") return raw;
  return ACCOUNT_LABEL_EN.get(raw) || raw;
}

function txTypeLabelForUi(txType) {
  return txType === "Einnahme" ? t("income") : t("expense");
}

function formatCanonicalDate(canonicalDate) {
  const p = getDateParts(canonicalDate);
  if (!p) return String(canonicalDate || "");
  if (desktopSettings().dateFormat === "YYYY-MM-DD") {
    return `${String(p.yyyy).padStart(4, "0")}-${String(p.mm).padStart(2, "0")}-${String(p.dd).padStart(2, "0")}`;
  }
  if (desktopSettings().dateFormat === "MM/DD/YYYY") {
    return `${String(p.mm).padStart(2, "0")}/${String(p.dd).padStart(2, "0")}/${String(p.yyyy).padStart(4, "0")}`;
  }
  return `${String(p.dd).padStart(2, "0")}.${String(p.mm).padStart(2, "0")}.${String(p.yyyy).padStart(4, "0")}`;
}

function applyLanguageToUi() {
  const lang = resolvedLangCode();
  document.documentElement.lang = lang;

  const mapping = [
    ["heroSubtitle", "heroSubtitle"],
    ["profileLabel", "profile"],
    ["addUserBtn", "add"],
    ["renameUserBtn", "rename"],
    ["deleteUserBtn", "delete"],
    ["tabDashboard", "dashboard"],
    ["tabBookings", "bookings"],
    ["tabReports", "reports"],
    ["tabSync", "sync"],
    ["tabSettings", "settings"],
    ["tabInfo", "about"],
    ["dashTopCategoriesTitle", "topCategoriesMonthTitle"],
    ["dashTopMonthLabel", "month"],
    ["dashMonthlyFlowTitle", "monthlyFlowTitle"],
    ["dashYearLabel", "year"],
    ["dashChartNote", "chartLegend"],
    ["bookingsNewTitle", "newBooking"],
    ["bookingsHelpEdit", "bookingHelpEdit"],
    ["settingsTitle", "settingsTitle"],
    ["settingsIntro", "settingsIntro"],
    ["settingsGroupGeneral", "settingsGroupGeneral"],
    ["settingsGroupBehavior", "settingsGroupBehavior"],
    ["settingsGroupAppearance", "settingsGroupAppearance"],
    ["settingsGroupSecurity", "settingsGroupSecurity"],
    ["settingsLanguageLabel", "settingsLanguage"],
    ["settingsDateFormatLabel", "settingsDateFormat"],
    ["settingsCurrencyLabel", "settingsCurrency"],
    ["settingsSortLabel", "settingsSort"],
    ["settingsStartTabLabel", "settingsStartTab"],
    ["settingsDefaultExportFormatLabel", "settingsDefaultExportFormat"],
    ["settingsKeepDateAfterSaveLabel", "settingsKeepDateAfterSave"],
    ["settingsCategorySuggestionsLabel", "settingsSuggestions"],
    ["settingsFontSizeLabel", "settingsFontSize"],
    ["settingsNavigationAnimationLabel", "settingsNavigationAnimation"],
    ["settingsAppLockLabel", "settingsAppLock"],
    ["settingsBackupTitle", "settingsBackup"],
    ["settingsBackupNote", "settingsBackupNote"],
    ["descriptionInputLabel", "description"],
    ["categoryInputLabel", "category"],
    ["amountInputLabel", "amount"],
    ["typeInputLabel", "type"],
    ["accountInputLabel", "account"],
    ["noteInputLabel", "note"],
    ["taxDeclarationLabel", "taxDeclarationBooking"],
    ["bookingSaveBtn", "save"],
    ["bookingResetBtn", "clear"],
    ["categoryToolsLabel", "manageCategories"],
    ["addCategoryBtn", "add"],
    ["renameCategoryBtn", "rename"],
    ["deleteCategoryBtn", "delete"],
    ["filterTitle", "filter"],
    ["filterHelp", "filterHelp"],
    ["filterTypeAllOption", "typeAll"],
    ["filterTypeExpenseOption", "expense"],
    ["filterTypeIncomeOption", "income"],
    ["resetFiltersBtn", "reset"],
    ["bookingsTableTitle", "bookingsTable"],
    ["deleteSelectedBookingsBtn", "deleteSelected"],
    ["bookingHeadDate", "date"],
    ["bookingHeadDescription", "description"],
    ["bookingHeadCategory", "category"],
    ["bookingHeadType", "type"],
    ["bookingHeadAmount", "amount"],
    ["bookingHeadAccount", "account"],
    ["bookingHeadNote", "note"],
    ["bookingHeadTax", "taxDeclaration"],
    ["reportsYearlyTitle", "reportYearlyTitle"],
    ["reportYearLabel", "year"],
    ["reportScopeLabel", "exportContent"],
    ["scopeSummaryOption", "yearSummary"],
    ["scopeComparisonOption", "yearComparison"],
    ["scopeYearBookingsOption", "allBookingsYear"],
    ["scopeMonthBookingsOption", "allBookingsMonth"],
    ["scopeTaxBookingsOption", "taxBookingsYear"],
    ["reportMonthLabel", "month"],
    ["exportReportBtn", "export"],
    ["reportComparisonTitle", "yearComparison"],
    ["reportHeadMetric", "metric"],
    ["reportYearHead", "currentYear"],
    ["reportHeadChange", "change"],
    ["reportMonthsTitle", "months"],
    ["reportHeadMonth", "month"],
    ["reportHeadIncome", "income"],
    ["reportHeadExpense", "expense"],
    ["reportHeadBalance", "balance"], 
    ["syncTitle", "syncTitle"],
    ["syncIntro", "syncIntro"],
    ["syncAndroidHint", "syncAndroidHint"],
    ["syncFolderTitle", "syncFolderTitle"],
    ["syncFolderPathLabel", "folderPath"],
    ["browseSyncFolderBtn", "browse"],
    ["syncBackupNowBtn", "backupNow"],
    ["syncRestoreBtn", "restoreLatest"],
    ["backupExportBtn", "backupExportBtn"],
    ["backupImportBtn", "backupImportBtn"],
    ["infoTitle", "appInfoTitle"],
    ["infoVersion", "appInfoVersion"],
    ["infoDescription", "appInfoDescription"],
    ["infoDeveloper", "appInfoDeveloper"],
    ["infoActionsTitle", "appInfoActionsTitle"],
    ["appLockTitle", "lockTitle"],
    ["appLockMessage", "lockMsg"],
    ["appLockUnlockBtn", "unlock"]
  ];

  mapping.forEach(([id, key]) => {
    const node = document.getElementById(id);
    if (node) node.textContent = t(key);
  });

  const setInfoActionLabel = (id, key) => {
    const btn = document.getElementById(id);
    if (!btn) return;
    const labelNode = btn.querySelector("span:last-child");
    if (labelNode) {
      labelNode.textContent = t(key);
    } else {
      btn.textContent = t(key);
    }
  };
  setInfoActionLabel("infoEmailBtn", "appInfoOpenEmail");
  setInfoActionLabel("infoGithubBtn", "appInfoOpenGithub");
  setInfoActionLabel("infoTelegramBtn", "appInfoOpenTelegram");
  setInfoActionLabel("infoProfileBtn", "appInfoOpenProfile");
  setInfoActionLabel("infoCoffeeBtn", "appInfoOpenCoffee");

  const monthLabelNode = document.getElementById("monthInputLabel");
  if (monthLabelNode) {
    const fmt = formatDateFormatForUi(desktopSettings().dateFormat);
    const labelPrefix = resolvedLangCode() === "en" ? "Date" : "Datum";
    monthLabelNode.textContent = `${labelPrefix} (${fmt})`;
  }

  if (el.monthInput) el.monthInput.placeholder = t("datePlaceholder");
  if (el.monthPickerBtn) {
    const label = resolvedLangCode() === "en" ? "Select date" : "Datum auswählen";
    el.monthPickerBtn.setAttribute("aria-label", label);
    el.monthPickerBtn.setAttribute("title", label);
  }
  if (el.selectAllBookings) {
    el.selectAllBookings.setAttribute(
      "aria-label",
      resolvedLangCode() === "en" ? "Select all bookings" : "Alle Buchungen auswählen"
    );
  }
  if (el.fSearch) el.fSearch.placeholder = t("search");
  if (el.typeExpenseOption) el.typeExpenseOption.textContent = t("expense");
  if (el.typeIncomeOption) el.typeIncomeOption.textContent = t("income");
  if (el.settingsSortDirection?.options?.length >= 2) {
    el.settingsSortDirection.options[0].textContent = t("newestFirst");
    el.settingsSortDirection.options[1].textContent = t("oldestFirst");
  }
  if (el.settingsDateFormat?.options?.length >= 3) {
    el.settingsDateFormat.options[0].textContent = formatDateFormatForUi("DD.MM.YYYY");
    el.settingsDateFormat.options[1].textContent = formatDateFormatForUi("YYYY-MM-DD");
    el.settingsDateFormat.options[2].textContent = formatDateFormatForUi("MM/DD/YYYY");
  }
  if (el.settingsStartTab?.options?.length >= 6) {
    el.settingsStartTab.options[0].textContent = t("dashboard");
    el.settingsStartTab.options[1].textContent = t("bookings");
    el.settingsStartTab.options[2].textContent = t("reports");
    el.settingsStartTab.options[3].textContent = t("sync");
    el.settingsStartTab.options[4].textContent = t("settings");
    el.settingsStartTab.options[5].textContent = t("about");
  }
  if (el.settingsCategorySuggestions?.options?.length >= 2) {
    el.settingsCategorySuggestions.options[0].textContent = t("active");
    el.settingsCategorySuggestions.options[1].textContent = t("inactive");
  }
  if (el.settingsKeepDateAfterSave?.options?.length >= 2) {
    el.settingsKeepDateAfterSave.options[0].textContent = t("active");
    el.settingsKeepDateAfterSave.options[1].textContent = t("inactive");
  }
  if (el.settingsNavigationAnimationStyle?.options?.length >= 6) {
    el.settingsNavigationAnimationStyle.options[0].textContent = "Slide";
    el.settingsNavigationAnimationStyle.options[1].textContent = "Fade";
    el.settingsNavigationAnimationStyle.options[2].textContent = "Zoom";
    el.settingsNavigationAnimationStyle.options[3].textContent = "Pop";
    el.settingsNavigationAnimationStyle.options[4].textContent = "Rotate";
    el.settingsNavigationAnimationStyle.options[5].textContent = resolvedLangCode() === "en" ? "None" : "Keine";
  }
  if (el.settingsFontSize?.options?.length >= 4) {
    el.settingsFontSize.options[0].textContent = t("normal");
    el.settingsFontSize.options[1].textContent = t("large");
    el.settingsFontSize.options[2].textContent = t("xlarge");
    el.settingsFontSize.options[3].textContent = t("xxlarge");
  }
  if (el.settingsBackupTitle) el.settingsBackupTitle.textContent = t("backupTitle");
  if (el.fMonth?.options?.length) {
    el.fMonth.options[0].textContent = t("monthAll");
  }
  if (el.fYear?.options?.length) {
    el.fYear.options[0].textContent = t("yearAll");
  }
  if (el.fCategory?.options?.length) {
    el.fCategory.options[0].textContent = t("categoryAll");
  }
  if (el.fAccount?.options?.length) {
    el.fAccount.options[0].textContent = t("accountAll");
  }

  if (el.reportExportMonth) {
    const previous = el.reportExportMonth.value;
    initReportExportMonthOptions();
    if (previous) el.reportExportMonth.value = previous;
  }

  refreshCategoryOptions(true);
  refreshAccountOptions(true);
  refreshYearFilterOptions();
}

function isAppLocked() {
  return desktopSettings().appLockEnabled && !window.__FT_UNLOCKED__;
}

function applyAppLockState() {
  const overlay = el.appLockOverlay;
  if (!overlay) return;
  const locked = isAppLocked();
  overlay.classList.toggle("hidden", !locked);
  overlay.setAttribute("aria-hidden", locked ? "false" : "true");
  document.body.classList.toggle("app-locked", locked);
  if (locked && el.appLockPinInput) {
    setTimeout(() => {
      el.appLockPinInput.focus();
      el.appLockPinInput.select();
    }, 0);
  }
}


function sanitizeLoadedState(loaded) {
  if (!loaded || typeof loaded !== "object") return createDefaultState();

  const users = Array.isArray(loaded.users) ? loaded.users.filter(u => u && u.id && u.name) : [];
  const customCategories = sanitizeCustomCategories(loaded.customCategories);
  const bookings = Array.isArray(loaded.bookings)
    ? loaded.bookings.filter(b => b && typeof b === "object").map(entry => normalizeBookingCategory(entry, customCategories))
    : [];

  if (users.length === 0) return createDefaultState();

  const activeUserId = users.some(u => u.id === loaded.activeUserId) ? loaded.activeUserId : users[0].id;
  const desktopSettingsSanitized = sanitizeDesktopSettings(loaded.desktopSettings);
  return { users, activeUserId, bookings, customCategories, desktopSettings: desktopSettingsSanitized };
}

function applyLoadedStateToUi(loadedState, options = {}) {
  const { keepMonth = true } = options;

  Object.assign(state, sanitizeLoadedState(loadedState));
  stateDataVersion += 1;
  resetPerfCache();

  bookingRenderState.key = "";
  bookingRenderState.visibleCount = BOOKING_RENDER_INITIAL;
  monthlyChartState.bars = [];
  monthlyChartState.rows = [];
  monthlyChartState.hoverIndex = -1;
  monthlyChartState.pinnedIndex = null;

  ensureActiveUser();
  selectedBookingId = null;
  selectedBookingIds.clear();
  window.__FT_UNLOCKED__ = !desktopSettings().appLockEnabled;
  applyDesktopSettings();
  applyLanguageToUi();
  applyAppLockState();
  clearForm(keepMonth);
  render();
}

async function hydrateStateFromStorage() {
  const isTauri = hasTauriRuntime();

  try {
    const dbPayload = await tryInvokeTauriCommand("db_load_state", {});
    if (typeof dbPayload === "string" && dbPayload.trim()) {
      try {
        const parsed = JSON.parse(dbPayload);
        Object.assign(state, sanitizeLoadedState(parsed));
      } catch (parseErr) {
        queueRecoveryNotice("Die gespeicherten Daten in SQLite sind beschädigt. Es wird auf lokale Sicherung/Fallback gewechselt.");
        console.warn("SQLite-Inhalt ist ungültig, nutze Fallback", parseErr);
      }

      if (state.users.length > 0 && isTauri) {
        markLegacyMigrationDone();
        tryCleanupLegacyStorage();
        return;
      }
    }
  } catch (err) {
    const msg = String(err || "").toLowerCase();
    if (msg.includes("gesperrt") || msg.includes("locked") || msg.includes("busy")) {
      queueRecoveryNotice("SQLite ist aktuell gesperrt. Die App nutzt vorübergehend den lokalen Fallback und versucht später erneut zu speichern.");
    }
    console.warn("SQLite-Laden fehlgeschlagen, nutze Fallback", err);
  }

  try {
    const raw = localStorage.getItem(STORAGE_KEY);
    if (raw) {
      const parsed = JSON.parse(raw);
      Object.assign(state, sanitizeLoadedState(parsed));
      const backend = await persistState();

      if (backend === "tauri") {
        markLegacyMigrationDone();
        tryCleanupLegacyStorage();
      }
      return;
    }
  } catch (err) {
    console.warn("localStorage-Migration fehlgeschlagen", err);
  }

  Object.assign(state, createDefaultState());
  await persistState();
}

function resetPerfCache() {
  perfCache.version = -1;
  perfCache.userId = null;
  perfCache.userBookings = [];
  perfCache.reportRowsByYear.clear();
  perfCache.filteredSorted = { key: "", value: [] };
}

function flushPersistQueue() {
  if (persistInFlight) {
    persistQueued = true;
    return;
  }

  persistInFlight = true;
  persistState()
    .catch(err => console.error("Speichern fehlgeschlagen", err))
    .finally(() => {
      persistInFlight = false;
      if (!persistQueued) return;
      persistQueued = false;
      flushPersistQueue();
    });
}

function saveState() {
  if (!stateReady) return;

  stateDataVersion += 1;
  resetPerfCache();

  if (saveTimer) clearTimeout(saveTimer);
  saveTimer = setTimeout(() => {
    saveTimer = null;
    flushPersistQueue();
  }, 80);
}

async function persistState() {
  const payload = JSON.stringify(state);

  try {
    const viaTauri = await tryInvokeTauriCommand("db_save_state", { payload });
    if (viaTauri !== null) {
      try {
        localStorage.removeItem(RECOVERY_FALLBACK_KEY);
      } catch (_) {}
      ensureDailyBackup(payload).catch(err => console.warn("Daily-Backup fehlgeschlagen", err));
      scheduleSyncAutoBackup(payload);
      return "tauri";
    }
  } catch (err) {
    const msg = String(err || "");
    console.warn("SQLite-Schreiben fehlgeschlagen, nutze lokalen Fallback", err);

    if (msg.toLowerCase().includes("gesperrt") || msg.toLowerCase().includes("locked") || msg.toLowerCase().includes("busy")) {
      queueRecoveryNotice("SQLite ist gerade gesperrt. Änderungen wurden lokal zwischengespeichert.");
    } else if (msg.toLowerCase().includes("json") || msg.toLowerCase().includes("datenstruktur")) {
      queueRecoveryNotice("Ein Speicherformat-Fehler wurde erkannt. Änderungen wurden lokal zwischengespeichert.");
    } else {
      queueRecoveryNotice("SQLite-Schreiben ist fehlgeschlagen. Änderungen wurden lokal zwischengespeichert.");
    }
  }

  localStorage.setItem(STORAGE_KEY, payload);
  localStorage.setItem(RECOVERY_FALLBACK_KEY, String(Date.now()));
  ensureDailyBackup(payload).catch(err => console.warn("Daily-Backup fehlgeschlagen", err));
  scheduleSyncAutoBackup(payload);
  return "local";
}

function hasTauriRuntime() {
  const w = window;
  return Boolean(w.__TAURI__?.core?.invoke || w.__TAURI_INTERNALS__?.invoke);
}

function markLegacyMigrationDone() {
  try {
    localStorage.setItem(LEGACY_MIGRATED_KEY, String(Date.now()));
  } catch (_) {}
}

function tryCleanupLegacyStorage() {
  try {
    localStorage.removeItem(STORAGE_KEY);
  } catch (_) {}
}

function todayBackupKey() {
  const d = new Date();
  const yyyy = d.getFullYear();
  const mm = String(d.getMonth() + 1).padStart(2, "0");
  const dd = String(d.getDate()).padStart(2, "0");
  return yyyy + "-" + mm + "-" + dd;
}

async function ensureDailyBackup(payload) {
  if (!hasTauriRuntime()) return;

  const key = todayBackupKey();
  try {
    if (localStorage.getItem(DAILY_BACKUP_KEY) === key) return;
  } catch (_) {}

  const filename = "state-backup-" + key + ".json";
  const written = await tryInvokeTauriCommand("db_write_backup", { filename, payload });

  if (written) {
    try {
      localStorage.setItem(DAILY_BACKUP_KEY, key);
    } catch (_) {}
  }
}

function queueRecoveryNotice(message) {
  if (!message) return;
  if (!recoveryNotice) {
    recoveryNotice = message;
  }
  if (stateReady) {
    setTimeout(() => {
      flushRecoveryNotice().catch(err => console.error("Recovery-Hinweis konnte nicht angezeigt werden", err));
    }, 0);
  }
}

async function flushRecoveryNotice() {
  if (!recoveryNotice || !stateReady) return;
  const message = recoveryNotice;
  recoveryNotice = null;
  await showInfo(message, resolvedLangCode() === "en" ? "Recovery" : "Wiederherstellung");
}

function uid() {
  return Math.random().toString(36).slice(2, 10);
}
function ensureActiveUser() {
  if (!state.users.length) {
    const user = defaultUser();
    state.users.push(user);
    state.activeUserId = user.id;
    saveState();
    return;
  }
  if (!state.users.some(u => u.id === state.activeUserId)) {
    state.activeUserId = state.users[0].id;
    saveState();
  }
}

function activeUser() {
  ensureActiveUser();
  return state.users.find(u => u.id === state.activeUserId) || state.users[0];
}

function setDefaultMonth() {
  const d = new Date();
  const dd = String(d.getDate()).padStart(2, "0");
  const mm = String(d.getMonth() + 1).padStart(2, "0");
  const canonical = dd + "." + mm + "." + d.getFullYear();
  el.monthInput.value = formatCanonicalDate(canonical);
}

function setDefaultReportYear() {
  const now = new Date();
  el.reportYearInput.value = String(now.getFullYear());
  if (el.reportExportMonth) {
    el.reportExportMonth.value = String(now.getMonth() + 1).padStart(2, "0");
  }
  updateReportExportControls();
}

function initBookingFormErrorSlots() {
  if (!el.bookingForm) return;
  el.bookingForm.querySelectorAll("label").forEach(label => {
    if (!label.querySelector(".field-error")) {
      const node = document.createElement("div");
      node.className = "field-error";
      label.appendChild(node);
    }
  });
}

function ensureFieldErrorNode(inputEl) {
  if (!inputEl) return null;
  const label = inputEl.closest("label");
  if (!label) return null;

  let node = label.querySelector(".field-error");
  if (!node) {
    node = document.createElement("div");
    node.className = "field-error";
    node.textContent = "";
    label.appendChild(node);
  }
  return node;
}

function setFieldError(inputEl, message) {
  if (!inputEl) return;
  const node = ensureFieldErrorNode(inputEl);
  inputEl.classList.add("invalid-field");
  inputEl.style.setProperty("border-color", "#dc2626", "important");
  inputEl.style.setProperty("background", "#fff6f6", "important");
  inputEl.style.setProperty("box-shadow", "0 0 0 3px rgba(220, 38, 38, 0.18)", "important");
  inputEl.style.setProperty("outline", "none", "important");
  if (node) {
    node.textContent = message || "";
    node.classList.toggle("has-error", Boolean(message));
  }
}

function clearFieldError(inputEl) {
  if (!inputEl) return;
  const node = ensureFieldErrorNode(inputEl);
  inputEl.classList.remove("invalid-field");
  inputEl.style.removeProperty("border-color");
  inputEl.style.removeProperty("background");
  inputEl.style.removeProperty("box-shadow");
  inputEl.style.removeProperty("outline");
  if (node) {
    node.textContent = "";
    node.classList.remove("has-error");
  }
}

function clearBookingFormErrors() {
  BOOKING_VALIDATION_FIELDS.forEach(key => clearFieldError(el[key]));
}

function initReportExportMonthOptions() {
  if (!el.reportExportMonth) return;
  el.reportExportMonth.innerHTML = "";
  const names = monthNamesForUi();

  for (let month = 1; month <= 12; month++) {
    const option = document.createElement("option");
    const monthValue = String(month).padStart(2, "0");
    option.value = monthValue;
    option.textContent = monthValue + " - " + names[month - 1];
    el.reportExportMonth.appendChild(option);
  }
}

function initSelectOptions() {
  refreshCategoryOptions(false);
  refreshAccountOptions(false);

  fillSelect(el.fMonth, [t("monthAll"), ...Array.from({ length: 12 }, (_, i) => `${String(i + 1).padStart(2, "0")}`)], true);
  fillSelect(el.fYear, [t("yearAll"), String(new Date().getFullYear())], true);
  initReportExportMonthOptions();
  if (el.reportExportScope) el.reportExportScope.value = "summary";
  updateReportExportControls();
}

function fillSelect(select, values, withAllPrefix = false) {
  select.innerHTML = "";
  values.forEach((v, idx) => {
    const option = document.createElement("option");
    if (withAllPrefix && idx === 0) {
      option.value = "Alle";
      option.textContent = v;
    } else {
      option.value = v;
      option.textContent = v;
    }
    select.appendChild(option);
  });
}

function isBuiltInCategory(name) {
  return CATEGORIES.includes(String(name || ""));
}

function refreshCategoryOptions(keepSelection = true) {
  const categories = allCategories(state.customCategories);
  const previousInput = keepSelection ? el.categoryInput.value : "";
  const previousFilter = keepSelection ? el.fCategory.value : "Alle";

  el.categoryInput.innerHTML = "";
  categories.forEach(cat => {
    const option = document.createElement("option");
    option.value = cat;
    option.textContent = categoryLabelForUi(cat);
    el.categoryInput.appendChild(option);
  });

  el.fCategory.innerHTML = "";
  const allOption = document.createElement("option");
  allOption.value = "Alle";
  allOption.textContent = t("categoryAll");
  el.fCategory.appendChild(allOption);
  categories.forEach(cat => {
    const option = document.createElement("option");
    option.value = cat;
    option.textContent = categoryLabelForUi(cat);
    el.fCategory.appendChild(option);
  });

  const selectedInput = categories.includes(previousInput) ? previousInput : "Sonstiges";
  el.categoryInput.value = selectedInput;

  if (previousFilter === "Alle") {
    el.fCategory.value = "Alle";
  } else {
    el.fCategory.value = categories.includes(previousFilter) ? previousFilter : "Alle";
  }
}

function refreshAccountOptions(keepSelection = true) {
  const previousInput = keepSelection ? el.accountInput.value : "Girokonto";
  const previousFilter = keepSelection ? el.fAccount.value : "Alle";

  el.accountInput.innerHTML = "";
  ACCOUNTS.forEach(acc => {
    const option = document.createElement("option");
    option.value = acc;
    option.textContent = accountLabelForUi(acc);
    el.accountInput.appendChild(option);
  });

  el.fAccount.innerHTML = "";
  const allOption = document.createElement("option");
  allOption.value = "Alle";
  allOption.textContent = t("accountAll");
  el.fAccount.appendChild(allOption);
  ACCOUNTS.forEach(acc => {
    const option = document.createElement("option");
    option.value = acc;
    option.textContent = accountLabelForUi(acc);
    el.fAccount.appendChild(option);
  });

  el.accountInput.value = ACCOUNTS.includes(previousInput) ? previousInput : "Girokonto";
  el.fAccount.value = previousFilter === "Alle" ? "Alle" : (ACCOUNTS.includes(previousFilter) ? previousFilter : "Alle");
}

function resetFilters() {
  el.fMonth.value = "Alle";
  el.fYear.value = "Alle";
  el.fType.value = "Alle";
  el.fCategory.value = "Alle";
  el.fAccount.value = "Alle";
  el.fSearch.value = "";
}


function activateTab(tabId, options = {}) {
  const { animate = true } = options;
  const nextId = ["dashboard", "bookings", "reports", "synchronisierung", "settings", "info"].includes(tabId)
    ? tabId
    : "dashboard";
  const nextPanel = document.getElementById(nextId);
  if (!nextPanel) return;

  document.querySelectorAll(".tab").forEach(tabBtn => {
    tabBtn.classList.toggle("active", tabBtn.dataset.tab === nextId);
  });
  document.querySelectorAll(".tab-panel").forEach(panel => panel.classList.remove("active"));
  nextPanel.classList.add("active");

  const style = desktopSettings().navigationAnimationStyle || "slide";
  if (animate && style !== "none") {
    nextPanel.classList.add("tab-anim", `tab-anim-${style}`);
    const onDone = () => {
      nextPanel.classList.remove("tab-anim", `tab-anim-${style}`);
      nextPanel.removeEventListener("animationend", onDone);
    };
    nextPanel.addEventListener("animationend", onDone);
  }

  activeTabId = nextId;
}

function bindTabs() {
  document.querySelectorAll(".tab").forEach(btn => {
    btn.addEventListener("click", () => {
      activateTab(btn.dataset.tab, { animate: true });
    });
  });
}

function bindEvents() {
  el.userSelect.addEventListener("change", () => {
    state.activeUserId = el.userSelect.value;
    selectedBookingId = null;
    selectedBookingIds.clear();
    clearForm(true);
    saveState();
    render();
  });

  el.addUserBtn.addEventListener("click", async () => {
    const name = await askText(
      resolvedLangCode() === "en" ? "Name of new user:" : "Name des neuen Benutzers:",
      resolvedLangCode() === "en" ? "New User" : "Neuer Benutzer"
    );
    if (name === null) return;
    const trimmed = name.trim();
    if (!trimmed) return;
    if (state.users.some(u => u.name.toLowerCase() === trimmed.toLowerCase())) {
      await showInfo(resolvedLangCode() === "en" ? "User already exists." : "Benutzer existiert bereits.");
      return;
    }
    const user = { id: uid(), name: trimmed };
    state.users.push(user);
    state.activeUserId = user.id;
    selectedBookingId = null;
    clearForm(true);
    saveState();
    render();
    showToast(resolvedLangCode() === "en" ? "User created." : "Benutzer angelegt.", "success");
  });

  el.renameUserBtn.addEventListener("click", async () => {
    const user = activeUser();
    const name = await askText(
      resolvedLangCode() === "en" ? "New name:" : "Neuer Name:",
      resolvedLangCode() === "en" ? "Rename User" : "Benutzer umbenennen",
      user.name
    );
    if (name === null) return;

    const trimmed = name.trim();
    if (!trimmed) {
      await showInfo(resolvedLangCode() === "en" ? "Please enter a name." : "Bitte einen Namen eingeben.");
      return;
    }
    if (state.users.some(u => u.id !== user.id && u.name.toLowerCase() === trimmed.toLowerCase())) {
      await showInfo(resolvedLangCode() === "en" ? "User already exists." : "Benutzer existiert bereits.");
      return;
    }

    user.name = trimmed;
    saveState();
    renderUsers();
    showToast(resolvedLangCode() === "en" ? "User renamed." : "Benutzer umbenannt.", "success");
  });

  el.deleteUserBtn.addEventListener("click", async () => {
    if (state.users.length <= 1) {
      await showInfo(resolvedLangCode() === "en" ? "The last user cannot be deleted." : "Der letzte Benutzer kann nicht gelöscht werden.");
      return;
    }

    const user = activeUser();
    const count = state.bookings.filter(b => b.userId === user.id).length;

    if (count > 0) {
      const ok = await askConfirm(
        resolvedLangCode() === "en"
          ? `Are you sure you want to delete user '${user.name}'?\n${count} bookings will be deleted.`
          : `Sind Sie sicher, dass Sie Benutzer '${user.name}' löschen möchten?\nEs werden ${count} Buchungen gelöscht.`,
        resolvedLangCode() === "en" ? "Delete User" : "Benutzer löschen",
        true
      );
      if (!ok) return;
    }

    state.bookings = state.bookings.filter(b => b.userId !== user.id);
    state.users = state.users.filter(u => u.id !== user.id);
    state.activeUserId = state.users[0].id;
    selectedBookingId = null;
    clearForm(true);
    saveState();
    render();
    showToast(
      resolvedLangCode() === "en" ? `'${user.name}' was deleted.` : `'${user.name}' wurde gelöscht.`,
      "success"
    );
  });

  el.descriptionInput.addEventListener("input", () => {
    if (!desktopSettings().categorySuggestions) return;
    if (categoryManuallyOverridden) return;
    const suggestion = suggestCategory(el.descriptionInput.value);
    if (!suggestion) return;
    if (el.categoryInput.value === "Sonstiges" || el.categoryInput.value === lastAutoCategory) {
      el.categoryInput.value = suggestion;
      lastAutoCategory = suggestion;
    }
  });

  el.categoryInput.addEventListener("change", () => {
    if (el.categoryInput.value !== lastAutoCategory) categoryManuallyOverridden = true;
  });

  [el.monthInput, el.descriptionInput, el.amountInput].forEach(input => {
    input.addEventListener("input", () => clearFieldError(input));
  });

  if (el.monthPickerBtn && el.monthPickerNative && el.monthInput) {
    el.monthPickerBtn.addEventListener("click", () => {
      const currentIso = dmyToIsoDate(el.monthInput.value);
      if (currentIso) el.monthPickerNative.value = currentIso;
      else el.monthPickerNative.value = dmyToIsoDate(parseMonth(el.monthInput.value)) || "";

      if (typeof el.monthPickerNative.showPicker === "function") {
        el.monthPickerNative.showPicker();
      } else {
        el.monthPickerNative.click();
      }
    });

    el.monthPickerNative.addEventListener("change", () => {
      const next = isoDateToDmy(el.monthPickerNative.value);
      if (!next) return;
      el.monthInput.value = formatCanonicalDate(next);
      clearFieldError(el.monthInput);
    });
  }

  el.addCategoryBtn.addEventListener("click", async () => {
    const name = await askText(
      resolvedLangCode() === "en" ? "Name of new category:" : "Name der neuen Kategorie:",
      resolvedLangCode() === "en" ? "Create Category" : "Kategorie anlegen"
    );
    if (name === null) return;

    const trimmed = name.trim();
    if (!trimmed) {
      await showInfo(resolvedLangCode() === "en" ? "Please enter a category name." : "Bitte einen Kategorienamen eingeben.");
      return;
    }

    const exists = allCategories(state.customCategories).some(c => c.toLowerCase() === trimmed.toLowerCase());
    if (exists) {
      await showInfo(resolvedLangCode() === "en" ? "This category already exists." : "Diese Kategorie existiert bereits.");
      return;
    }

    state.customCategories = sanitizeCustomCategories([...(state.customCategories || []), trimmed]);
    refreshCategoryOptions(true);
    el.categoryInput.value = trimmed;
    saveState();
    render();
    showToast(resolvedLangCode() === "en" ? "Category created." : "Kategorie angelegt.", "success");
  });

  el.renameCategoryBtn.addEventListener("click", async () => {
    const selected = String(el.categoryInput.value || "").trim();
    if (!selected) return;

    if (isBuiltInCategory(selected)) {
      await showInfo(resolvedLangCode() === "en" ? "Built-in categories cannot be renamed." : "Standard-Kategorien können nicht umbenannt werden.");
      return;
    }

    const nextName = await askText(
      resolvedLangCode() === "en" ? "New category name:" : "Neuer Kategoriename:",
      resolvedLangCode() === "en" ? "Rename Category" : "Kategorie umbenennen",
      selected
    );
    if (nextName === null) return;

    const trimmed = nextName.trim();
    if (!trimmed) {
      await showInfo(resolvedLangCode() === "en" ? "Please enter a category name." : "Bitte einen Kategorienamen eingeben.");
      return;
    }

    const duplicate = allCategories(state.customCategories).some(c => c.toLowerCase() === trimmed.toLowerCase() && c.toLowerCase() !== selected.toLowerCase());
    if (duplicate) {
      await showInfo(resolvedLangCode() === "en" ? "This category already exists." : "Diese Kategorie existiert bereits.");
      return;
    }

    state.customCategories = sanitizeCustomCategories((state.customCategories || []).map(c => c === selected ? trimmed : c));
    state.bookings = state.bookings.map(entry => entry.category === selected
      ? { ...entry, category: normalizeCategory(trimmed, state.customCategories) }
      : entry);

    if (selectedBookingId) {
      const selectedEntry = state.bookings.find(b => b.id === selectedBookingId && b.userId === activeUser().id);
      if (selectedEntry) loadEntryIntoForm(selectedEntry);
    }

    refreshCategoryOptions(true);
    el.categoryInput.value = normalizeCategory(trimmed, state.customCategories);
    saveState();
    render();
    showToast(resolvedLangCode() === "en" ? "Category renamed." : "Kategorie umbenannt.", "success");
  });

  el.deleteCategoryBtn.addEventListener("click", async () => {
    const selected = String(el.categoryInput.value || "").trim();
    if (!selected) return;

    if (isBuiltInCategory(selected)) {
      await showInfo(resolvedLangCode() === "en" ? "Built-in categories cannot be deleted." : "Standard-Kategorien können nicht gelöscht werden.");
      return;
    }

    const usageCount = state.bookings.filter(entry => entry.category === selected).length;
    const msg = usageCount > 0
      ? (resolvedLangCode() === "en"
        ? `Delete category '${selected}'?\n${usageCount} booking(s) will be moved to 'Sonstiges'.`
        : "Kategorie '" + selected + "' löschen?\n" + usageCount + " Buchung(en) werden auf 'Sonstiges' gesetzt.")
      : (resolvedLangCode() === "en" ? `Delete category '${selected}'?` : "Kategorie '" + selected + "' löschen?");
    const ok = await askConfirm(msg, resolvedLangCode() === "en" ? "Delete Category" : "Kategorie löschen", true);
    if (!ok) return;

    state.customCategories = sanitizeCustomCategories((state.customCategories || []).filter(c => c !== selected));
    state.bookings = state.bookings.map(entry => entry.category === selected
      ? { ...entry, category: "Sonstiges" }
      : entry);

    if (selectedBookingId) {
      const selectedEntry = state.bookings.find(b => b.id === selectedBookingId && b.userId === activeUser().id);
      if (selectedEntry) loadEntryIntoForm(selectedEntry);
    }

    refreshCategoryOptions(true);
    saveState();
    render();
    showToast(resolvedLangCode() === "en" ? "Category deleted." : "Kategorie gelöscht.", "success");
  });

  el.bookingForm.addEventListener("submit", async e => {
    e.preventDefault();
    const payload = await validateBookingForm();
    if (!payload) return;

    let updated = false;

    if (selectedBookingId) {
      const idx = state.bookings.findIndex(b => b.id === selectedBookingId && b.userId === activeUser().id);
      if (idx >= 0) {
        state.bookings[idx] = { ...state.bookings[idx], ...payload };
        updated = true;
      } else {
        state.bookings.push({ id: uid(), userId: activeUser().id, createdAt: Date.now(), ...payload });
      }
    } else {
      state.bookings.push({ id: uid(), userId: activeUser().id, createdAt: Date.now(), ...payload });
    }

    saveState();
    selectedBookingId = null;
    clearForm(Boolean(desktopSettings().keepDateAfterSave));
    render();
    showToast(
      updated
        ? (resolvedLangCode() === "en" ? "Booking updated." : "Buchung aktualisiert.")
        : (resolvedLangCode() === "en" ? "Booking saved." : "Buchung gespeichert."),
      "success"
    );
  });

  el.bookingForm.addEventListener("reset", e => {
    e.preventDefault();
    selectedBookingId = null;
    clearForm(false);
    clearBookingFormErrors();
    renderBookings();
  });

  el.deleteSelectedBookingsBtn.addEventListener("click", async () => {
    const ids = Array.from(selectedBookingIds);
    if (!ids.length) {
      await showInfo(resolvedLangCode() === "en" ? "Please select at least one booking." : "Bitte mindestens eine Buchung markieren.");
      return;
    }

    const ok = await askConfirm(
      resolvedLangCode() === "en"
        ? `Are you sure you want to delete ${ids.length} selected booking(s)?`
        : `Sind Sie sicher, dass Sie ${ids.length} ausgewählte Buchung(en) löschen möchten?`,
      resolvedLangCode() === "en" ? "Delete Multiple Bookings" : "Mehrere Buchungen löschen",
      true
    );
    if (!ok) return;

    state.bookings = state.bookings.filter(b => !(b.userId === activeUser().id && selectedBookingIds.has(b.id)));
    selectedBookingIds.clear();
    selectedBookingId = null;
    saveState();
    render();
    showToast(
      resolvedLangCode() === "en" ? `${ids.length} booking(s) deleted.` : ids.length + " Buchung(en) gelöscht.",
      "success"
    );
  });

  el.selectAllBookings.addEventListener("change", () => {
    const entries = filteredAndSortedBookings();

    if (el.selectAllBookings.checked) {
      entries.forEach(e => selectedBookingIds.add(e.id));
    } else {
      entries.forEach(e => selectedBookingIds.delete(e.id));
    }

    renderBookings();
  });

  [el.fMonth, el.fYear, el.fType, el.fCategory, el.fAccount, el.fSearch].forEach(i => i.addEventListener("input", renderBookings));
  el.resetFiltersBtn.addEventListener("click", e => {
    e.preventDefault();
    resetFilters();
    renderBookings();
  });

  if (el.bookingsTableWrap) {
    el.bookingsTableWrap.addEventListener("scroll", () => {
      const entries = filteredAndSortedBookings();
      if (entries.length <= bookingRenderState.visibleCount) return;

      const remaining = el.bookingsTableWrap.scrollHeight - el.bookingsTableWrap.clientHeight - el.bookingsTableWrap.scrollTop;
      if (remaining > 140) return;

      bookingRenderState.visibleCount = Math.min(entries.length, bookingRenderState.visibleCount + BOOKING_RENDER_STEP);
      renderBookings();
    });
  }

  if (el.dashboardYearSelect) {
    el.dashboardYearSelect.addEventListener("change", () => {
      renderDashboard();
    });
  }
  if (el.dashboardTopMonthSelect) {
    el.dashboardTopMonthSelect.addEventListener("change", () => {
      renderDashboard();
    });
  }

  async function autoSaveSyncFolderPath(folderPath, showErrorDialog = true) {
    if (!hasTauriRuntime()) return false;

    const trimmed = String(folderPath || "").trim();
    if (!trimmed) return false;

    try {
      await tryInvokeTauriCommand("sync_set_folder", { folderPath: trimmed, folder_path: trimmed });
      await refreshSyncStatus();
      return true;
    } catch (err) {
      if (showErrorDialog) {
        await showInfo(
          (resolvedLangCode() === "en" ? "Folder could not be saved:\n" : "Ordner konnte nicht gespeichert werden:\n") + String(err),
          t("sync")
        );
      }
      return false;
    }
  }

  el.browseSyncFolderBtn.addEventListener("click", async () => {
    if (!hasTauriRuntime()) {
      await showInfo(
        resolvedLangCode() === "en" ? "Folder picker is only available in desktop app." : "Ordner-Auswahl ist nur in der Desktop-App verfügbar.",
        t("sync")
      );
      return;
    }

    try {
      const selected = await tryInvokeTauriCommand("sync_pick_folder", {});
      if (!selected || typeof selected !== "string") return;
      el.syncFolderInput.value = selected;
      await autoSaveSyncFolderPath(selected, true);
    } catch (err) {
      await showInfo(
        (resolvedLangCode() === "en" ? "Folder selection failed:\n" : "Ordnerauswahl fehlgeschlagen:\n") + String(err),
        t("sync")
      );
    }
  });

  el.syncFolderInput.addEventListener("change", async () => {
    const folderPath = (el.syncFolderInput?.value || "").trim();
    if (!folderPath) return;
    await autoSaveSyncFolderPath(folderPath, true);
  });


  el.syncBackupNowBtn.addEventListener("click", async () => {
    if (!hasTauriRuntime()) {
      await showInfo(
        resolvedLangCode() === "en" ? "Sync folder is only available in desktop app." : "Sync-Ordner ist nur in der Desktop-App verfügbar.",
        t("sync")
      );
      return;
    }

    try {
      const writtenPath = await tryInvokeTauriCommand("sync_write_backup", { payload: JSON.stringify(state) });
      markSyncWriteSuccess();
      await refreshSyncStatus();
      await showInfo(
        (resolvedLangCode() === "en" ? "Backup was written to sync folder:\n" : "Sicherung wurde in den Sync-Ordner geschrieben:\n") + writtenPath,
        t("sync")
      );
    } catch (err) {
      await showInfo(
        (resolvedLangCode() === "en" ? "Sync backup failed:\n" : "Sync-Sicherung fehlgeschlagen:\n") + String(err),
        t("sync")
      );
    }
  });

  el.syncRestoreBtn.addEventListener("click", async () => {
    if (!hasTauriRuntime()) {
      await showInfo(
        resolvedLangCode() === "en" ? "Sync folder is only available in desktop app." : "Sync-Ordner ist nur in der Desktop-App verfügbar.",
        t("sync")
      );
      return;
    }

    const ok = await askConfirm(
      resolvedLangCode() === "en"
        ? "Load latest backup from sync folder?\nLocal data will be overwritten."
        : "Neueste Sicherung aus dem Sync-Ordner laden?\nLokale Daten werden dadurch überschrieben.",
      resolvedLangCode() === "en" ? "Sync Restore" : "Sync-Wiederherstellung",
      false
    );
    if (!ok) return;

    try {
      const raw = await tryInvokeTauriCommand("sync_restore_latest", {});
      if (!raw || typeof raw !== "string") {
        await showInfo(resolvedLangCode() === "en" ? "No backup found." : "Keine Sicherung gefunden.", t("sync"));
        return;
      }

      const parsed = JSON.parse(raw);
      applyLoadedStateToUi(parsed, { keepMonth: true });
      markSyncRestoreSuccess();
      saveState();

      await showInfo(
        resolvedLangCode() === "en" ? "Latest sync backup was loaded." : "Neueste Sync-Sicherung wurde geladen.",
        t("sync")
      );
    } catch (err) {
      await showInfo(
        (resolvedLangCode() === "en" ? "Restore failed:\n" : "Wiederherstellung fehlgeschlagen:\n") + String(err),
        t("sync")
      );
    }
  });

  if (el.reportYearInput) {
    const refreshReport = () => renderReport();
    el.reportYearInput.addEventListener("change", refreshReport);
    el.reportYearInput.addEventListener("blur", refreshReport);
    el.reportYearInput.addEventListener("keydown", evt => {
      if (evt.key !== "Enter") return;
      evt.preventDefault();
      refreshReport();
    });
  }

  if (el.reportExportScope) {
    el.reportExportScope.addEventListener("change", () => updateReportExportControls());
  }

  if (el.settingsLanguage) {
    el.settingsLanguage.addEventListener("change", () => {
      state.desktopSettings = sanitizeDesktopSettings({ ...desktopSettings(), language: el.settingsLanguage.value });
      applyLanguageToUi();
      saveState();
      render();
    });
  }

  if (el.settingsDateFormat) {
    el.settingsDateFormat.addEventListener("change", () => {
      const currentCanonical = parseMonth(el.monthInput.value);
      state.desktopSettings = sanitizeDesktopSettings({ ...desktopSettings(), dateFormat: el.settingsDateFormat.value });
      if (currentCanonical) el.monthInput.value = formatCanonicalDate(currentCanonical);
      saveState();
      render();
    });
  }

  if (el.settingsCurrency) {
    el.settingsCurrency.addEventListener("change", () => {
      state.desktopSettings = sanitizeDesktopSettings({ ...desktopSettings(), currency: el.settingsCurrency.value });
      saveState();
      render();
    });
  }

  if (el.settingsSortDirection) {
    el.settingsSortDirection.addEventListener("change", () => {
      state.desktopSettings = sanitizeDesktopSettings({ ...desktopSettings(), sortDirection: el.settingsSortDirection.value });
      saveState();
      renderBookings();
    });
  }

  if (el.settingsStartTab) {
    el.settingsStartTab.addEventListener("change", () => {
      state.desktopSettings = sanitizeDesktopSettings({ ...desktopSettings(), startTab: el.settingsStartTab.value });
      saveState();
      activateTab(state.desktopSettings.startTab, { animate: true });
    });
  }

  if (el.settingsDefaultExportFormat) {
    el.settingsDefaultExportFormat.addEventListener("change", () => {
      state.desktopSettings = sanitizeDesktopSettings({
        ...desktopSettings(),
        defaultExportFormat: el.settingsDefaultExportFormat.value
      });
      if (el.reportExportFormat) {
        el.reportExportFormat.value = state.desktopSettings.defaultExportFormat;
      }
      saveState();
    });
  }

  if (el.settingsKeepDateAfterSave) {
    el.settingsKeepDateAfterSave.addEventListener("change", () => {
      state.desktopSettings = sanitizeDesktopSettings({
        ...desktopSettings(),
        keepDateAfterSave: String(el.settingsKeepDateAfterSave.value) === "active"
      });
      saveState();
    });
  }

  if (el.settingsCategorySuggestions) {
    el.settingsCategorySuggestions.addEventListener("change", () => {
      state.desktopSettings = sanitizeDesktopSettings({
        ...desktopSettings(),
        categorySuggestions: String(el.settingsCategorySuggestions.value) === "active"
      });
      saveState();
    });
  }

  if (el.settingsFontSize) {
    el.settingsFontSize.addEventListener("change", () => {
      state.desktopSettings = sanitizeDesktopSettings({ ...desktopSettings(), fontSize: el.settingsFontSize.value });
      applyDesktopSettings();
      saveState();
    });
  }

  if (el.settingsNavigationAnimationStyle) {
    el.settingsNavigationAnimationStyle.addEventListener("change", () => {
      state.desktopSettings = sanitizeDesktopSettings({
        ...desktopSettings(),
        navigationAnimationStyle: el.settingsNavigationAnimationStyle.value
      });
      saveState();
    });
  }

  if (el.settingsAppLock) {
    el.settingsAppLock.addEventListener("change", async () => {
      const enabled = Boolean(el.settingsAppLock.checked);
      const settings = desktopSettings();

      if (enabled) {
        let pin = String(settings.appLockPin || "");
        if (!pin) {
          const entered = await askText(t("pinPromptSet"), t("settingsAppLock"));
          if (entered === null) {
            el.settingsAppLock.checked = false;
            return;
          }
          pin = String(entered || "").trim();
          if (pin.length < 4) {
            await showInfo(t("pinTooShort"));
            el.settingsAppLock.checked = false;
            return;
          }
        }

        state.desktopSettings = sanitizeDesktopSettings({
          ...settings,
          appLockEnabled: true,
          appLockPin: pin
        });
        window.__FT_UNLOCKED__ = false;
        applyAppLockState();
        saveState();
        showToast(t("lockEnabled"), "success");
        return;
      }

      const entered = await askText(t("pinPromptDisable"), t("settingsAppLock"));
      if (entered === null || String(entered) !== String(settings.appLockPin || "")) {
        await showInfo(t("pinMismatch"));
        el.settingsAppLock.checked = true;
        return;
      }

      state.desktopSettings = sanitizeDesktopSettings({
        ...settings,
        appLockEnabled: false
      });
      window.__FT_UNLOCKED__ = true;
      applyAppLockState();
      saveState();
      showToast(t("lockDisabled"), "success");
    });
  }

  if (el.appLockUnlockBtn && el.appLockPinInput) {
    const tryUnlock = async () => {
      const pin = String(el.appLockPinInput.value || "");
      if (pin !== String(desktopSettings().appLockPin || "")) {
        await showInfo(t("pinMismatch"));
        el.appLockPinInput.focus();
        el.appLockPinInput.select();
        return;
      }
      window.__FT_UNLOCKED__ = true;
      el.appLockPinInput.value = "";
      applyAppLockState();
    };

    el.appLockUnlockBtn.addEventListener("click", () => {
      tryUnlock().catch(err => console.error(err));
    });

    el.appLockPinInput.addEventListener("keydown", evt => {
      if (evt.key !== "Enter") return;
      evt.preventDefault();
      tryUnlock().catch(err => console.error(err));
    });
  }

  if (el.backupExportBtn) {
    el.backupExportBtn.addEventListener("click", async () => {
      const now = new Date();
      const yyyy = now.getFullYear();
      const mm = String(now.getMonth() + 1).padStart(2, "0");
      const dd = String(now.getDate()).padStart(2, "0");
      const hh = String(now.getHours()).padStart(2, "0");
      const mi = String(now.getMinutes()).padStart(2, "0");
      const ss = String(now.getSeconds()).padStart(2, "0");
      const filename = `finanz-tracker-backup-${yyyy}${mm}${dd}-${hh}${mi}${ss}.json`;
      const content = JSON.stringify(state, null, 2);

      if (hasTauriRuntime()) {
        try {
          const bytes = new TextEncoder().encode(content);
          const writtenPath = await writeBinaryReportViaTauri(filename, bytes);
          if (writtenPath) {
            showToast(t("backupExported") + ": " + writtenPath, "success");
            return;
          }
        } catch (err) {
          const message = String(err || "");
          if (message.includes("EXPORT_CANCELED")) {
            showToast(t("exportCanceled"), "info");
            return;
          }
        }
      }

      triggerDownload(filename, content, "application/json;charset=utf-8");
      showToast(
        resolvedLangCode() === "en" ? "Backup exported as download." : "Backup als Download exportiert.",
        "success"
      );
    });
  }

  if (el.backupImportBtn && el.backupImportInput) {
    el.backupImportBtn.addEventListener("click", () => {
      el.backupImportInput.value = "";
      el.backupImportInput.click();
    });

    el.backupImportInput.addEventListener("change", async () => {
      const file = el.backupImportInput.files?.[0];
      if (!file) return;

      const ok = await askConfirm(
        t("backupImportConfirm"),
        t("backupImportTitle"),
        true
      );
      if (!ok) {
        el.backupImportInput.value = "";
        return;
      }

      try {
        const content = await file.text();
        const parsed = JSON.parse(content);
        applyLoadedStateToUi(parsed, { keepMonth: true });
        saveState();
        showToast(t("backupImported"), "success");
      } catch (err) {
        await showInfo(t("backupImportError"), t("backupTitle"));
      } finally {
        el.backupImportInput.value = "";
      }
    });
  }

  const openExternal = async url => {
    if (!url) return;
    if (hasTauriRuntime()) {
      try {
        await tryInvokeTauriCommand("open_external_url", { url });
        return;
      } catch (_) {
        // fallback below
      }
    }

    const popup = window.open(url, "_blank", "noopener,noreferrer");
    if (!popup) window.location.assign(url);
  };
  el.infoEmailBtn?.addEventListener("click", () => { openExternal("mailto:sichler.daniel@gmail.com"); });
  el.infoGithubBtn?.addEventListener("click", () => { openExternal("https://linktr.ee/darexsh"); });
  el.infoTelegramBtn?.addEventListener("click", () => { openExternal("https://t.me/darexsh_bot"); });
  el.infoProfileBtn?.addEventListener("click", () => { openExternal("https://github.com/Darexsh?tab=repositories"); });
  el.infoCoffeeBtn?.addEventListener("click", () => { openExternal("https://buymeacoffee.com/darexsh"); });

  setupMonthlyChartInteractions();
  window.addEventListener("resize", () => {
    if (!stateReady) return;
    renderDashboard();
  });

  el.exportReportBtn.addEventListener("click", async () => {
    await exportReport();
  });

  document.addEventListener("click", evt => {
    if (isAppLocked()) return;
    if (!selectedBookingId) return;
    const target = evt.target;
    if (!(target instanceof Element)) return;
    if (target.closest('#bookingsBody tr[data-id]')) return;
    if (target.closest('#bookingForm')) return;
    selectedBookingId = null;
    renderBookings();
  });
}

function parseMonth(value) {
  const t = String(value).trim();

  const de = t.match(/^(\d{2})\.(\d{2})\.(\d{4})$/);
  if (de) {
    const dd = Number(de[1]);
    const mm = Number(de[2]);
    const yyyy = Number(de[3]);
    if (dd < 1 || dd > 31 || mm < 1 || mm > 12 || yyyy < 2000 || yyyy > 2100) return null;
    return String(dd).padStart(2, "0") + "." + String(mm).padStart(2, "0") + "." + yyyy;
  }

  const en = t.match(/^(\d{2})\/(\d{2})\/(\d{4})$/);
  if (en) {
    const mm = Number(en[1]);
    const dd = Number(en[2]);
    const yyyy = Number(en[3]);
    if (dd < 1 || dd > 31 || mm < 1 || mm > 12 || yyyy < 2000 || yyyy > 2100) return null;
    return String(dd).padStart(2, "0") + "." + String(mm).padStart(2, "0") + "." + yyyy;
  }

  const ymd = t.match(/^(\d{4})-(\d{2})-(\d{2})$/);
  if (ymd) {
    const yyyy = Number(ymd[1]);
    const mm = Number(ymd[2]);
    const dd = Number(ymd[3]);
    if (dd < 1 || dd > 31 || mm < 1 || mm > 12 || yyyy < 2000 || yyyy > 2100) return null;
    return String(dd).padStart(2, "0") + "." + String(mm).padStart(2, "0") + "." + yyyy;
  }

  const legacy = t.match(/^(\d{2})\.(\d{4})$/);
  if (legacy) {
    const mm = Number(legacy[1]);
    const yyyy = Number(legacy[2]);
    if (mm < 1 || mm > 12 || yyyy < 2000 || yyyy > 2100) return null;
    return "01." + String(mm).padStart(2, "0") + "." + yyyy;
  }

  return null;
}

function getDateParts(dateStr) {
  const normalized = parseMonth(dateStr);
  if (!normalized) return null;
  const parts = normalized.split(".");
  return {
    dd: Number(parts[0]),
    mm: Number(parts[1]),
    yyyy: Number(parts[2]),
    normalized
  };
}

function monthYearKey(dateStr) {
  const p = getDateParts(dateStr);
  if (!p) return null;
  return String(p.mm).padStart(2, "0") + "." + p.yyyy;
}

function parseYear(value) {
  const t = String(value).trim();
  if (!/^\d{4}$/.test(t)) return null;
  const year = Number(t);
  if (year < 2000 || year > 2100) return null;
  return year;
}

function monthSortKey(dateStr) {
  const p = getDateParts(dateStr);
  if (!p) return 0;
  return p.yyyy * 10000 + p.mm * 100 + p.dd;
}

function dmyToIsoDate(dmy) {
  const p = getDateParts(dmy);
  if (!p) return "";
  return `${String(p.yyyy).padStart(4, "0")}-${String(p.mm).padStart(2, "0")}-${String(p.dd).padStart(2, "0")}`;
}

function formatDateFormatForUi(format) {
  const isEnglish = resolvedLangCode() === "en";
  if (format === "YYYY-MM-DD") return isEnglish ? "YYYY-MM-DD" : "JJJJ-MM-TT";
  if (format === "MM/DD/YYYY") return isEnglish ? "MM/DD/YYYY" : "MM/TT/JJJJ";
  return isEnglish ? "DD.MM.YYYY" : "TT.MM.JJJJ";
}

function isoDateToDmy(iso) {
  const m = String(iso || "").match(/^(\d{4})-(\d{2})-(\d{2})$/);
  if (!m) return null;
  const yyyy = Number(m[1]);
  const mm = Number(m[2]);
  const dd = Number(m[3]);
  if (yyyy < 2000 || yyyy > 2100 || mm < 1 || mm > 12 || dd < 1 || dd > 31) return null;
  return `${String(dd).padStart(2, "0")}.${String(mm).padStart(2, "0")}.${String(yyyy).padStart(4, "0")}`;
}

const LEARNED_CATEGORY_STOPWORDS = new Set([
  "der", "die", "das", "den", "dem", "ein", "eine", "einer", "einem", "und", "oder",
  "mit", "ohne", "von", "für", "fuer", "auf", "im", "in", "am", "an", "zu", "zum",
  "zur", "bei", "aus", "ist", "war", "ich", "wir", "ihr", "sie", "er", "es"
]);

function normalizeLearningText(value) {
  return String(value || "")
    .normalize("NFKD")
    .replace(/[̀-ͯ]/g, "")
    .replace(/[^a-zA-Z0-9 ]+/g, " ")
    .toLowerCase()
    .trim();
}

function tokenizeForCategoryLearning(value) {
  const text = normalizeLearningText(value);
  if (!text) return [];

  const raw = text.split(/\s+/g).filter(Boolean);
  const unique = new Set();

  raw.forEach(token => {
    if (token.length < 3) return;
    if (LEARNED_CATEGORY_STOPWORDS.has(token)) return;
    unique.add(token);
  });

  return Array.from(unique);
}

function buildLearnedCategoryModel() {
  const perToken = new Map();
  const totalPerCategory = new Map();

  userBookings().forEach(entry => {
    const category = normalizeCategory(entry.category, state.customCategories);
    const tokens = tokenizeForCategoryLearning(entry.description);
    if (!category || tokens.length === 0) return;

    totalPerCategory.set(category, (totalPerCategory.get(category) || 0) + 1);

    tokens.forEach(token => {
      if (!perToken.has(token)) perToken.set(token, new Map());
      const catMap = perToken.get(token);
      catMap.set(category, (catMap.get(category) || 0) + 1);
    });
  });

  return { perToken, totalPerCategory };
}

function suggestCategoryFromHistory(description) {
  const tokens = tokenizeForCategoryLearning(description);
  if (tokens.length === 0) return null;

  const { perToken, totalPerCategory } = buildLearnedCategoryModel();
  const scoreByCategory = new Map();

  tokens.forEach(token => {
    const catMap = perToken.get(token);
    if (!catMap) return;
    catMap.forEach((score, category) => {
      scoreByCategory.set(category, (scoreByCategory.get(category) || 0) + score);
    });
  });

  if (scoreByCategory.size === 0) return null;

  const ranked = Array.from(scoreByCategory.entries()).sort((a, b) => {
    const byScore = b[1] - a[1];
    if (byScore !== 0) return byScore;
    const byTotal = (totalPerCategory.get(b[0]) || 0) - (totalPerCategory.get(a[0]) || 0);
    if (byTotal !== 0) return byTotal;
    return a[0].localeCompare(b[0], "de");
  });

  return ranked[0]?.[0] || null;
}

function suggestCategory(description) {
  const learned = suggestCategoryFromHistory(description);
  if (learned) return normalizeCategory(learned, state.customCategories);

  const text = normalizeLearningText(description);
  const hit = KEYWORD_MAP.find(([k]) => text.includes(k));
  return hit ? normalizeCategory(hit[1], state.customCategories) : null;
}

async function validateBookingForm() {
  clearBookingFormErrors();

  const month = parseMonth(el.monthInput.value);
  if (!month) {
    setFieldError(el.monthInput, t("dateInvalid"));
    el.monthInput.focus();
    return null;
  }

  const description = el.descriptionInput.value.trim();
  if (!description) {
    setFieldError(
      el.descriptionInput,
      resolvedLangCode() === "en" ? "Please enter a description." : "Bitte eine Beschreibung eingeben."
    );
    el.descriptionInput.focus();
    return null;
  }

  const amount = parseFloat(String(el.amountInput.value).replace(",", "."));
  if (Number.isNaN(amount) || amount < 0) {
    setFieldError(
      el.amountInput,
      resolvedLangCode() === "en" ? "Please enter a valid amount." : "Bitte einen gültigen Betrag eingeben."
    );
    el.amountInput.focus();
    return null;
  }

  return {
    month,
    description,
    category: normalizeCategory(el.categoryInput.value, state.customCategories),
    txType: ["Ausgabe", "Einnahme"].includes(el.typeInput.value) ? el.typeInput.value : "Ausgabe",
    amount,
    account: el.accountInput.value || "Girokonto",
    note: el.noteInput.value.trim(),
    taxDeclaration: Boolean(el.taxDeclarationInput.checked)
  };
}

function clearForm(keepMonth = true) {
  categoryManuallyOverridden = false;
  lastAutoCategory = null;
  el.descriptionInput.value = "";
  el.categoryInput.value = "Sonstiges";
  el.amountInput.value = "";
  el.typeInput.value = "Ausgabe";
  el.accountInput.value = "Girokonto";
  el.noteInput.value = "";
  el.taxDeclarationInput.checked = false;
  if (!keepMonth) setDefaultMonth();
  clearBookingFormErrors();
}

function loadEntryIntoForm(entry) {
  el.monthInput.value = formatCanonicalDate(entry.month);
  el.descriptionInput.value = entry.description;
  el.categoryInput.value = normalizeCategory(entry.category, state.customCategories);
  el.typeInput.value = entry.txType;
  el.amountInput.value = String(entry.amount).replace(".", ",");
  el.accountInput.value = entry.account || "Girokonto";
  el.noteInput.value = entry.note || "";
  el.taxDeclarationInput.checked = Boolean(entry.taxDeclaration);
  lastAutoCategory = normalizeCategory(entry.category, state.customCategories);
  categoryManuallyOverridden = true;
}

function userBookings() {
  const userId = activeUser().id;

  if (perfCache.version === stateDataVersion && perfCache.userId === userId) {
    return perfCache.userBookings;
  }

  const list = state.bookings.filter(b => b.userId === userId);
  perfCache.version = stateDataVersion;
  perfCache.userId = userId;
  perfCache.userBookings = list;
  perfCache.reportRowsByYear.clear();
  perfCache.filteredSorted = { key: "", value: [] };
  return list;
}

function availableFilterYears() {
  const years = Array.from(new Set(userBookings().map(b => getDateParts(b.month)?.yyyy).filter(Boolean).map(String)));
  years.sort((a, b) => b.localeCompare(a));

  const currentYear = String(new Date().getFullYear());
  if (!years.includes(currentYear)) years.unshift(currentYear);
  return years;
}

function refreshYearFilterOptions() {
  const previous = el.fYear.value || "Alle";
  const years = availableFilterYears();
  fillSelect(el.fYear, [t("yearAll"), ...years], true);

  if (previous === "Alle") {
    el.fYear.value = "Alle";
  } else {
    el.fYear.value = years.includes(previous) ? previous : "Alle";
  }
}

function filteredBookings() {
  const source = userBookings();
  const search = el.fSearch.value.trim().toLowerCase();

  return source.filter(b => {
    const parts = getDateParts(b.month);
    if (!parts) return false;
    if (el.fMonth.value !== "Alle" && String(parts.mm).padStart(2, "0") !== el.fMonth.value) return false;
    if (el.fYear.value !== "Alle" && String(parts.yyyy) !== el.fYear.value) return false;
    if (el.fType.value !== "Alle" && b.txType !== el.fType.value) return false;
    if (el.fCategory.value !== "Alle" && b.category !== el.fCategory.value) return false;
    if (el.fAccount.value !== "Alle" && b.account !== el.fAccount.value) return false;
    if (search && !(b.description.toLowerCase().includes(search) || (b.note || "").toLowerCase().includes(search))) return false;
    return true;
  });
}

function bookingFilterKey() {
  return [
    stateDataVersion,
    activeUser().id,
    desktopSettings().sortDirection,
    el.fMonth.value,
    el.fYear.value,
    el.fType.value,
    el.fCategory.value,
    el.fAccount.value,
    el.fSearch.value.trim().toLowerCase()
  ].join("|");
}

function filteredAndSortedBookings() {
  const key = bookingFilterKey();

  if (perfCache.filteredSorted.key === key) {
    return perfCache.filteredSorted.value;
  }

  const sortDirection = desktopSettings().sortDirection;
  const factor = sortDirection === "asc" ? 1 : -1;
  const sorted = filteredBookings().slice().sort((a, b) => {
    const byDate = (monthSortKey(a.month) - monthSortKey(b.month)) * factor;
    if (byDate !== 0) return byDate;
    return ((Number(a.createdAt) || 0) - (Number(b.createdAt) || 0)) * factor;
  });

  perfCache.filteredSorted = { key, value: sorted };
  return sorted;
}
function emptyStateHtml(title, message) {
  return `<div class="empty-state"><strong>${escapeHtml(title)}</strong><p>${escapeHtml(message)}</p></div>`;
}

function updateMonthlyChartEmptyState(entries, year) {
  const holder = el.monthlyChartEmpty;
  if (!holder) return;

  const hasAnyBookings = entries.length > 0;
  const hasYearData = monthlyCashflowRows(entries, year).some(row => row.income > 0 || row.expense > 0);

  if (hasYearData) {
    holder.classList.add("hidden");
    holder.innerHTML = "";
    return;
  }

  const title = hasAnyBookings
    ? (resolvedLangCode() === "en" ? `No bookings for ${year} yet` : `Für ${year} sind noch keine Buchungen vorhanden`)
    : (resolvedLangCode() === "en" ? "No bookings yet" : "Noch keine Buchungen vorhanden");
  const message = hasAnyBookings
    ? (resolvedLangCode() === "en" ? "Choose another year or create a new booking." : "Wähle ein anderes Jahr oder erfasse eine neue Buchung.")
    : (resolvedLangCode() === "en" ? "Create your first booking above to see monthly flow." : "Lege oben deine erste Buchung an, um den Monatsverlauf zu sehen.");

  holder.innerHTML = emptyStateHtml(title, message);
  holder.classList.remove("hidden");
}

function render() {
  refreshCategoryOptions(true);
  renderUsers();
  renderDashboard();
  renderBookings();
  renderReport();
  renderSettings();
}

function renderUsers() {
  ensureActiveUser();
  el.userSelect.innerHTML = "";
  state.users.forEach(u => {
    const op = document.createElement("option");
    op.value = u.id;
    op.textContent = u.name;
    el.userSelect.appendChild(op);
  });
  el.userSelect.value = activeUser().id;
}

function euro(v) {
  const value = Number(v || 0);
  const currency = desktopSettings().currency || "EUR";
  const locale = resolvedLangCode() === "en" ? "en-US" : "de-DE";
  try {
    return new Intl.NumberFormat(locale, {
      style: "currency",
      currency,
      minimumFractionDigits: 2,
      maximumFractionDigits: 2
    }).format(value);
  } catch (_) {
    return new Intl.NumberFormat(locale, {
      style: "currency",
      currency: "EUR",
      minimumFractionDigits: 2,
      maximumFractionDigits: 2
    }).format(value);
  }
}

function renderSettings() {
  const settings = desktopSettings();
  if (el.settingsLanguage) el.settingsLanguage.value = settings.language;
  if (el.settingsDateFormat) el.settingsDateFormat.value = settings.dateFormat;
  if (el.settingsCurrency) el.settingsCurrency.value = settings.currency;
  if (el.settingsSortDirection) el.settingsSortDirection.value = settings.sortDirection;
  if (el.settingsStartTab) el.settingsStartTab.value = settings.startTab;
  if (el.settingsDefaultExportFormat) el.settingsDefaultExportFormat.value = settings.defaultExportFormat;
  if (el.settingsKeepDateAfterSave) el.settingsKeepDateAfterSave.value = settings.keepDateAfterSave ? "active" : "inactive";
  if (el.settingsCategorySuggestions) el.settingsCategorySuggestions.value = settings.categorySuggestions ? "active" : "inactive";
  if (el.settingsFontSize) el.settingsFontSize.value = settings.fontSize;
  if (el.settingsNavigationAnimationStyle) el.settingsNavigationAnimationStyle.value = settings.navigationAnimationStyle;
  if (el.settingsAppLock) el.settingsAppLock.checked = Boolean(settings.appLockEnabled);
  if (el.reportExportFormat) el.reportExportFormat.value = settings.defaultExportFormat;
}

function availableDashboardYears(entries) {
  const years = Array.from(new Set(entries.map(e => getDateParts(e.month)?.yyyy).filter(Boolean)));
  const currentYear = new Date().getFullYear();
  if (!years.includes(currentYear)) years.push(currentYear);
  years.sort((a, b) => b - a);
  return years;
}

function syncDashboardYearSelect(entries) {
  const select = el.dashboardYearSelect;
  const currentYear = new Date().getFullYear();
  const years = availableDashboardYears(entries);

  if (!select) return currentYear;

  const previous = Number(select.value) || currentYear;
  select.innerHTML = years.map(y => `<option value="${y}">${y}</option>`).join("");

  const selected = years.includes(previous) ? previous : years[0] || currentYear;
  select.value = String(selected);
  return selected;
}

function syncDashboardTopMonthSelect() {
  const select = el.dashboardTopMonthSelect;
  const currentMonth = new Date().getMonth() + 1;
  if (!select) return currentMonth;

  const previous = Number(select.value) || currentMonth;
  const names = monthNamesForUi();
  select.innerHTML = names.map((name, i) => `<option value="${i + 1}">${name}</option>`).join("");

  const selected = previous >= 1 && previous <= 12 ? previous : currentMonth;
  select.value = String(selected);
  return selected;
}

function renderDashboard() {
  const now = new Date();
  const currentYear = now.getFullYear();
  const currentMonth = now.getMonth() + 1;
  const entries = userBookings();

  let totalIncome = 0;
  let totalExpense = 0;
  let monthIncome = 0;
  let monthExpense = 0;

  entries.forEach(entry => {
    const amount = Number(entry.amount) || 0;
    const parts = getDateParts(entry.month);

    if (entry.txType === "Einnahme") {
      totalIncome += amount;
      if (parts && parts.yyyy === currentYear && parts.mm === currentMonth) {
        monthIncome += amount;
      }
      return;
    }

    totalExpense += amount;
    if (parts && parts.yyyy === currentYear && parts.mm === currentMonth) {
      monthExpense += amount;
    }
  });

  const stats = [
    [resolvedLangCode() === "en" ? "Current Balance" : "Aktueller Saldo", euro(totalIncome - totalExpense)],
    [resolvedLangCode() === "en" ? "Income (Month)" : "Einnahmen (Monat)", euro(monthIncome)],
    [resolvedLangCode() === "en" ? "Expense (Month)" : "Ausgaben (Monat)", euro(monthExpense)],
    [resolvedLangCode() === "en" ? "Monthly Surplus" : "Monatsüberschuss", euro(monthIncome - monthExpense)]
  ];

  el.statsCards.innerHTML = stats.map(([k, v]) => `<article class="card"><p>${k}</p><h4>${v}</h4></article>`).join("");

  const selectedYear = syncDashboardYearSelect(entries);
  const selectedTopMonth = syncDashboardTopMonthSelect();

  const expenseByCategory = new Map();
  entries.forEach(entry => {
    if (entry.txType !== "Ausgabe") return;
    const parts = getDateParts(entry.month);
    if (!parts || parts.yyyy !== selectedYear || parts.mm !== selectedTopMonth) return;
    const key = entry.category;
    expenseByCategory.set(key, (expenseByCategory.get(key) || 0) + (Number(entry.amount) || 0));
  });

  const top = Array.from(expenseByCategory.entries()).sort((a, b) => b[1] - a[1]).slice(0, 10);
  const emptyMonthLabel = `${monthNamesForUi()[selectedTopMonth - 1]} ${selectedYear}`;
  el.topCategories.innerHTML = top.length
    ? top.map(([k, v]) => `<li>${categoryLabelForUi(k)}: ${euro(v)}</li>`).join("")
    : `<li>${resolvedLangCode() === "en"
      ? `No expenses in ${emptyMonthLabel}. Add an expense to show categories.`
      : `Keine Ausgaben in ${emptyMonthLabel}. Erfasse eine Ausgabe, um Kategorien zu sehen.`}</li>`;

  updateMonthlyChartEmptyState(entries, selectedYear);
  renderMonthlyCashflowChart(entries, selectedYear);
}

function monthlyCashflowRows(entries, year) {
  const rows = Array.from({ length: 12 }, (_, i) => ({ month: i + 1, income: 0, expense: 0 }));

  entries.forEach(entry => {
    const parts = getDateParts(entry.month);
    if (!parts || parts.yyyy !== year) return;

    const idx = parts.mm - 1;
    const amount = Number(entry.amount) || 0;
    if (entry.txType === "Einnahme") rows[idx].income += amount;
    else rows[idx].expense += amount;
  });

  return rows;
}

function setupMonthlyChartInteractions() {
  const canvas = el.monthlyChart;
  if (!canvas || canvas.dataset.bound === "1") return;

  canvas.dataset.bound = "1";

  canvas.addEventListener("mousemove", evt => {
    if (monthlyChartState.pinnedIndex !== null) return;

    const idx = monthlyHitIndex(evt);
    if (idx === monthlyChartState.hoverIndex) return;

    monthlyChartState.hoverIndex = idx;
    if (idx >= 0) {
      updateMonthlyChartTooltip(idx, evt);
    } else {
      hideMonthlyChartTooltip();
    }
    renderMonthlyCashflowChart(userBookings(), monthlyChartState.year || new Date().getFullYear());
  });

  canvas.addEventListener("mouseleave", () => {
    if (monthlyChartState.pinnedIndex !== null) return;
    monthlyChartState.hoverIndex = -1;
    hideMonthlyChartTooltip();
    renderMonthlyCashflowChart(userBookings(), monthlyChartState.year || new Date().getFullYear());
  });

  canvas.addEventListener("click", evt => {
    const idx = monthlyHitIndex(evt);

    if (idx < 0) {
      monthlyChartState.pinnedIndex = null;
      monthlyChartState.hoverIndex = -1;
      hideMonthlyChartTooltip();
      renderMonthlyCashflowChart(userBookings(), monthlyChartState.year || new Date().getFullYear());
      return;
    }

    if (monthlyChartState.pinnedIndex === idx) {
      monthlyChartState.pinnedIndex = null;
      monthlyChartState.hoverIndex = idx;
      updateMonthlyChartTooltip(idx, evt);
    } else {
      monthlyChartState.pinnedIndex = idx;
      monthlyChartState.hoverIndex = idx;
      updateMonthlyChartTooltip(idx, evt);
    }

    renderMonthlyCashflowChart(userBookings(), monthlyChartState.year || new Date().getFullYear());
  });
}

function monthlyHitIndex(evt) {
  const canvas = el.monthlyChart;
  const rect = canvas.getBoundingClientRect();
  const x = evt.clientX - rect.left;
  const y = evt.clientY - rect.top;

  const hit = monthlyChartState.bars.find(b => x >= b.slotX && x <= b.slotX + b.slotW && y >= b.hitTop && y <= b.hitBottom);
  return hit ? hit.index : -1;
}

function monthLabel(month, year) {
  return `${monthNamesForUi()[month - 1]} ${year}`;
}

function updateMonthlyChartTooltip(index, evt = null) {
  const tip = el.monthlyChartTooltip;
  const canvas = el.monthlyChart;
  if (!tip || !canvas) return;

  const row = monthlyChartState.rows[index];
  const bar = monthlyChartState.bars[index];
  if (!row || !bar) {
    hideMonthlyChartTooltip();
    return;
  }

  const net = row.income - row.expense;
  tip.innerHTML = [
    `<strong>${monthLabel(row.month, monthlyChartState.year)}</strong>`,
    `<p>${t("income")}: <span class="i">${euro(row.income)}</span></p>`,
    `<p>${t("expense")}: <span class="e">${euro(row.expense)}</span></p>`,
    `<p>${t("balance")}: <span class="n">${euro(net)}</span></p>`
  ].join("");

  tip.classList.remove("hidden");
  tip.setAttribute("aria-hidden", "false");

  const wrap = canvas.parentElement;
  const wrapRect = wrap.getBoundingClientRect();
  const canvasRect = canvas.getBoundingClientRect();

  const desiredX = evt ? evt.clientX - wrapRect.left : (canvasRect.left - wrapRect.left + bar.x + bar.w / 2);
  const anchorY = canvasRect.top - wrapRect.top + bar.y;

  const tipW = tip.offsetWidth || 180;
  const tipH = tip.offsetHeight || 92;

  let left = desiredX - tipW / 2;
  left = Math.max(8, Math.min(left, wrapRect.width - tipW - 8));

  let top = anchorY - tipH - 10;
  if (top < 8) top = Math.min(anchorY + 12, wrapRect.height - tipH - 8);

  tip.style.left = `${left}px`;
  tip.style.top = `${top}px`;
}

function hideMonthlyChartTooltip() {
  const tip = el.monthlyChartTooltip;
  if (!tip) return;
  tip.classList.add("hidden");
  tip.setAttribute("aria-hidden", "true");
}

function renderMonthlyCashflowChart(entries, year) {
  const canvas = el.monthlyChart;
  if (!canvas) return;

  const ctx = canvas.getContext("2d");
  if (!ctx) return;

  const dpr = window.devicePixelRatio || 1;
  const width = Math.max(320, Math.floor(canvas.clientWidth || 760));
  const height = Math.max(220, Math.floor(canvas.clientHeight || 280));
  canvas.width = Math.floor(width * dpr);
  canvas.height = Math.floor(height * dpr);
  ctx.setTransform(dpr, 0, 0, dpr, 0, 0);

  const rows = monthlyCashflowRows(entries, year);
  const maxScale = Math.max(1, ...rows.map(r => Math.max(r.income, r.expense)));

  const padLeft = 56;
  const padRight = 12;
  const padTop = 20;
  const padBottom = 30;

  const chartW = width - padLeft - padRight;
  const chartH = height - padTop - padBottom;
  const baseY = height - padBottom;
  const maxBarH = chartH - 8;

  ctx.clearRect(0, 0, width, height);
  ctx.fillStyle = "#ffffff";
  ctx.fillRect(0, 0, width, height);

  const formatAxisValue = value => {
    if (value >= 1000000) return (value / 1000000).toFixed(1).replace(".", ",") + "M";
    if (value >= 1000) return (value / 1000).toFixed(1).replace(".", ",") + "k";
    return Math.round(value).toString();
  };

  ctx.strokeStyle = "#dbe4ee";
  ctx.lineWidth = 1;
  ctx.beginPath();
  ctx.moveTo(padLeft, padTop);
  ctx.lineTo(padLeft, baseY);
  ctx.lineTo(width - padRight, baseY);
  ctx.stroke();

  const tickValues = [1, 0.75, 0.5, 0.25, 0];
  tickValues.forEach(tick => {
    const y = baseY - maxBarH * tick;

    ctx.strokeStyle = tick === 0 ? "#dbe4ee" : "#eef2f7";
    ctx.beginPath();
    ctx.moveTo(padLeft, y);
    ctx.lineTo(width - padRight, y);
    ctx.stroke();

    const rawValue = maxScale * tick;
    const label = tick === 0 ? "0" : formatAxisValue(rawValue);
    ctx.fillStyle = "#64748b";
    ctx.font = "11px Segoe UI";
    ctx.textAlign = "right";
    ctx.textBaseline = "middle";
    ctx.fillText(label, padLeft - 8, y);
  });

  ctx.textBaseline = "alphabetic";

  const slot = chartW / 12;
  const barW = slot * 0.56;

  monthlyChartState.rows = rows;
  monthlyChartState.year = year;
  monthlyChartState.bars = [];

  if (monthlyChartState.pinnedIndex !== null && !rows[monthlyChartState.pinnedIndex]) {
    monthlyChartState.pinnedIndex = null;
  }

  rows.forEach((row, i) => {
    const x = padLeft + i * slot + (slot - barW) / 2;
    const income = row.income;
    const expense = row.expense;

    const incomeH = income > 0 ? Math.max(2, (income / maxScale) * maxBarH) : 0;
    const yIncome = baseY - incomeH;

    const expenseHRaw = expense > 0 ? Math.max(2, (expense / maxScale) * maxBarH) : 0;
    const expenseOverlayH = incomeH > 0 ? Math.min(expenseHRaw, incomeH) : expenseHRaw;

    if (incomeH > 0) {
      ctx.fillStyle = "#22c55e";
      ctx.fillRect(x, yIncome, barW, incomeH);
    }

    if (expenseOverlayH > 0) {
      const yExpense = baseY - expenseOverlayH;
      ctx.fillStyle = "#ef4444";
      ctx.fillRect(x, yExpense, barW, expenseOverlayH);
    }

    const visibleBarH = Math.max(incomeH, expenseOverlayH);
    const barTop = visibleBarH > 0 ? baseY - visibleBarH : baseY - 2;

    const activeIndex = monthlyChartState.pinnedIndex !== null ? monthlyChartState.pinnedIndex : monthlyChartState.hoverIndex;
    if (activeIndex === i) {
      ctx.strokeStyle = "#1e293b";
      ctx.lineWidth = 1.5;
      ctx.strokeRect(x - 1.5, barTop - 1.5, barW + 3, Math.max(4, visibleBarH + 3));
    }

    ctx.fillStyle = "#64748b";
    ctx.font = "11px Segoe UI";
    ctx.textAlign = "center";
    ctx.fillText(String(i + 1), x + barW / 2, height - 10);

    monthlyChartState.bars.push({
      index: i,
      slotX: padLeft + i * slot,
      slotW: slot,
      x,
      y: barTop,
      w: barW,
      h: Math.max(2, visibleBarH),
      hitTop: padTop,
      hitBottom: baseY
    });
  });

  ctx.fillStyle = "#64748b";
  ctx.font = "600 11px Segoe UI";
  ctx.textAlign = "right";
  ctx.fillText(String(year), width - padRight, 12);

  const activeIndex = monthlyChartState.pinnedIndex !== null ? monthlyChartState.pinnedIndex : monthlyChartState.hoverIndex;
  if (activeIndex >= 0) {
    updateMonthlyChartTooltip(activeIndex);
  } else {
    hideMonthlyChartTooltip();
  }
}

function renderBookings() {

  refreshYearFilterOptions();
  const allEntries = userBookings();
  const entries = filteredAndSortedBookings();
  const filterKey = bookingFilterKey();

  if (bookingRenderState.key !== filterKey) {
    bookingRenderState.key = filterKey;
    bookingRenderState.visibleCount = BOOKING_RENDER_INITIAL;
  }

  const visibleCount = Math.min(entries.length, Math.max(BOOKING_RENDER_INITIAL, bookingRenderState.visibleCount));
  const visibleEntries = entries.slice(0, visibleCount);

  const visibleIds = new Set(entries.map(e => e.id));
  Array.from(selectedBookingIds).forEach(id => {
    if (!visibleIds.has(id)) selectedBookingIds.delete(id);
  });

  const hasAnyBookings = allEntries.length > 0;

  if (!entries.length) {
    const title = hasAnyBookings
      ? (resolvedLangCode() === "en" ? "No bookings for current filters" : "Keine Buchungen für den aktuellen Filter")
      : (resolvedLangCode() === "en" ? "No bookings yet" : "Noch keine Buchungen vorhanden");
    const message = hasAnyBookings
      ? (resolvedLangCode() === "en" ? "Adjust filters or use Reset to show entries again." : "Passe die Filter an oder nutze Reset, um wieder Einträge anzuzeigen.")
      : (resolvedLangCode() === "en" ? "Create your first booking above with date, description, and amount." : "Erfasse oben deine erste Buchung mit Datum, Beschreibung und Betrag.");
    el.bookingsBody.innerHTML = `<tr class="empty-row"><td colspan="9">${emptyStateHtml(title, message)}</td></tr>`;
  } else {
    const rowsHtml = visibleEntries.map(e => {
      const rowClasses = [];
      if (Boolean(e.taxDeclaration)) rowClasses.push("tax-row");
      if (e.id === selectedBookingId) rowClasses.push("selected-row");
      const rowClass = rowClasses.join(" ");
      const checked = selectedBookingIds.has(e.id) ? "checked" : "";
      return `
    <tr class="${rowClass}" data-id="${e.id}">
      <td class="check-col"><input type="checkbox" class="booking-check" data-id="${e.id}" ${checked} /></td>
      <td>${formatCanonicalDate(e.month)}</td>
      <td>${escapeHtml(e.description)}</td>
      <td>${categoryLabelForUi(e.category)}</td>
      <td>${txTypeLabelForUi(e.txType)}</td>
      <td>${euro(e.amount)}</td>
      <td>${accountLabelForUi(e.account)}</td>
      <td>${escapeHtml(e.note || "")}</td>
      <td class="check-col"><input type="checkbox" class="tax-check" data-id="${e.id}" ${e.taxDeclaration ? "checked" : ""} aria-label="${resolvedLangCode() === "en" ? "Mark for tax declaration" : "Für Steuererklärung markieren"}" /></td>
    </tr>
  `;
    }).join("");

    const hasMore = entries.length > visibleEntries.length;
    const loadMoreHtml = hasMore
      ? `<tr class="load-more-row"><td colspan="9"><button type="button" class="btn secondary" id="loadMoreBookingsBtn">${resolvedLangCode() === "en" ? "Load more" : "Mehr laden"} (${visibleEntries.length}/${entries.length})</button></td></tr>`
      : "";

    el.bookingsBody.innerHTML = rowsHtml + loadMoreHtml;
  }

  if (el.selectedBookingsInfo) {
    const text = entries.length > visibleEntries.length
      ? tf("selectedCountShown", { count: selectedBookingIds.size, shown: visibleEntries.length, total: entries.length })
      : tf("selectedCount", { count: selectedBookingIds.size });
    el.selectedBookingsInfo.textContent = text;
  }

  if (el.selectAllBookings) {
    const allChecked = entries.length > 0 && entries.every(e => selectedBookingIds.has(e.id));
    el.selectAllBookings.checked = allChecked;
    el.selectAllBookings.indeterminate = selectedBookingIds.size > 0 && !allChecked;
  }

  el.bookingsBody.querySelectorAll("input.booking-check").forEach(box => {
    box.addEventListener("click", evt => evt.stopPropagation());
    box.addEventListener("change", () => {
      const id = box.dataset.id;
      if (!id) return;
      if (box.checked) selectedBookingIds.add(id);
      else selectedBookingIds.delete(id);
      renderBookings();
    });
  });

  el.bookingsBody.querySelectorAll("input.tax-check").forEach(box => {
    box.addEventListener("click", evt => evt.stopPropagation());
    box.addEventListener("change", () => {
      const id = box.dataset.id;
      if (!id) return;
      const idx = state.bookings.findIndex(b => b.id === id && b.userId === activeUser().id);
      if (idx < 0) return;
      state.bookings[idx] = { ...state.bookings[idx], taxDeclaration: Boolean(box.checked) };
      if (selectedBookingId === id) {
        el.taxDeclarationInput.checked = Boolean(box.checked);
      }
      saveState();
      renderBookings();
    });
  });

  const loadMoreBtn = document.getElementById("loadMoreBookingsBtn");
  if (loadMoreBtn) {
    loadMoreBtn.addEventListener("click", () => {
      bookingRenderState.visibleCount = Math.min(entries.length, bookingRenderState.visibleCount + BOOKING_RENDER_STEP);
      renderBookings();
    });
  }

  el.bookingsBody.querySelectorAll("tr").forEach(row => {
    row.addEventListener("click", evt => {
      if (!row.dataset.id) return;
      if (evt.target instanceof HTMLInputElement) return;
      selectedBookingId = row.dataset.id;
      renderBookings();
    });

    row.addEventListener("dblclick", evt => {
      if (!row.dataset.id) return;
      if (evt.target instanceof HTMLInputElement) return;
      const id = row.dataset.id;
      const entry = state.bookings.find(b => b.id === id && b.userId === activeUser().id);
      if (!entry) return;
      selectedBookingId = id;
      loadEntryIntoForm(entry);
      renderBookings();
    });
  });
}

function reportRows(year) {
  const cacheKey = String(year);
  if (perfCache.reportRowsByYear.has(cacheKey)) {
    return perfCache.reportRowsByYear.get(cacheKey);
  }

  const rows = Array.from({ length: 12 }, (_, i) => ({ month: i + 1, income: 0, expense: 0, net: 0 }));

  userBookings().forEach(entry => {
    const parts = getDateParts(entry.month);
    if (!parts || parts.yyyy !== year) return;

    const idx = parts.mm - 1;
    const amount = Number(entry.amount) || 0;
    if (entry.txType === "Einnahme") rows[idx].income += amount;
    else rows[idx].expense += amount;
  });

  rows.forEach(row => {
    row.net = row.income - row.expense;
  });

  perfCache.reportRowsByYear.set(cacheKey, rows);
  return rows;
}

function compareRowHtml(label, prevValue, currentValue, isCurrency = true) {
  const delta = currentValue - prevValue;
  const pct = prevValue === 0 ? null : (delta / prevValue) * 100;
  const deltaClass = delta > 0 ? "cmp-up" : delta < 0 ? "cmp-down" : "cmp-flat";
  const deltaSign = delta > 0 ? "+" : "";

  const prevText = isCurrency ? euro(prevValue) : String(prevValue);
  const currentText = isCurrency ? euro(currentValue) : String(currentValue);
  const deltaText = isCurrency ? `${deltaSign}${euro(delta)}` : `${deltaSign}${delta}`;
  const pctText = pct === null ? "-" : `${deltaSign}${pct.toFixed(1).replace(".", ",")}%`;

  return `
    <tr>
      <td>${escapeHtml(label)}</td>
      <td>${escapeHtml(prevText)}</td>
      <td>${escapeHtml(currentText)}</td>
      <td class="${deltaClass}">${escapeHtml(deltaText)} <span class="cmp-pct">(${escapeHtml(pctText)})</span></td>
    </tr>
  `;
}

function renderYearComparison(year, currentRows) {
  const prevYear = year - 1;
  const prevRows = reportRows(prevYear);

  const currentIncome = currentRows.reduce((sum, row) => sum + row.income, 0);
  const currentExpense = currentRows.reduce((sum, row) => sum + row.expense, 0);
  const currentNet = currentIncome - currentExpense;

  const prevIncome = prevRows.reduce((sum, row) => sum + row.income, 0);
  const prevExpense = prevRows.reduce((sum, row) => sum + row.expense, 0);
  const prevNet = prevIncome - prevExpense;

  let currentCount = 0;
  let prevCount = 0;
  userBookings().forEach(entry => {
    const y = getDateParts(entry.month)?.yyyy;
    if (y === year) currentCount += 1;
    else if (y === prevYear) prevCount += 1;
  });

  if (el.reportPrevYearHead) el.reportPrevYearHead.textContent = String(prevYear);
  if (el.reportYearHead) el.reportYearHead.textContent = String(year);

  const hasData =
    currentIncome > 0 || currentExpense > 0 || currentCount > 0 ||
    prevIncome > 0 || prevExpense > 0 || prevCount > 0;

  if (!hasData) {
    el.reportCompareBody.innerHTML = `<tr class="empty-row"><td colspan="4">${emptyStateHtml(
      resolvedLangCode() === "en"
        ? `No bookings for ${year} and ${prevYear} yet`
        : `Für ${year} und ${prevYear} liegen noch keine Buchungen vor`,
      resolvedLangCode() === "en"
        ? "Once bookings are created, the year comparison appears here."
        : "Sobald Buchungen erfasst wurden, erscheint hier der Jahresvergleich."
    )}</td></tr>`;
    return;
  }

  el.reportCompareBody.innerHTML = [
    compareRowHtml(t("income"), prevIncome, currentIncome, true),
    compareRowHtml(t("expense"), prevExpense, currentExpense, true),
    compareRowHtml(t("balance"), prevNet, currentNet, true),
    compareRowHtml(t("bookings"), prevCount, currentCount, false)
  ].join("");
}

function renderReport() {
  const year = parseYear(el.reportYearInput.value);
  if (!year) {
    el.reportStatsCards.innerHTML = "";
    el.reportBody.innerHTML = `<tr><td colspan="4">${resolvedLangCode() === "en" ? "Please enter a valid year like 2026." : "Bitte ein gültiges Jahr wie 2026 eingeben."}</td></tr>`;
    if (el.reportCompareBody) {
      el.reportCompareBody.innerHTML = `<tr><td colspan="4">${resolvedLangCode() === "en" ? "Please enter a valid year like 2026." : "Bitte ein gültiges Jahr wie 2026 eingeben."}</td></tr>`;
    }
    return;
  }

  const rows = reportRows(year);
  const totalIncome = rows.reduce((sum, r) => sum + r.income, 0);
  const totalExpense = rows.reduce((sum, r) => sum + r.expense, 0);
  const net = totalIncome - totalExpense;

  el.reportStatsCards.innerHTML = [
    [resolvedLangCode() === "en" ? "Year Income" : "Jahr Einnahmen", euro(totalIncome)],
    [resolvedLangCode() === "en" ? "Year Expense" : "Jahr Ausgaben", euro(totalExpense)],
    [resolvedLangCode() === "en" ? "Year Balance" : "Jahr Saldo", euro(net)]
  ].map(([k, v]) => `<article class="card"><p>${k}</p><h4>${v}</h4></article>`).join("");

  renderYearComparison(year, rows);

  const hasReportData = rows.some(r => r.income > 0 || r.expense > 0);
  if (!hasReportData) {
    el.reportBody.innerHTML = `<tr class="empty-row"><td colspan="4">${emptyStateHtml(
      resolvedLangCode() === "en" ? `No bookings for ${year} yet` : `Für ${year} liegen noch keine Buchungen vor`,
      resolvedLangCode() === "en"
        ? "Once bookings for this year are created, the monthly overview appears here."
        : "Sobald Buchungen mit diesem Jahr erfasst sind, erscheint hier die Monatsübersicht."
    )}</td></tr>`;
    return;
  }

  el.reportBody.innerHTML = rows.map(r => `
    <tr>
      <td>${monthNamesForUi()[r.month - 1]}</td>
      <td>${euro(r.income)}</td>
      <td>${euro(r.expense)}</td>
      <td>${euro(r.net)}</td>
    </tr>
  `).join("");
}

async function tryInvokeTauriCommand(cmd, payload) {
  const w = window;

  if (w.__TAURI__?.core?.invoke) {
    return w.__TAURI__.core.invoke(cmd, payload);
  }

  if (w.__TAURI_INTERNALS__?.invoke) {
    return w.__TAURI_INTERNALS__.invoke(cmd, payload);
  }

  return null;
}

function getReportExportScope() {
  const scope = String(el.reportExportScope?.value || "summary").toLowerCase();
  if (
    scope === "year-bookings" ||
    scope === "month-bookings" ||
    scope === "year-comparison" ||
    scope === "tax-year-bookings"
  ) return scope;
  return "summary";
}

function getReportExportMonth() {
  const raw = String(el.reportExportMonth?.value || "").trim();
  if (!/^\d{2}$/.test(raw)) return null;
  const month = Number(raw);
  if (month < 1 || month > 12) return null;
  return month;
}

function reportExportMonthLabel(month) {
  if (!month || month < 1 || month > 12) return "";
  return monthNamesForUi()[month - 1];
}

function updateReportExportControls() {
  if (!el.reportExportScope || !el.reportExportMonth) return;
  const scope = getReportExportScope();
  const needsMonth = scope === "month-bookings";
  el.reportExportMonth.disabled = !needsMonth;
}

function collectExportBookings(year, month = null, onlyTax = false) {
  return userBookings()
    .filter(entry => {
      const parts = getDateParts(entry.month);
      if (!parts) return false;
      if (parts.yyyy !== year) return false;
      if (month !== null && parts.mm !== month) return false;
      if (onlyTax && !Boolean(entry.taxDeclaration)) return false;
      return true;
    })
    .sort((a, b) => {
      const byDate = monthSortKey(b.month) - monthSortKey(a.month);
      if (byDate !== 0) return byDate;
      return (Number(b.createdAt || 0) - Number(a.createdAt || 0));
    });
}

function buildSummaryExportModel(year) {
  const rows = reportRows(year);
  const totalIncome = rows.reduce((sum, row) => sum + row.income, 0);
  const totalExpense = rows.reduce((sum, row) => sum + row.expense, 0);
  const totalNet = totalIncome - totalExpense;

  return {
    kind: "summary",
    year,
    createdAt: new Date().toLocaleString("de-DE"),
    currency: desktopSettings().currency || "EUR",
    rows,
    totals: { income: totalIncome, expense: totalExpense, net: totalNet }
  };
}

function buildBookingsExportModel(year, month = null, options = {}) {
  const onlyTax = Boolean(options.onlyTax);
  const rows = collectExportBookings(year, month, onlyTax).map(entry => ({
    date: formatCanonicalDate(entry.month),
    description: entry.description,
    category: normalizeCategory(entry.category, state.customCategories),
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
    currency: desktopSettings().currency || "EUR",
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
  const currentRows = reportRows(year);
  const prevRows = reportRows(prevYear);

  const currentIncome = currentRows.reduce((sum, row) => sum + row.income, 0);
  const currentExpense = currentRows.reduce((sum, row) => sum + row.expense, 0);
  const currentNet = currentIncome - currentExpense;

  const prevIncome = prevRows.reduce((sum, row) => sum + row.income, 0);
  const prevExpense = prevRows.reduce((sum, row) => sum + row.expense, 0);
  const prevNet = prevIncome - prevExpense;

  let currentCount = 0;
  let prevCount = 0;
  userBookings().forEach(entry => {
    const y = getDateParts(entry.month)?.yyyy;
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
    currency: desktopSettings().currency || "EUR",
    rows: [
      toRow("Einnahmen", prevIncome, currentIncome, true),
      toRow("Ausgaben", prevExpense, currentExpense, true),
      toRow("Saldo", prevNet, currentNet, true),
      toRow("Buchungen", prevCount, currentCount, false)
    ]
  };
}

function buildReportExportModel(year, scope, month) {
  if (scope === "year-bookings") {
    return buildBookingsExportModel(year, null);
  }
  if (scope === "month-bookings") {
    return buildBookingsExportModel(year, month);
  }
  if (scope === "tax-year-bookings") {
    return buildBookingsExportModel(year, null, { onlyTax: true });
  }
  if (scope === "year-comparison") {
    return buildComparisonExportModel(year);
  }
  return buildSummaryExportModel(year);
}
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

function buildCsvSummaryContent(model) {
  const lines = [
    csvLine(["Export", "Finanz Tracker Jahresauswertung"]),
    csvLine(["Jahr", String(model.year)]),
    csvLine(["Erstellt am", model.createdAt]),
    csvLine(["Währung", model.currency]),
    "",
    csvLine(["Monat", "Einnahmen", "Ausgaben", "Saldo"]),
    ...model.rows.map(row => csvLine([
      monthNamesForUi()[row.month - 1],
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
    : (model.month
      ? reportExportMonthLabel(model.month) + " " + model.year
      : String(model.year));
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
      return csvLine([
        row.label,
        prevValue,
        currentValue,
        deltaValue,
        reportExportPercent(row.pct)
      ]);
    })
  ];

  return "\uFEFF" + lines.join("\n");
}

function buildCsvContent(model) {
  if (model.kind === "bookings") return buildCsvBookingsContent(model);
  if (model.kind === "comparison") return buildCsvComparisonContent(model);
  return buildCsvSummaryContent(model);
}

function buildXlsxSummaryBytes(model) {
  const xlsx = window.XLSX;
  if (!xlsx) throw new Error("XLSX-Bibliothek wurde nicht geladen.");

  const aoa = [
    ["Export", "Finanz Tracker Jahresauswertung"],
    ["Jahr", String(model.year)],
    ["Erstellt am", model.createdAt],
    ["Währung", model.currency],
    [],
    ["Monat", "Einnahmen", "Ausgaben", "Saldo"],
    ...model.rows.map(row => [
      monthNamesForUi()[row.month - 1],
      Number(row.income || 0),
      Number(row.expense || 0),
      Number(row.net || 0)
    ]),
    [],
    [
      "Gesamtsumme",
      Number(model.totals.income || 0),
      Number(model.totals.expense || 0),
      Number(model.totals.net || 0)
    ]
  ];

  const ws = xlsx.utils.aoa_to_sheet(aoa);
  ws["!cols"] = [
    { wch: 20 },
    { wch: 16 },
    { wch: 16 },
    { wch: 16 }
  ];

  const wb = xlsx.utils.book_new();
  xlsx.utils.book_append_sheet(wb, ws, "Auswertung " + model.year);

  const arr = xlsx.write(wb, { type: "array", bookType: "xlsx" });
  return new Uint8Array(arr);
}

function buildXlsxBookingsBytes(model) {
  const xlsx = window.XLSX;
  if (!xlsx) throw new Error("XLSX-Bibliothek wurde nicht geladen.");

  const isTaxScope = model.exportVariant === "tax-year-bookings";
  const scopeLabel = isTaxScope
    ? "Steuererklärung-Buchungen " + model.year
    : (model.month
      ? reportExportMonthLabel(model.month) + " " + model.year
      : String(model.year));
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

  const ws = xlsx.utils.aoa_to_sheet(aoa);
  ws["!cols"] = [
    { wch: 12 },
    { wch: 30 },
    { wch: 22 },
    { wch: 12 },
    { wch: 14 },
    { wch: 18 },
    { wch: 16 },
    { wch: 36 }
  ];

  const wb = xlsx.utils.book_new();
  const name = isTaxScope
    ? "Steuer " + model.year
    : (model.month
      ? "Buchungen " + String(model.month).padStart(2, "0") + "." + model.year
      : "Buchungen " + model.year);
  xlsx.utils.book_append_sheet(wb, ws, name.slice(0, 31));

  const arr = xlsx.write(wb, { type: "array", bookType: "xlsx" });
  return new Uint8Array(arr);
}

function buildXlsxComparisonBytes(model) {
  const xlsx = window.XLSX;
  if (!xlsx) throw new Error("XLSX-Bibliothek wurde nicht geladen.");

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
      row.isCurrency ? Number(row.prev || 0) : Number(row.prev || 0),
      row.isCurrency ? Number(row.current || 0) : Number(row.current || 0),
      row.isCurrency ? Number(row.delta || 0) : Number(row.delta || 0),
      reportExportPercent(row.pct)
    ])
  ];

  const ws = xlsx.utils.aoa_to_sheet(aoa);
  ws["!cols"] = [
    { wch: 20 },
    { wch: 16 },
    { wch: 16 },
    { wch: 16 },
    { wch: 14 }
  ];

  const wb = xlsx.utils.book_new();
  xlsx.utils.book_append_sheet(wb, ws, "Vergleich " + model.year);

  const arr = xlsx.write(wb, { type: "array", bookType: "xlsx" });
  return new Uint8Array(arr);
}

function buildXlsxBytes(model) {
  if (model.kind === "bookings") return buildXlsxBookingsBytes(model);
  if (model.kind === "comparison") return buildXlsxComparisonBytes(model);
  return buildXlsxSummaryBytes(model);
}

function buildPdfSummaryBytes(model) {
  const jsPdfNs = window.jspdf;
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
    monthNamesForUi()[row.month - 1],
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
  const jsPdfNs = window.jspdf;
  if (!jsPdfNs?.jsPDF) throw new Error("PDF-Bibliothek wurde nicht geladen.");

  const doc = new jsPdfNs.jsPDF({ unit: "pt", format: "a4", orientation: "landscape" });

  const isTaxScope = model.exportVariant === "tax-year-bookings";
  const scopeLabel = isTaxScope
    ? "Steuererklärung-Buchungen " + model.year
    : (model.month
      ? reportExportMonthLabel(model.month) + " " + model.year
      : String(model.year));
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
  const jsPdfNs = window.jspdf;
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
    if (model.exportVariant === "tax-year-bookings") {
      return "steuererklaerung_buchungen_" + model.year + "." + ext;
    }
    if (model.month) {
      return "buchungen_" + String(model.month).padStart(2, "0") + "_" + model.year + "." + ext;
    }
    return "buchungen_" + model.year + "." + ext;
  }
  if (model.kind === "comparison") {
    return "jahresvergleich_" + model.year + "_vs_" + model.prevYear + "." + ext;
  }
  return "auswertung_" + model.year + "." + ext;
}
function bytesToBase64(bytes) {
  let binary = "";
  const chunkSize = 0x8000;
  for (let i = 0; i < bytes.length; i += chunkSize) {
    const chunk = bytes.subarray(i, i + chunkSize);
    binary += String.fromCharCode.apply(null, Array.from(chunk));
  }
  return btoa(binary);
}

function triggerDownload(filename, content, mimeType = "application/octet-stream") {
  const blob = content instanceof Blob ? content : new Blob([content], { type: mimeType });
  const url = URL.createObjectURL(blob);
  const a = document.createElement("a");
  a.href = url;
  a.download = filename;
  document.body.appendChild(a);
  a.click();
  a.remove();
  URL.revokeObjectURL(url);
}

async function writeBinaryReportViaTauri(filename, bytes) {
  const base64 = bytesToBase64(bytes);
  return tryInvokeTauriCommand("write_report_binary", {
    filename,
    contentBase64: base64,
    content_base64: base64
  });
}

async function exportReport() {
  const year = parseYear(el.reportYearInput.value);
  if (!year) {
    showToast(
      resolvedLangCode() === "en" ? "Please enter a valid year first." : "Bitte zuerst ein gültiges Jahr eintragen.",
      "error"
    );
    return;
  }

  const scope = getReportExportScope();
  const month = scope === "month-bookings" ? getReportExportMonth() : null;
  if (scope === "month-bookings" && month === null) {
    showToast(
      resolvedLangCode() === "en" ? "Please choose a valid month for export." : "Bitte einen gültigen Monat für den Export wählen.",
      "error"
    );
    return;
  }

  const model = buildReportExportModel(year, scope, month);
  const format = String(el.reportExportFormat?.value || "pdf").toLowerCase();

  if (format === "xlsx") {
    const filename = reportExportFilename(model, "xlsx");
    let bytes;
    try {
      bytes = buildXlsxBytes(model);
    } catch (err) {
      showToast(resolvedLangCode() === "en" ? "Could not generate XLSX." : "XLSX konnte nicht erstellt werden.", "error");
      return;
    }

    try {
      const writtenPath = await writeBinaryReportViaTauri(filename, bytes);
      if (writtenPath) {
        showToast((resolvedLangCode() === "en" ? "XLSX exported: " : "XLSX exportiert: ") + writtenPath, "success");
        return;
      }
    } catch (err) {
      const message = String(err || "");
      if (message.includes("EXPORT_CANCELED")) {
        showToast(t("exportCanceled"), "info");
        return;
      }
      triggerDownload(filename, bytes, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
      showToast(
        resolvedLangCode() === "en" ? "XLSX browser download started." : "XLSX als Browser-Download gestartet.",
        "success"
      );
      return;
    }

    triggerDownload(filename, bytes, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    showToast(resolvedLangCode() === "en" ? "XLSX browser download started." : "XLSX als Browser-Download gestartet.", "success");
    return;
  }

  if (format === "pdf") {
    const filename = reportExportFilename(model, "pdf");
    let bytes;
    try {
      bytes = buildPdfBytes(model);
    } catch (err) {
      showToast(resolvedLangCode() === "en" ? "Could not generate PDF." : "PDF konnte nicht erstellt werden.", "error");
      return;
    }

    try {
      const writtenPath = await writeBinaryReportViaTauri(filename, bytes);
      if (writtenPath) {
        showToast((resolvedLangCode() === "en" ? "PDF exported: " : "PDF exportiert: ") + writtenPath, "success");
        return;
      }
    } catch (err) {
      const message = String(err || "");
      if (message.includes("EXPORT_CANCELED")) {
        showToast(t("exportCanceled"), "info");
        return;
      }
      triggerDownload(filename, bytes, "application/pdf");
      showToast(resolvedLangCode() === "en" ? "PDF browser download started." : "PDF als Browser-Download gestartet.", "success");
      return;
    }

    triggerDownload(filename, bytes, "application/pdf");
    showToast(resolvedLangCode() === "en" ? "PDF browser download started." : "PDF als Browser-Download gestartet.", "success");
    return;
  }

  const filename = reportExportFilename(model, "csv");
  const content = buildCsvContent(model);

  try {
    const writtenPath = await tryInvokeTauriCommand("write_report_csv", { filename, content });
    if (writtenPath) {
      showToast((resolvedLangCode() === "en" ? "CSV exported: " : "CSV exportiert: ") + writtenPath, "success");
      return;
    }
  } catch (err) {
    const message = String(err || "");
    if (message.includes("EXPORT_CANCELED")) {
      showToast(t("exportCanceled"), "info");
      return;
    }
    triggerDownload(filename, content, "text/csv;charset=utf-8");
    showToast(resolvedLangCode() === "en" ? "CSV browser download started." : "CSV als Browser-Download gestartet.", "success");
    return;
  }

  triggerDownload(filename, content, "text/csv;charset=utf-8");
  showToast(resolvedLangCode() === "en" ? "CSV browser download started." : "CSV als Browser-Download gestartet.", "success");
}
function showDialog({
  title = resolvedLangCode() === "en" ? "Info" : "Hinweis",
  message = "",
  mode = "alert",
  defaultValue = "",
  okText = "OK",
  cancelText = resolvedLangCode() === "en" ? "Cancel" : "Abbrechen",
  danger = false
}) {
  return new Promise(resolve => {
    const { dialogOverlay, dialogTitle, dialogMessage, dialogInput, dialogOkBtn, dialogCancelBtn } = el;

    dialogTitle.textContent = title;
    dialogMessage.textContent = message;
    dialogOkBtn.textContent = okText;
    dialogCancelBtn.textContent = cancelText;

    dialogOkBtn.classList.remove("danger", "primary");
    dialogOkBtn.classList.add(danger ? "danger" : "primary");

    const isPrompt = mode === "prompt";
    const hasCancel = mode === "confirm" || mode === "prompt";

    dialogInput.classList.toggle("hidden", !isPrompt);
    dialogCancelBtn.classList.toggle("hidden", !hasCancel);

    dialogInput.value = defaultValue;

    dialogOverlay.classList.remove("hidden");
    dialogOverlay.setAttribute("aria-hidden", "false");

    if (isPrompt) {
      setTimeout(() => {
        dialogInput.focus();
        dialogInput.select();
      }, 0);
    } else {
      setTimeout(() => dialogOkBtn.focus(), 0);
    }

    const finish = result => {
      cleanup();
      dialogOverlay.classList.add("hidden");
      dialogOverlay.setAttribute("aria-hidden", "true");
      resolve(result);
    };

    const onOk = () => {
      if (mode === "prompt") {
        finish(dialogInput.value);
      } else if (mode === "confirm") {
        finish(true);
      } else {
        finish(true);
      }
    };

    const onCancel = () => {
      if (mode === "confirm") {
        finish(false);
      } else if (mode === "prompt") {
        finish(null);
      } else {
        finish(true);
      }
    };

    const onKeyDown = evt => {
      if (evt.key === "Escape" && hasCancel) {
        evt.preventDefault();
        onCancel();
      }
      if (evt.key === "Enter") {
        if (!isPrompt || document.activeElement === dialogInput) {
          evt.preventDefault();
          onOk();
        }
      }
    };

    const onOverlayClick = evt => {
      if (evt.target === dialogOverlay && hasCancel) {
        onCancel();
      }
    };

    const cleanup = () => {
      dialogOkBtn.removeEventListener("click", onOk);
      dialogCancelBtn.removeEventListener("click", onCancel);
      dialogOverlay.removeEventListener("click", onOverlayClick);
      document.removeEventListener("keydown", onKeyDown);
    };

    dialogOkBtn.addEventListener("click", onOk);
    dialogCancelBtn.addEventListener("click", onCancel);
    dialogOverlay.addEventListener("click", onOverlayClick);
    document.addEventListener("keydown", onKeyDown);
  });
}

function showToast(message, type = "success", durationMs = 2600) {
  const host = el.toastContainer;
  if (!host) return;

  const toast = document.createElement("div");
  toast.className = "toast " + type;
  toast.textContent = message;
  host.appendChild(toast);

  requestAnimationFrame(() => {
    toast.classList.add("show");
  });

  const remove = () => {
    toast.classList.remove("show");
    setTimeout(() => toast.remove(), 180);
  };

  setTimeout(remove, durationMs);
}

async function showInfo(message, title = (resolvedLangCode() === "en" ? "Info" : "Hinweis")) {
  await showDialog({ title, message, mode: "alert", okText: "OK" });
}

async function askConfirm(message, title = (resolvedLangCode() === "en" ? "Confirmation" : "Bestätigung"), danger = false) {
  return showDialog({
    title,
    message,
    mode: "confirm",
    okText: danger ? t("delete") : (resolvedLangCode() === "en" ? "Confirm" : "Bestätigen"),
    cancelText: resolvedLangCode() === "en" ? "Cancel" : "Abbrechen",
    danger
  });
}

async function askText(message, title = (resolvedLangCode() === "en" ? "Input" : "Eingabe"), defaultValue = "") {
  return showDialog({
    title,
    message,
    mode: "prompt",
    defaultValue,
    okText: t("save"),
    cancelText: resolvedLangCode() === "en" ? "Cancel" : "Abbrechen"
  });
}



function formatSyncDate(ts) {
  const n = Number(ts);
  if (!Number.isFinite(n) || n <= 0) return "-";
  return new Date(n).toLocaleString(resolvedLangCode() === "en" ? "en-US" : "de-DE");
}

function updateSyncMetaDisplay() {
  const lastWriteRaw = localStorage.getItem(SYNC_LAST_WRITE_KEY);
  const lastRestoreRaw = localStorage.getItem(SYNC_LAST_RESTORE_KEY);

  if (el.syncLastWrite) {
    el.syncLastWrite.textContent = tf("lastSyncBackup", { value: formatSyncDate(lastWriteRaw) });
  }

  if (el.syncLastRestore) {
    el.syncLastRestore.textContent = tf("lastSyncRestore", { value: formatSyncDate(lastRestoreRaw) });
  }
}

function markSyncWriteSuccess() {
  try {
    localStorage.setItem(SYNC_LAST_WRITE_KEY, String(Date.now()));
  } catch (_) {}
  updateSyncMetaDisplay();
}

function markSyncRestoreSuccess() {
  try {
    localStorage.setItem(SYNC_LAST_RESTORE_KEY, String(Date.now()));
  } catch (_) {}
  updateSyncMetaDisplay();
}

async function tryAutoRestoreFromSync(quiet = false) {
  if (!hasTauriRuntime() || !syncConfigured) return false;

  try {
    const raw = await tryInvokeTauriCommand("sync_restore_latest", {});
    if (!raw || typeof raw !== "string") return false;
    if (raw === lastSyncPayloadSnapshot) return false;

    const parsed = JSON.parse(raw);
    applyLoadedStateToUi(parsed, { keepMonth: true });
    markSyncRestoreSuccess();
    lastSyncPayloadSnapshot = raw;
    skipNextSyncBackupWrite = true;
    await persistState();
    return true;
  } catch (err) {
    if (!quiet) {
      console.warn("Auto-Restore aus Sync-Ordner übersprungen", err);
    }
    return false;
  }
}

async function tryAutoRestoreFromSyncWithStartupRetry() {
  // Folder sync tools may deliver the file a few moments after app start.
  // Retry briefly on startup so users do not need a second app launch.
  const maxAttempts = 20;
  const retryDelayMs = 1000;

  for (let attempt = 1; attempt <= maxAttempts; attempt += 1) {
    const restored = await tryAutoRestoreFromSync();
    if (restored) return true;
    if (attempt < maxAttempts) {
      await new Promise(resolve => setTimeout(resolve, retryDelayMs));
    }
  }

  // Some sync providers deliver late; do one extra delayed pass without requiring app restart.
  setTimeout(() => {
    tryAutoRestoreFromSync().catch(err => {
      console.warn("Verzögerter Auto-Restore fehlgeschlagen", err);
    });
  }, 15000);
  return false;
}

function scheduleSyncAutoBackup(payload) {
  if (skipNextSyncBackupWrite) {
    skipNextSyncBackupWrite = false;
    return;
  }

  if (!hasTauriRuntime() || !syncConfigured) return;

  if (syncAutoBackupTimer) clearTimeout(syncAutoBackupTimer);
  syncAutoBackupTimer = setTimeout(async () => {
    syncAutoBackupTimer = null;
    try {
      await tryInvokeTauriCommand("sync_write_backup", { payload });
      lastSyncPayloadSnapshot = payload;
      markSyncWriteSuccess();
    } catch (err) {
      console.warn("Automatisches Sync-Backup fehlgeschlagen", err);
    }
  }, 300);
}

function refreshSyncAutoRestoreTimer() {
  if (syncAutoRestoreTimer) {
    clearInterval(syncAutoRestoreTimer);
    syncAutoRestoreTimer = null;
  }

  if (!hasTauriRuntime() || !syncConfigured) return;

  // Keep desktop UI fresh while app stays open (no manual F5 needed).
  syncAutoRestoreTimer = setInterval(() => {
    tryAutoRestoreFromSync(true).catch(err => {
      console.warn("Hintergrund-Sync-Restore fehlgeschlagen", err);
    });
  }, 5000);
}

async function refreshSyncStatus() {
  if (!el.cloudStatus) return;

  const hasDesktop = hasTauriRuntime();
  if (!hasDesktop) {
    syncConfigured = false;
    refreshSyncAutoRestoreTimer();
    setSyncStatus(resolvedLangCode() === "en" ? "Sync: only available in Desktop (Tauri)" : "Sync: nur in Desktop (Tauri) verfügbar");
    return;
  }

  try {
    const status = await tryInvokeTauriCommand("sync_get_status", {});
    if (!status) {
      syncConfigured = false;
      refreshSyncAutoRestoreTimer();
      setSyncStatus(resolvedLangCode() === "en" ? "Sync: status not available" : "Sync: Status nicht verfügbar");
      return;
    }

    if (el.syncFolderInput && status.folder_path) {
      el.syncFolderInput.value = status.folder_path;
    }

    if (!status.configured) {
      syncConfigured = false;
      refreshSyncAutoRestoreTimer();
      setSyncStatus(t("statusNotConfigured"));
      return;
    }

    syncConfigured = true;
    refreshSyncAutoRestoreTimer();
    setSyncStatus((resolvedLangCode() === "en" ? "Sync: active -> " : "Sync: aktiv -> ") + status.folder_path);
  } catch (err) {
    syncConfigured = false;
    refreshSyncAutoRestoreTimer();
    setSyncStatus(resolvedLangCode() === "en" ? "Sync: status error" : "Sync: Statusfehler");
    console.warn("Sync-Status konnte nicht geladen werden", err);
  }
}

function setSyncStatus(message) {
  if (!el.cloudStatus) return;
  el.cloudStatus.textContent = message;
}

function escapeHtml(text) {
  return text
    .replaceAll("&", "&amp;")
    .replaceAll("<", "&lt;")
    .replaceAll(">", "&gt;")
    .replaceAll('"', "&quot;")
    .replaceAll("'", "&#039;");
}

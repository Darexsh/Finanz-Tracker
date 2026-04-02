const STORAGE_KEY = "finanz-universal-v1";
const LEGACY_MIGRATED_KEY = "finanz-universal-migrated-to-sqlite-v1";
const RECOVERY_FALLBACK_KEY = "finanz-universal-recovery-fallback-v1";
const DAILY_BACKUP_KEY = "finanz-universal-daily-backup-v1";

const CATEGORIES = [
  "Miete", "Nebenkosten", "Strom/Gas", "Internet/Handy", "Lebensmittel", "Drogerie",
  "Haushalt", "Mobilität", "Auto", "ÖPNV", "Versicherung", "Gesundheit", "Shopping",
  "Freizeit", "Gastronomie", "Reisen", "Bildung", "Kinder", "Haustiere", "Abo",
  "Steuern/Gebühren", "Gehalt", "Nebenverdienst", "Sonstiges"
];

const ACCOUNTS = ["Girokonto", "Kreditkarte", "Paypal", "Bargeld", "Extra Konto", "Sonstiges"];

const MONTH_NAMES = [
  "Januar", "Februar", "März", "April", "Mai", "Juni",
  "Juli", "August", "September", "Oktober", "November", "Dezember"
];

const KEYWORD_MAP = [
  ["teelicht", "Shopping"], ["kerze", "Shopping"], ["temu", "Shopping"], ["amazon", "Shopping"],
  ["dm", "Drogerie"], ["rossmann", "Drogerie"], ["lidl", "Lebensmittel"], ["aldi", "Lebensmittel"],
  ["rewe", "Lebensmittel"], ["tank", "Auto"], ["benzin", "Auto"], ["bahn", "ÖPNV"],
  ["versicherung", "Versicherung"], ["arzt", "Gesundheit"], ["apotheke", "Gesundheit"],
  ["netflix", "Abo"], ["spotify", "Abo"], ["chatgpt", "Abo"], ["miete", "Miete"],
  ["gehalt", "Gehalt"], ["urlaub", "Reisen"]
];

const state = createDefaultState();
let stateReady = false;
let saveTimer = null;
let categoryManuallyOverridden = false;
let lastAutoCategory = null;
let selectedBookingId = null;
let recoveryNotice = null;
const selectedBookingIds = new Set();
const monthlyChartState = { bars: [], rows: [], year: null, hoverIndex: -1, pinnedIndex: null };

const el = {
  userSelect: document.getElementById("userSelect"),
  addUserBtn: document.getElementById("addUserBtn"),
  renameUserBtn: document.getElementById("renameUserBtn"),
  deleteUserBtn: document.getElementById("deleteUserBtn"),
  statsCards: document.getElementById("statsCards"),
  topCategories: document.getElementById("topCategories"),
  monthlyChart: document.getElementById("monthlyChart"),
  monthlyChartTooltip: document.getElementById("monthlyChartTooltip"),

  bookingForm: document.getElementById("bookingForm"),
  monthInput: document.getElementById("monthInput"),
  descriptionInput: document.getElementById("descriptionInput"),
  categoryInput: document.getElementById("categoryInput"),
  amountInput: document.getElementById("amountInput"),
  typeInput: document.getElementById("typeInput"),
  accountInput: document.getElementById("accountInput"),
  noteInput: document.getElementById("noteInput"),

  fMonth: document.getElementById("fMonth"),
  fYear: document.getElementById("fYear"),
  fType: document.getElementById("fType"),
  fCategory: document.getElementById("fCategory"),
  fAccount: document.getElementById("fAccount"),
  fSearch: document.getElementById("fSearch"),
  resetFiltersBtn: document.getElementById("resetFiltersBtn"),

  bookingsBody: document.getElementById("bookingsBody"),
  deleteSelectedBookingsBtn: document.getElementById("deleteSelectedBookingsBtn"),
  selectAllBookings: document.getElementById("selectAllBookings"),
  selectedBookingsInfo: document.getElementById("selectedBookingsInfo"),

  reportYearInput: document.getElementById("reportYearInput"),
  loadReportBtn: document.getElementById("loadReportBtn"),
  exportReportBtn: document.getElementById("exportReportBtn"),
  reportStatsCards: document.getElementById("reportStatsCards"),
  reportBody: document.getElementById("reportBody"),

  dialogOverlay: document.getElementById("dialogOverlay"),
  dialogTitle: document.getElementById("dialogTitle"),
  dialogMessage: document.getElementById("dialogMessage"),
  dialogInput: document.getElementById("dialogInput"),
  dialogOkBtn: document.getElementById("dialogOkBtn"),
  dialogCancelBtn: document.getElementById("dialogCancelBtn")
};

init().catch(err => {
  console.error("Init fehlgeschlagen", err);
});

async function init() {
  bindTabs();
  initSelectOptions();
  bindEvents();
  await hydrateStateFromStorage();
  ensureActiveUser();
  setDefaultMonth();
  setDefaultReportYear();
  render();
  stateReady = true;
  await flushRecoveryNotice();
}

function defaultUser() {
  return { id: uid(), name: "Standard" };
}

function createDefaultState() {
  const user = defaultUser();
  return { users: [user], activeUserId: user.id, bookings: [] };
}

function sanitizeLoadedState(loaded) {
  if (!loaded || typeof loaded !== "object") return createDefaultState();

  const users = Array.isArray(loaded.users) ? loaded.users.filter(u => u && u.id && u.name) : [];
  const bookings = Array.isArray(loaded.bookings) ? loaded.bookings : [];

  if (users.length === 0) return createDefaultState();

  const activeUserId = users.some(u => u.id === loaded.activeUserId) ? loaded.activeUserId : users[0].id;
  return { users, activeUserId, bookings };
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

function saveState() {
  if (!stateReady) return;

  if (saveTimer) clearTimeout(saveTimer);
  saveTimer = setTimeout(() => {
    saveTimer = null;
    persistState().catch(err => console.error("Speichern fehlgeschlagen", err));
  }, 50);
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
  await showInfo(message, "Wiederherstellung");
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
  el.monthInput.value = dd + "." + mm + "." + d.getFullYear();
}

function setDefaultReportYear() {
  el.reportYearInput.value = String(new Date().getFullYear());
}

function initSelectOptions() {
  fillSelect(el.categoryInput, CATEGORIES);
  fillSelect(el.accountInput, ACCOUNTS);

  fillSelect(el.fMonth, ["Monat: Alle", ...Array.from({ length: 12 }, (_, i) => `${String(i + 1).padStart(2, "0")}`)], true);
  fillSelect(el.fYear, ["Jahr: Alle", String(new Date().getFullYear())], true);
  fillSelect(el.fCategory, ["Kategorie: Alle", ...CATEGORIES], true);
  fillSelect(el.fAccount, ["Konto: Alle", ...ACCOUNTS], true);
}

function fillSelect(select, values, withAllPrefix = false) {
  select.innerHTML = "";
  values.forEach(v => {
    const option = document.createElement("option");
    if (withAllPrefix && v.includes(": Alle")) {
      option.value = "Alle";
      option.textContent = v;
    } else {
      option.value = v;
      option.textContent = v;
    }
    select.appendChild(option);
  });
}

function bindTabs() {
  document.querySelectorAll(".tab").forEach(btn => {
    btn.addEventListener("click", () => {
      document.querySelectorAll(".tab").forEach(t => t.classList.remove("active"));
      document.querySelectorAll(".tab-panel").forEach(p => p.classList.remove("active"));
      btn.classList.add("active");
      document.getElementById(btn.dataset.tab).classList.add("active");
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
    const name = await askText("Name des neuen Benutzers:", "Neuer Benutzer");
    if (name === null) return;
    const trimmed = name.trim();
    if (!trimmed) return;
    if (state.users.some(u => u.name.toLowerCase() === trimmed.toLowerCase())) {
      await showInfo("Benutzer existiert bereits.");
      return;
    }
    const user = { id: uid(), name: trimmed };
    state.users.push(user);
    state.activeUserId = user.id;
    selectedBookingId = null;
    clearForm(true);
    saveState();
    render();
  });

  el.renameUserBtn.addEventListener("click", async () => {
    const user = activeUser();
    const name = await askText("Neuer Name:", "Benutzer umbenennen", user.name);
    if (name === null) return;

    const trimmed = name.trim();
    if (!trimmed) {
      await showInfo("Bitte einen Namen eingeben.");
      return;
    }
    if (state.users.some(u => u.id !== user.id && u.name.toLowerCase() === trimmed.toLowerCase())) {
      await showInfo("Benutzer existiert bereits.");
      return;
    }

    user.name = trimmed;
    saveState();
    renderUsers();
  });

  el.deleteUserBtn.addEventListener("click", async () => {
    if (state.users.length <= 1) {
      await showInfo("Der letzte Benutzer kann nicht gelöscht werden.");
      return;
    }

    const user = activeUser();
    const count = state.bookings.filter(b => b.userId === user.id).length;

    if (count > 0) {
      const ok = await askConfirm(
        `Sind Sie sicher, dass Sie Benutzer '${user.name}' löschen möchten?\nEs werden ${count} Buchungen gelöscht.`,
        "Benutzer löschen",
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
    await showInfo(`'${user.name}' wurde gelöscht.`);
  });

  el.descriptionInput.addEventListener("input", () => {
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

  el.bookingForm.addEventListener("submit", async e => {
    e.preventDefault();
    const payload = await validateBookingForm();
    if (!payload) return;

    if (selectedBookingId) {
      const idx = state.bookings.findIndex(b => b.id === selectedBookingId && b.userId === activeUser().id);
      if (idx >= 0) {
        state.bookings[idx] = { ...state.bookings[idx], ...payload };
      } else {
        state.bookings.push({ id: uid(), userId: activeUser().id, createdAt: Date.now(), ...payload });
      }
    } else {
      state.bookings.push({ id: uid(), userId: activeUser().id, createdAt: Date.now(), ...payload });
    }

    saveState();
    selectedBookingId = null;
    clearForm(true);
    render();
  });

  el.bookingForm.addEventListener("reset", e => {
    e.preventDefault();
    selectedBookingId = null;
    clearForm(false);
    renderBookings();
  });

  el.deleteSelectedBookingsBtn.addEventListener("click", async () => {
    const ids = Array.from(selectedBookingIds);
    if (!ids.length) {
      await showInfo("Bitte mindestens eine Buchung markieren.");
      return;
    }

    const ok = await askConfirm(
      `Sind Sie sicher, dass Sie ${ids.length} ausgewählte Buchung(en) löschen möchten?`,
      "Mehrere Buchungen löschen",
      true
    );
    if (!ok) return;

    state.bookings = state.bookings.filter(b => !(b.userId === activeUser().id && selectedBookingIds.has(b.id)));
    selectedBookingIds.clear();
    selectedBookingId = null;
    saveState();
    render();
  });

  el.selectAllBookings.addEventListener("change", () => {
    const entries = filteredBookings().sort((a, b) => {
      const byDate = monthSortKey(b.month) - monthSortKey(a.month);
      if (byDate !== 0) return byDate;
      return (Number(b.createdAt) || 0) - (Number(a.createdAt) || 0);
    });

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
    el.fMonth.value = "Alle";
    el.fYear.value = "Alle";
    el.fType.value = "Alle";
    el.fCategory.value = "Alle";
    el.fAccount.value = "Alle";
    el.fSearch.value = "";
    renderBookings();
  });

  el.loadReportBtn.addEventListener("click", () => renderReport());
  setupMonthlyChartInteractions();
  window.addEventListener("resize", () => {
    if (!stateReady) return;
    renderDashboard();
  });

  el.exportReportBtn.addEventListener("click", async () => {
    await exportReportCsv();
  });
}

function parseMonth(value) {
  const t = String(value).trim();

  const full = t.match(/^(\d{2})\.(\d{2})\.(\d{4})$/);
  if (full) {
    const dd = Number(full[1]);
    const mm = Number(full[2]);
    const yyyy = Number(full[3]);
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

function suggestCategory(description) {
  const text = description.toLowerCase();
  const hit = KEYWORD_MAP.find(([k]) => text.includes(k));
  return hit ? hit[1] : null;
}

async function validateBookingForm() {
  const month = parseMonth(el.monthInput.value);
  if (!month) {
    await showInfo("Bitte Datum als TT.MM.JJJJ eingeben.");
    return null;
  }

  const description = el.descriptionInput.value.trim();
  if (!description) {
    await showInfo("Bitte eine Beschreibung eingeben.");
    return null;
  }

  const amount = parseFloat(String(el.amountInput.value).replace(",", "."));
  if (Number.isNaN(amount) || amount < 0) {
    await showInfo("Ungültiger Betrag.");
    return null;
  }

  return {
    month,
    description,
    category: el.categoryInput.value || "Sonstiges",
    txType: ["Ausgabe", "Einnahme"].includes(el.typeInput.value) ? el.typeInput.value : "Ausgabe",
    amount,
    account: el.accountInput.value || "Girokonto",
    note: el.noteInput.value.trim()
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
  if (!keepMonth) setDefaultMonth();
}

function loadEntryIntoForm(entry) {
  el.monthInput.value = entry.month;
  el.descriptionInput.value = entry.description;
  el.categoryInput.value = entry.category || "Sonstiges";
  el.typeInput.value = entry.txType;
  el.amountInput.value = String(entry.amount).replace(".", ",");
  el.accountInput.value = entry.account || "Girokonto";
  el.noteInput.value = entry.note || "";
  lastAutoCategory = entry.category || null;
  categoryManuallyOverridden = true;
}

function userBookings() {
  return state.bookings.filter(b => b.userId === activeUser().id);
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
  fillSelect(el.fYear, ["Jahr: Alle", ...years], true);

  if (previous === "Alle") {
    el.fYear.value = "Alle";
  } else {
    el.fYear.value = years.includes(previous) ? previous : "Alle";
  }
}

function filteredBookings() {
  return userBookings().filter(b => {
    const parts = getDateParts(b.month);
    if (!parts) return false;
    if (el.fMonth.value !== "Alle" && String(parts.mm).padStart(2, "0") !== el.fMonth.value) return false;
    if (el.fYear.value !== "Alle" && String(parts.yyyy) !== el.fYear.value) return false;
    if (el.fType.value !== "Alle" && b.txType !== el.fType.value) return false;
    if (el.fCategory.value !== "Alle" && b.category !== el.fCategory.value) return false;
    if (el.fAccount.value !== "Alle" && b.account !== el.fAccount.value) return false;
    const s = el.fSearch.value.trim().toLowerCase();
    if (s && !(b.description.toLowerCase().includes(s) || (b.note || "").toLowerCase().includes(s))) return false;
    return true;
  });
}

function render() {
  renderUsers();
  renderDashboard();
  renderBookings();
  renderReport();
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
  return `${v.toFixed(2).replace(".", ",")} €`;
}

function renderDashboard() {
  const now = new Date();
  const month = String(now.getMonth() + 1).padStart(2, "0") + "." + now.getFullYear();
  const entries = userBookings();
  const monthEntries = entries.filter(e => monthYearKey(e.month) === month);

  const totalIncome = entries.filter(e => e.txType === "Einnahme").reduce((a, b) => a + b.amount, 0);
  const totalExpense = entries.filter(e => e.txType === "Ausgabe").reduce((a, b) => a + b.amount, 0);
  const monthIncome = monthEntries.filter(e => e.txType === "Einnahme").reduce((a, b) => a + b.amount, 0);
  const monthExpense = monthEntries.filter(e => e.txType === "Ausgabe").reduce((a, b) => a + b.amount, 0);

  const stats = [
    ["Aktueller Saldo", euro(totalIncome - totalExpense)],
    ["Einnahmen (Monat)", euro(monthIncome)],
    ["Ausgaben (Monat)", euro(monthExpense)],
    ["Monatsüberschuss", euro(monthIncome - monthExpense)]
  ];

  el.statsCards.innerHTML = stats.map(([k, v]) => `<article class="card"><p>${k}</p><h4>${v}</h4></article>`).join("");

  const byCategory = {};
  monthEntries.filter(e => e.txType === "Ausgabe").forEach(e => {
    byCategory[e.category] = (byCategory[e.category] || 0) + e.amount;
  });
  const top = Object.entries(byCategory).sort((a, b) => b[1] - a[1]).slice(0, 10);
  el.topCategories.innerHTML = top.length
    ? top.map(([k, v]) => `<li>${k}: ${euro(v)}</li>`).join("")
    : "<li>Keine Ausgaben im aktuellen Monat</li>";

  renderMonthlyCashflowChart(entries, now.getFullYear());
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
  return `${MONTH_NAMES[month - 1]} ${year}`;
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
    `<p>Einnahmen: <span class="i">${euro(row.income)}</span></p>`,
    `<p>Ausgaben: <span class="e">${euro(row.expense)}</span></p>`,
    `<p>Saldo: <span class="n">${euro(net)}</span></p>`
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
  const entries = filteredBookings().sort((a, b) => {
    const byDate = monthSortKey(b.month) - monthSortKey(a.month);
    if (byDate !== 0) return byDate;
    return (Number(b.createdAt) || 0) - (Number(a.createdAt) || 0);
  });

  const visibleIds = new Set(entries.map(e => e.id));
  Array.from(selectedBookingIds).forEach(id => {
    if (!visibleIds.has(id)) selectedBookingIds.delete(id);
  });

  el.bookingsBody.innerHTML = entries.map(e => {
    const rowClass = e.id === selectedBookingId ? "selected-row" : "";
    const checked = selectedBookingIds.has(e.id) ? "checked" : "";
    return `
    <tr class="${rowClass}" data-id="${e.id}">
      <td class="check-col"><input type="checkbox" class="booking-check" data-id="${e.id}" ${checked} /></td>
      <td>${e.month}</td>
      <td>${escapeHtml(e.description)}</td>
      <td>${e.category}</td>
      <td>${e.txType}</td>
      <td>${euro(e.amount)}</td>
      <td>${e.account}</td>
      <td>${escapeHtml(e.note || "")}</td>
    </tr>
  `;
  }).join("");

  if (el.selectedBookingsInfo) {
    el.selectedBookingsInfo.textContent = `${selectedBookingIds.size} ausgewählt`;
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

  el.bookingsBody.querySelectorAll("tr").forEach(row => {
    row.addEventListener("click", evt => {
      if (evt.target instanceof HTMLInputElement) return;
      selectedBookingId = row.dataset.id;
      renderBookings();
    });

    row.addEventListener("dblclick", evt => {
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
  const rows = [];
  for (let month = 1; month <= 12; month++) {
    const key = `${String(month).padStart(2, "0")}.${year}`;
    const items = userBookings().filter(e => monthYearKey(e.month) === key);
    const income = items.filter(e => e.txType === "Einnahme").reduce((a, b) => a + b.amount, 0);
    const expense = items.filter(e => e.txType === "Ausgabe").reduce((a, b) => a + b.amount, 0);
    rows.push({ month, income, expense, net: income - expense });
  }
  return rows;
}

function renderReport() {
  const year = parseYear(el.reportYearInput.value);
  if (!year) {
    el.reportStatsCards.innerHTML = "";
    el.reportBody.innerHTML = '<tr><td colspan="4">Bitte ein gültiges Jahr wie 2026 eingeben.</td></tr>';
    return;
  }

  const rows = reportRows(year);
  const totalIncome = rows.reduce((sum, r) => sum + r.income, 0);
  const totalExpense = rows.reduce((sum, r) => sum + r.expense, 0);
  const net = totalIncome - totalExpense;

  el.reportStatsCards.innerHTML = [
    ["Jahr Einnahmen", euro(totalIncome)],
    ["Jahr Ausgaben", euro(totalExpense)],
    ["Jahr Saldo", euro(net)]
  ].map(([k, v]) => `<article class="card"><p>${k}</p><h4>${v}</h4></article>`).join("");

  el.reportBody.innerHTML = rows.map(r => `
    <tr>
      <td>${MONTH_NAMES[r.month - 1]}</td>
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

async function exportReportCsv() {
  const year = parseYear(el.reportYearInput.value);
  if (!year) {
    await showInfo("Bitte zuerst ein gültiges Jahr eintragen.");
    return;
  }

  const lines = ["Monat;Einnahmen;Ausgaben;Saldo"];
  reportRows(year).forEach(r => {
    const income = r.income.toFixed(2).replace(".", ",");
    const expense = r.expense.toFixed(2).replace(".", ",");
    const net = r.net.toFixed(2).replace(".", ",");
    lines.push(`${MONTH_NAMES[r.month - 1]};${income};${expense};${net}`);
  });

  const content = lines.join("\n");
  const filename = `auswertung_${year}.csv`;

  try {
    const writtenPath = await tryInvokeTauriCommand("write_report_csv", { filename, content });
    if (writtenPath) {
      await showInfo(`CSV exportiert:\n${writtenPath}`, "Export");
      return;
    }
  } catch (err) {
    await showInfo(`Export fehlgeschlagen:\n${String(err)}`, "Exportfehler");
    return;
  }

  const blob = new Blob([content], { type: "text/csv;charset=utf-8" });
  const url = URL.createObjectURL(blob);
  const a = document.createElement("a");
  a.href = url;
  a.download = filename;
  document.body.appendChild(a);
  a.click();
  a.remove();
  URL.revokeObjectURL(url);
  await showInfo("CSV wurde als Browser-Download gestartet.", "Export");
}
function showDialog({
  title = "Hinweis",
  message = "",
  mode = "alert",
  defaultValue = "",
  okText = "OK",
  cancelText = "Abbrechen",
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

async function showInfo(message, title = "Hinweis") {
  await showDialog({ title, message, mode: "alert", okText: "OK" });
}

async function askConfirm(message, title = "Bestätigung", danger = false) {
  return showDialog({
    title,
    message,
    mode: "confirm",
    okText: danger ? "Löschen" : "Bestätigen",
    cancelText: "Abbrechen",
    danger
  });
}

async function askText(message, title = "Eingabe", defaultValue = "") {
  return showDialog({
    title,
    message,
    mode: "prompt",
    defaultValue,
    okText: "Speichern",
    cancelText: "Abbrechen"
  });
}

function escapeHtml(text) {
  return text
    .replaceAll("&", "&amp;")
    .replaceAll("<", "&lt;")
    .replaceAll(">", "&gt;")
    .replaceAll('"', "&quot;")
    .replaceAll("'", "&#039;");
}

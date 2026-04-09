export function parseMonth(value) {
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

export function getDateParts(dateStr) {
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

export function monthSortKey(dateStr) {
  const p = getDateParts(dateStr);
  if (!p) return 0;
  return p.yyyy * 10000 + p.mm * 100 + p.dd;
}

export function formatCanonicalDate(canonicalDate, format) {
  const p = getDateParts(canonicalDate);
  if (!p) return String(canonicalDate || "");
  if (format === "YYYY-MM-DD") {
    return `${String(p.yyyy).padStart(4, "0")}-${String(p.mm).padStart(2, "0")}-${String(p.dd).padStart(2, "0")}`;
  }
  if (format === "MM/DD/YYYY") {
    return `${String(p.mm).padStart(2, "0")}/${String(p.dd).padStart(2, "0")}/${String(p.yyyy).padStart(4, "0")}`;
  }
  return `${String(p.dd).padStart(2, "0")}.${String(p.mm).padStart(2, "0")}.${String(p.yyyy).padStart(4, "0")}`;
}

export function dmyToIsoDate(dmy) {
  const p = getDateParts(dmy);
  if (!p) return "";
  return `${String(p.yyyy).padStart(4, "0")}-${String(p.mm).padStart(2, "0")}-${String(p.dd).padStart(2, "0")}`;
}

export function isoDateToDmy(iso) {
  const m = String(iso || "").match(/^(\d{4})-(\d{2})-(\d{2})$/);
  if (!m) return null;
  const yyyy = Number(m[1]);
  const mm = Number(m[2]);
  const dd = Number(m[3]);
  if (yyyy < 2000 || yyyy > 2100 || mm < 1 || mm > 12 || dd < 1 || dd > 31) return null;
  return `${String(dd).padStart(2, "0")}.${String(mm).padStart(2, "0")}.${String(yyyy).padStart(4, "0")}`;
}


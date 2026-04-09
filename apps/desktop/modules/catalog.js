export const CATEGORIES = [
  "Miete", "Nebenkosten", "Strom/Gas", "Internet/Handy", "Lebensmittel", "Drogerie",
  "Haushalt", "Mobilität", "Auto", "Parken", "ÖPNV", "Versicherung", "Abgaben/Beiträge",
  "Gesundheit", "Shopping", "Kleidung", "Elektronik", "Freizeit", "Gaming/Medien",
  "Gastronomie", "Reisen", "Bildung", "Geschenke", "Kinder", "Haustiere", "Abo",
  "Steuern/Gebühren", "Gehalt", "Nebenverdienst", "Transfer", "Sonstiges"
];

export const ACCOUNTS = ["Girokonto", "Kreditkarte", "Paypal", "Bargeld", "Extra Konto", "Sonstiges"];

export const KEYWORD_MAP = [
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

export const CATEGORY_ALIAS_MAP = new Map([
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

export const CATEGORY_LABEL_EN = new Map([
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

export const ACCOUNT_LABEL_EN = new Map([
  ["Girokonto", "Checking Account"],
  ["Kreditkarte", "Credit Card"],
  ["Paypal", "PayPal"],
  ["Bargeld", "Cash"],
  ["Extra Konto", "Extra Account"],
  ["Sonstiges", "Other"]
]);

export function sanitizeCustomCategories(list) {
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

export function allCategories(customCategories = []) {
  const merged = [...CATEGORIES, ...sanitizeCustomCategories(customCategories)];
  const seen = new Set();
  return merged.filter(item => {
    const key = item.toLowerCase();
    if (seen.has(key)) return false;
    seen.add(key);
    return true;
  });
}

export function normalizeCategory(value, customCategories = []) {
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

export function normalizeBookingCategory(entry, customCategories = []) {
  if (!entry || typeof entry !== "object") return entry;
  return { ...entry, category: normalizeCategory(entry.category, customCategories) };
}

export function categoryLabelForUi(categoryName, isEnglish) {
  if (!isEnglish) return categoryName;
  return CATEGORY_LABEL_EN.get(categoryName) || categoryName;
}

export function accountLabelForUi(accountName, isEnglish) {
  if (!isEnglish) return accountName;
  return ACCOUNT_LABEL_EN.get(accountName) || accountName;
}

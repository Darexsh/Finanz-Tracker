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

export function createCategorySuggester(deps) {
  function buildLearnedCategoryModel() {
    const perToken = new Map();
    const totalPerCategory = new Map();

    deps.userBookings().forEach(entry => {
      const category = deps.normalizeCategory(entry.category, deps.customCategories());
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

  return function suggestCategory(description) {
    const learned = suggestCategoryFromHistory(description);
    if (learned) return deps.normalizeCategory(learned, deps.customCategories());

    const text = normalizeLearningText(description);
    const hit = deps.keywordMap.find(([k]) => text.includes(k));
    return hit ? deps.normalizeCategory(hit[1], deps.customCategories()) : null;
  };
}

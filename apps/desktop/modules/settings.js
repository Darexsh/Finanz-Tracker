import { setSelectOptionTexts } from "./ui.js";

export const ROOT_FONT_SIZES = Object.freeze({
  normal: "16px",
  large: "18px",
  xlarge: "20px",
  xxlarge: "22px"
});

export function applyDesktopFontSize(root, fontSizeMode) {
  if (!root) return;
  root.style.fontSize = ROOT_FONT_SIZES[fontSizeMode] || ROOT_FONT_SIZES.normal;
}

export function formatDateFormatForUi(format, isEnglish) {
  if (format === "YYYY-MM-DD") return isEnglish ? "YYYY-MM-DD" : "JJJJ-MM-TT";
  if (format === "MM/DD/YYYY") return isEnglish ? "MM/DD/YYYY" : "MM/TT/JJJJ";
  return isEnglish ? "DD.MM.YYYY" : "TT.MM.JJJJ";
}

export function applySettingsOptionLabels({ el, t, isEnglish }) {
  setSelectOptionTexts(el.settingsSortDirection, [t("newestFirst"), t("oldestFirst")]);
  setSelectOptionTexts(el.settingsStartTab, [
    t("dashboard"),
    t("bookings"),
    t("reports"),
    t("sync"),
    t("settings"),
    t("about")
  ]);
  setSelectOptionTexts(el.settingsCategorySuggestions, [t("active"), t("inactive")]);
  setSelectOptionTexts(el.settingsKeepDateAfterSave, [t("active"), t("inactive")]);
  setSelectOptionTexts(el.settingsNavigationAnimationStyle, [
    "Slide",
    "Fade",
    "Zoom",
    "Pop",
    "Rotate",
    isEnglish ? "None" : "Keine"
  ]);
  setSelectOptionTexts(el.settingsFontSize, [t("normal"), t("large"), t("xlarge"), t("xxlarge")]);
}


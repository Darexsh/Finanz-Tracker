export function setSelectOptionTexts(selectEl, labels) {
  if (!selectEl || !Array.isArray(labels)) return;
  const max = Math.min(selectEl.options.length, labels.length);
  for (let i = 0; i < max; i += 1) {
    selectEl.options[i].textContent = labels[i];
  }
}


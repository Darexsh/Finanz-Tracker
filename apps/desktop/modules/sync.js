export function normalizeSyncFolderPath(value) {
  return String(value || "").trim();
}

export function isSyncConfiguredPath(value) {
  return normalizeSyncFolderPath(value).length > 0;
}


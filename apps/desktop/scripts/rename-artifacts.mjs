import { promises as fs } from "node:fs";
import path from "node:path";

const root = process.cwd();
const releaseDir = path.join(root, "src-tauri", "target", "release");
const nsisDir = path.join(releaseDir, "bundle", "nsis");

async function fileExists(filePath) {
  try {
    await fs.access(filePath);
    return true;
  } catch {
    return false;
  }
}

async function newestExeInDir(dir) {
  const items = await fs.readdir(dir, { withFileTypes: true });
  const exes = items
    .filter(entry => entry.isFile() && entry.name.toLowerCase().endsWith(".exe"))
    .map(entry => path.join(dir, entry.name));

  if (!exes.length) return null;

  let newestPath = exes[0];
  let newestMtime = (await fs.stat(newestPath)).mtimeMs;
  for (const exePath of exes.slice(1)) {
    const mtime = (await fs.stat(exePath)).mtimeMs;
    if (mtime > newestMtime) {
      newestMtime = mtime;
      newestPath = exePath;
    }
  }
  return newestPath;
}

async function newestNsisInstaller(dir) {
  const items = await fs.readdir(dir, { withFileTypes: true });
  const exes = items
    .filter(entry => entry.isFile() && entry.name.toLowerCase().endsWith(".exe"))
    .map(entry => path.join(dir, entry.name));

  if (!exes.length) return null;

  let newestPath = exes[0];
  let newestMtime = (await fs.stat(newestPath)).mtimeMs;
  for (const exePath of exes.slice(1)) {
    const mtime = (await fs.stat(exePath)).mtimeMs;
    if (mtime > newestMtime) {
      newestMtime = mtime;
      newestPath = exePath;
    }
  }
  return newestPath;
}

async function main() {
  if (!(await fileExists(releaseDir))) {
    throw new Error(`Release-Ordner nicht gefunden: ${releaseDir}`);
  }
  if (!(await fileExists(nsisDir))) {
    throw new Error(`NSIS-Ordner nicht gefunden: ${nsisDir}`);
  }

  const portableSource = await newestExeInDir(releaseDir);
  if (!portableSource) {
    throw new Error(`Keine portable .exe gefunden in: ${releaseDir}`);
  }
  const portableTarget = path.join(releaseDir, "FinanzTracker_portable.exe");
  await fs.copyFile(portableSource, portableTarget);

  const installerSource = await newestNsisInstaller(nsisDir);
  if (!installerSource) {
    throw new Error(`Kein NSIS-Installer (.exe) gefunden in: ${nsisDir}`);
  }
  const installerTarget = path.join(nsisDir, "FinanzTracker_installer.exe");
  await fs.copyFile(installerSource, installerTarget);

  console.log("Artefakte kopiert:");
  console.log(`- Portable:  ${portableTarget}`);
  console.log(`- Installer: ${installerTarget}`);
}

main().catch(err => {
  console.error(err instanceof Error ? err.message : String(err));
  process.exit(1);
});

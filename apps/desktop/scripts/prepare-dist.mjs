import fs from "node:fs";
import path from "node:path";

const root = process.cwd();
const dist = path.join(root, "web-dist");
const vendorDir = path.join(root, "vendor");

fs.rmSync(dist, { recursive: true, force: true });
fs.mkdirSync(dist, { recursive: true });
fs.mkdirSync(vendorDir, { recursive: true });

const vendorFiles = [
  ["node_modules/xlsx/dist/xlsx.full.min.js", "vendor/xlsx.full.min.js"],
  ["node_modules/jspdf/dist/jspdf.umd.min.js", "vendor/jspdf.umd.min.js"],
  ["node_modules/jspdf-autotable/dist/jspdf.plugin.autotable.min.js", "vendor/jspdf.plugin.autotable.min.js"]
];

for (const [srcRel, outRel] of vendorFiles) {
  const src = path.join(root, srcRel);
  const out = path.join(root, outRel);
  if (!fs.existsSync(src)) {
    throw new Error(`Fehlende Vendor-Datei: ${srcRel}. Bitte npm install ausführen.`);
  }
  fs.mkdirSync(path.dirname(out), { recursive: true });
  fs.copyFileSync(src, out);
}

const files = ["index.html", "styles.css", "app.js"];
for (const file of files) {
  fs.copyFileSync(path.join(root, file), path.join(dist, file));
}

const distVendor = path.join(dist, "vendor");
fs.mkdirSync(distVendor, { recursive: true });
for (const [, outRel] of vendorFiles) {
  const src = path.join(root, outRel);
  const dest = path.join(dist, outRel);
  fs.mkdirSync(path.dirname(dest), { recursive: true });
  fs.copyFileSync(src, dest);
}

console.log("Prepared web-dist");

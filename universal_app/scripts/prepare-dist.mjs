import fs from "node:fs";
import path from "node:path";

const root = process.cwd();
const dist = path.join(root, "web-dist");

fs.rmSync(dist, { recursive: true, force: true });
fs.mkdirSync(dist, { recursive: true });

const files = ["index.html", "styles.css", "app.js"];
for (const file of files) {
  fs.copyFileSync(path.join(root, file), path.join(dist, file));
}

console.log("Prepared web-dist");

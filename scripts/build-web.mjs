import { cp, mkdir, rm, stat } from "node:fs/promises";
import { join } from "node:path";
import { fileURLToPath } from "node:url";

const projectRoot = join(fileURLToPath(new URL(".", import.meta.url)), "..");
const outputDir = join(projectRoot, "dist");
const siteFiles = ["index.html", "styles.css", "app.js", "manifest.webmanifest", "sw.js"];

await rm(outputDir, { recursive: true, force: true });
await mkdir(outputDir, { recursive: true });

for (const file of siteFiles) {
  const source = join(projectRoot, file);
  await stat(source);
  await cp(source, join(outputDir, file));
}

console.log(`Dayframe web built to ${outputDir}`);

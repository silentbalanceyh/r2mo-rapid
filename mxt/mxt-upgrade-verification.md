# MXT Upgrade Verification

> Single-purpose verification checklist for MXT after framework or rule upgrades.

## 1. File Existence Check

Confirm required entry docs and route docs still exist.

## 2. Entry-Link Integrity Check

Start from `README.md` and confirm linked high-priority docs still exist and still fit their stated responsibility.

## 3. Module Consistency Check

Compare root `pom.xml` with:

- `framework-map.md`
- route documents
- module boundary documents

## 4. SPI Coverage Check

Search for:

- `SPI.findOne`
- `SPI.findOneOf`
- `SPI.findMany`
- `@SPID`

Then compare those results with `extension-points.md`.

## 5. Retrieval-Path Check

Confirm the shortest MCP path still works:

```text
fast start -> one route -> one boundary/guide -> exact source
```

## 6. Final Rule

If code and docs disagree, code wins and MXT must be updated.

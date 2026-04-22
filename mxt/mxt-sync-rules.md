# MXT Sync Rules

> Single-purpose rules for synchronizing `mxt/` docs after framework changes.

## 1. Abstraction Rule Changes

When abstraction rules change, update in this order:

1. `abstraction-rules.md`
2. `framework-map.md`
3. `README.md`
4. `search-hints.md`

## 2. Entry-Level Document Sync

These entry docs must stay aligned with repository reality:

- `README.md`
- `framework-map.md`
- `search-hints.md`

## 3. Sync Checks

After framework upgrades, confirm:

- root `pom.xml` still matches the documented module backbone,
- route files still point to live documents,
- broad entry docs still reflect the shortest reading path,
- SPI coverage is still reflected in `extension-points.md`.

## 4. Forbidden Behavior

- updating one entry doc while leaving the others stale,
- hiding architecture changes only inside subsystem guides,
- changing routing without updating the fast-entry docs.

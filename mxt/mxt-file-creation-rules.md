# MXT File Creation Rules

> Single-purpose governance rules for adding new `mxt/*.md` files.

## 1. When to Add a New File

Create a new standalone file only when one of these is true:

1. a new module layer or layering concept appears,
2. a new extension or routing mechanism appears,
3. cross-document repetition becomes large enough to cause drift,
4. a new container/runtime family appears.

## 2. Naming Rules

- use lowercase kebab-case,
- describe responsibility directly,
- do not encode versions in filenames,
- use specialization-friendly names when splitting from a parent topic.

## 3. Stable Baseline Files

These filenames are baseline anchors and should not be renamed casually:

- `README.md`
- `framework-map.md`
- `abstraction-rules.md`
- `extension-points.md`
- `spring-layer-map.md`
- `spec-boundary.md`
- `dual-side-development.md`
- `search-hints.md`

## 4. Final Rule

Add a new file only when updating an existing file would make that existing file answer more than one main question.

# Evolution Rules

This file explains **how to maintain the `mxt/` document set when the `r2mo-rapid` framework evolves in structure or capability**.

## 1. When to Add a New Markdown File

Create a new standalone Markdown file only when one of the following is true:

1. **A new module layer or new layering concept appears**
   - Example: a new `r2mo-extension-*` layer appears, or `r2mo-spec-v2` introduces a new contract dimension that cannot be cleanly absorbed into existing documents.
2. **A new extension mechanism appears**
   - Example: a second registration mechanism is introduced in addition to SPI, requiring either a new document such as `extension-points-runtime.md` or a meaningful split.
3. **Cross-document repetition exceeds practical limits**
   - If the same concept is repeated across `framework-map`, `spring-layer-map`, and `dual-side-development` and starts drifting into inconsistency, extract it into a dedicated document and have the others point to it.
4. **A new runtime or container is introduced**
   - Example: if R2MO begins supporting a third container, such as Quarkus, a dedicated map like `quarkus-layer-map.md` becomes justified.

### Naming Rules

- Use lowercase kebab-case for all filenames.
- Filenames should directly describe responsibility.
- Do not encode versions in filenames; versioning belongs to git history.
- If a file is a specialization of an existing document, use `parent-topic.md`, for example: `extension-points-spi.md`.
- The baseline filenames are stable and should not be renamed casually:
  - `README.md`
  - `framework-map.md`
  - `abstraction-rules.md`
  - `extension-points.md`
  - `spring-layer-map.md`
  - `spec-boundary.md`
  - `dual-side-development.md`
  - `search-hints.md`
  - `evolution-rules.md`

## 2. When to Update Existing Markdown Instead of Adding New Files

Prefer updating an existing file when one of the following is true:

1. **Only the module list changes**
   - Example: adding `r2mo-spring-security-passkey` should usually update `framework-map.md` and `spring-layer-map.md` instead of creating a new top-level document.
2. **SPI or extension points gain another instance of an existing pattern**
   - Extend the relevant section in `extension-points.md`.
3. **The existing document remains structurally correct and only needs more coverage**
   - Append or revise the correct section instead of replacing the document.
4. **Existing decision rules are only slightly adjusted**
   - Update the relevant clauses in `abstraction-rules.md` or `spec-boundary.md` directly.

## 3. How to Synchronize Abstraction Rule Changes

Changes in abstraction rules are the highest-priority documentation synchronization case.

### Required Update Sequence

1. **Update `abstraction-rules.md` first**
   - Make the new admission or business-retention rule explicit.
2. **Then inspect `framework-map.md`**
   - If the new rule changes how the architecture should be read, update the layer explanations.
3. **Then inspect `README.md`**
   - Its core decision section must remain aligned with `abstraction-rules.md`.
4. **Then inspect `search-hints.md`**
   - If a new abstraction, module family, or keyword becomes important, update the search guidance.
5. **Then verify the logic chain**
   - Read through `README.md` -> `framework-map.md` -> `abstraction-rules.md` to ensure they still form a coherent entry path.

### Forbidden Behavior

- Updating `abstraction-rules.md` without synchronizing `README.md` and `framework-map.md`.
- Hiding an abstraction rule change inside `extension-points.md`.

## 4. How README / framework-map / search-hints Must Stay in Sync

These three documents are entry-level documents and must remain aligned with the real repository structure.

### `README.md` Maintenance Checklist

- The reading list includes every current `mxt/*.md` entry file.
- The module backbone still matches the root `pom.xml`.
- The reading order still reflects the current navigation path.
- Definitions for `Cc`, `Fn`, `DBE`, `HFS`, `RFS`, `HED`, and `SPI` are still accurate.
- The dual-container model description is still accurate.
- The introduction still clearly states that MXT is an AI-first knowledge pack.

### `framework-map.md` Maintenance Checklist

- The four-layer explanation still matches the root `pom.xml` and current repository reality.
- The Spring / Vert.x main lines still match the actual dependency structure.
- Module additions or removals are reflected promptly.
- The positioning of `Cc`, `Fn`, `DBE`, and `SPI` remains accurate.

### `search-hints.md` Maintenance Checklist

- Module names in topical searches still match the repository.
- Search keywords still reflect real SPI usage and module naming.
- Bootstrap dependency-chain guidance still matches the current `pom.xml` files.
- Recommended reading paths still point to real files.

## 5. How Agents Should Verify Correct Targeting After Upgrades

Whenever the framework structure changes, agents should run a lightweight verification workflow.

### 5.1 File Existence Check

Use a simple directory listing such as:

```bash
ls -la /Users/lang/zero-cloud/app-zero/r2mo-rapid/mxt/
```

Confirm that the baseline nine files still exist.

### 5.2 Entry-Link Integrity Check

Start from `README.md` and confirm that every linked file still exists and remains relevant.

### 5.3 Module Consistency Check

Compare the root `pom.xml` module list against `framework-map.md` and confirm that:

- Abstraction-layer modules are still correctly grouped.
- Spring-side modules are still correctly grouped.
- Vert.x-side modules are still correctly grouped.
- Boot-layer modules are still correctly grouped.

### 5.4 SPI Coverage Check

Search for `SPI.findOne`, `SPI.findOneOf`, `SPI.findMany`, and `@SPID`, then compare those results with `extension-points.md`:

- Newly introduced SPI usage should be reflected.
- Removed or obsolete SPI usage should be cleaned up from the document.

### 5.5 Abstraction Rule Consistency Check

Re-read the key sections of `abstraction-rules.md` and verify that:

- Admission rules still match the actual framework layering.
- Business-retention rules still match the current project boundary philosophy.

## 6. Change Log Convention

Whenever MXT documents are updated, add or refresh a small change log section at the bottom of the affected file when the change is structurally meaningful.

Recommended format:

```markdown
## Change Log

| Date | File | Change Type | Summary | Trigger |
|------|------|-------------|---------|---------|
| 2026-03-27 | evolution-rules.md | add | Created the evolution maintenance guide | New baseline requirement |
```

Suggested change types:

- add
- update
- delete
- refactor

Suggested triggers:

- framework upgrade
- new module family
- abstraction rule change
- documentation review feedback

## 7. Final Rule

**MXT documents are a mirror of the framework, not an independent artifact.**

Whenever the framework structure changes, MXT must be updated.
Whenever MXT is updated, it must be checked against the real codebase.
If documents and code disagree, the code is authoritative and the documentation must catch up.

## 8. Single-Responsibility Rule

Each `mxt/*.md` file should answer one primary question for one main audience.

Allowed exception:

- `README.md` may remain a directory index and entry document.

Split a document when one or more of the following is true:

1. it mixes multiple technology stacks that can be read independently,
2. it mixes framework guidance and app-specific addenda in a way that changes audience mid-file,
3. it mixes operational routing, subsystem detail, and maintenance policy in one place,
4. readers regularly need only one half of the file.

Good split examples:

- route index -> multiple `mcp-route-*.md` files
- frontend multi-stack guide -> one file per stack plus optional design-system addendum
- architecture map -> architecture map plus separate runtime-contract guide when needed

---

## Change Log

| Date | File | Change Type | Summary | Trigger |
|------|------|-------------|---------|---------|
| 2026-03-27 | evolution-rules.md | add | Created the MXT evolution maintenance guide | New baseline requirement |
| 2026-03-27 | evolution-rules.md | update | Rewrote the document in English for AI-agent readability | AI-first English knowledge pack requirement |
| 2026-04-21 | evolution-rules.md | update | Added the MXT single-responsibility rule for document splitting | Documentation structure review |

# Project Rule Awareness (Harness 3.0)

This document defines how AI agents should treat project-local rule files (MDC, `.mdc`, `.trae`, and equivalent) when working in a framework-based project.

---

## Core Principle

Project-local rule files are an **optional, project-scoped rule layer**.
They refine agent behavior for a specific project.
They do not override framework fundamentals.
They must never be promoted into framework-level knowledge.

---

## MDC Awareness Rules

### Rule 1 — Inspect if present, ignore if absent

If project-local rule files exist (`.cursor/rules/*.mdc`, `.trae/rules/*.md`, or equivalent), inspect them before starting implementation.

If no rule files exist, ignore this layer entirely and continue with:
- `mxt/` framework docs
- source code
- `.r2mo` specs
- project `CLAUDE.md` / `AGENTS.md`

Do **not** block, loop, or ask the user about missing MDC files.

---

### Rule 2 — `devapi.mdc` is the highest-priority backend rule file

If `devapi.mdc` (or equivalent API execution rule) exists, read it first before:
- implementing backend APIs
- validating request / response contracts
- extracting data structures
- deciding schema / DTO / domain mapping
- checking interface behavior

It defines the project's local method for API inspection and data structure extraction.
Its presence changes how backend development proceeds.
Its absence means: continue with `.r2mo`, framework docs, and source code.

---

### Rule 3 — Extract only what is reusable within that project

From any MDC file, extract only:
- directory or module boundaries
- layering and naming conventions
- allowed and forbidden modification zones
- frontend / backend / integration workflow constraints
- tooling or generation rules

Do **not** promote project-specific naming, entity names, module IDs, or business terms into framework-level guidance.

---

### Rule 4 — MDC files are not cross-project portable

Different projects may have different MDC rule sets, even if they use the same framework.
An MDC rule valid in Project A may be wrong or irrelevant in Project B.
Always treat MDC as scoped to the current project, not as universal truth.

---

### Rule 5 — MDC refines, framework guides

Framework `mxt/` docs define the structural and semantic rules.
MDC files define project-local execution preferences.
When they conflict, prefer framework rules unless the MDC explicitly overrides a specific behavior for a documented reason.

---

## Agent Execution Flow (Harness 3.0)

```text
1. Detect stack      → R2MO-first or Zero-first (from root pom.xml or framework BOM)
2. Read mxt/         → framework-map, abstraction-rules, extension-points, evolution-rules
3. Check MDC layer   → if exists: inspect devapi.mdc first, then other relevant mdc files
4. Extract project constraints from MDC (if present)
5. Read .r2mo specs  → operations, schemas, proto, domain contracts
6. Proceed with implementation using framework + project constraints
7. If MDC absent at step 3: skip directly to step 5
```

---

## What MDC Can and Cannot Do

| MDC CAN do | MDC CANNOT do |
|---|---|
| Constrain directory usage | Override DPA dependency direction |
| Define naming conventions | Change how framework modules are loaded |
| Specify tooling workflow | Replace framework evolution rules |
| Define API inspection method | Promote project entities into framework |
| Refine frontend patterns | Make project-specific rules apply cross-project |
| Set import/export conventions | Override env/runtime contract rules |

---

## Summary

> MDC is a lens, not a replacement.
> Use it to see the project more clearly.
> Don't let it blind you to the framework.

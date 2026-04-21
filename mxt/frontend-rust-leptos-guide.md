# Frontend Rust Leptos Guide

> Final guide for the Rust / Leptos / Tauri frontend stack used in the R2MO ecosystem.

## 1. Scope

Use this file for:

- Leptos frontend work,
- Tauri desktop frontend work,
- Rust page/module structure,
- contract-first Rust UI implementation.

Do not use this file for:

- React / Ant Design admin work,
- app-specific design tokens,
- or Rust-admin app-shell addenda.

## 2. Stack Identity

This stack is characterized by:

- Leptos `0.8` with CSR,
- `leptos_router 0.8`,
- Tauri desktop integration,
- Tailwind CSS v4,
- Trunk for WASM build,
- `gloo-net`, `serde`, `serde_yaml`, and `web-sys`.

## 3. Source-of-Truth Order

Read these before coding:

1. `src/pages/{module}/requirement.module.md`
2. `src/pages/{module}/{page}/requirement.page.md`
3. `src/pages/{module}/metadata.yaml`
4. `src/pages/{module}/{page}/page.yaml`
5. `.r2mo/api/**`
6. `.r2mo/domain/**`
7. `.r2mo/design/spec.md` and `spec-page.md`

If spec data is missing, mark `TBD`.
Do not invent endpoints, models, or UI rules.

## 4. UI Structure

```text
{app}-ui/
├── .r2mo/
│   ├── api/
│   ├── design/
│   ├── domain/
│   └── requirements/
├── src/
│   ├── pages/
│   │   └── {module}/
│   │       ├── mod.rs
│   │       ├── view.rs
│   │       ├── metadata.yaml
│   │       ├── menu.yaml
│   │       ├── requirement.module.md
│   │       └── {page}/
│   │           ├── view.rs
│   │           ├── page.yaml
│   │           └── requirement.page.md
│   ├── components/
│   ├── models/
│   ├── api/
│   ├── utils/
│   └── app.rs
└── src-tauri/
```

## 5. Workflow

1. read requirements and annotations,
2. build model types in `src/models/` from `.r2mo/domain` or `.r2mo/api/components/schemas`,
3. build the API client in `src/api/`,
4. implement page views in `src/pages/{module}/{page}/view.rs`,
5. extract repeated UI into `src/components/`,
6. style with Tailwind using design tokens.

## 6. Coding Rules

- No `unwrap()`, `expect()`, or `panic!()` in UI paths.
- Return `Result<T, AppError>` from API and async paths.
- Use signals for local state.
- Use `create_resource` for async fetching.
- Always implement loading, error, and empty states.
- Keep Tailwind class order stable:
  `layout -> size -> spacing -> type -> color -> state`.

## 7. Build Commands

```bash
trunk serve --port 6100
trunk build --release
cargo tauri dev
cargo tauri build
cargo check
cargo clippy --all-targets --all-features
cargo fmt --all
```

## 8. Cross-Stack Contract Rules

- `.r2mo` is the contract between frontend and backend.
- The UI layer is independent from backend modules, but both sides must match `.r2mo` API and domain specs.
- New page work must start from requirements and specs, not ad-hoc component coding.
- All pages must handle loading, error, and empty states.
- Enterprise B-side defaults to list/detail/form workflows, not marketing-site interaction patterns.

## 9. Final Rule

For this stack, use:

```text
requirements first -> model and API typing -> page implementation -> component extraction -> state-safe async UI
```

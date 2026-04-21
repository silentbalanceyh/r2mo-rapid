# Frontend Rust Admin Addendum

> Final addendum for the Rust-based admin frontend pattern observed in `r2mo-apps-admin`.

## 1. Scope

Use this file only when the task is specifically about the Rust admin implementation pattern found in `r2mo-apps-admin`.

It covers:

- Leptos admin module structure,
- router layering,
- menu-driven navigation,
- layout shell rules,
- Rust admin HTTP organization.

It does not replace:

- `frontend-rust-leptos-guide.md`
- `frontend-admin-design-system.md`

## 2. Domain Module Structure

`src/pages/mod.rs` exports first-level admin domains such as:

- `apps`
- `desktop`
- `home`
- `login`
- `personal`
- `placeholder`
- `product`
- `security`
- `tenant`

Preferred domain layout:

```text
src/pages/{module}/
├── mod.rs
├── menu.yaml
├── metadata.yaml
├── requirement.module.md
└── {page-or-submodule}/
    ├── view.rs
    ├── page.yaml
    └── requirement.page.md
```

## 3. Rust Module Export Rules

Use `mod.rs` to hide file-layout details and export stable page views.

Patterns:

```rust
pub mod apps;
pub mod desktop;
pub mod personal;
pub mod security;
pub mod tenant;
```

For kebab-case directories, map explicitly:

```rust
#[path = "sec-account/view.rs"]
pub mod sec_account;
```

For internal file names that should not leak into route code, re-export public names:

```rust
mod list;
pub use list::TenantListView;
```

Rules:

- external callers should import stable `*View` exports from the module root
- internal file names may stay implementation-specific
- use explicit `#[path = ...]` when filesystem naming and Rust module naming differ

## 4. Router Architecture

Use a two-layer router:

- top-level router in `src/app.rs`
- inner authenticated router in `src/components/layout.rs`

Required split:

- `/` -> login page
- `/*any` -> shared authenticated app shell
- authenticated business routes stay inside the inner layout router

Rules:

- keep auth entry and app shell split at the top router
- keep feature page routes inside the layout component
- add new business pages to the inner route table, not the app root

## 5. Route Registration Rules

A new page is not complete until all three are wired:

1. the page component exists in `src/pages/{module}/.../view.rs`
2. the page is re-exported from the relevant `mod.rs`
3. the route is registered in `src/components/layout.rs`

Rules:

- route path prefixes should match module folder names
- keep route groups clustered by module in the layout route table
- use placeholder pages only as temporary shells, not long-term structure

## 6. Menu-Driven Navigation Rules

Menus are runtime-loaded from YAML, not hardcoded in component logic.

Observed behavior:

- loader fetches `/menus/{module}.yaml`
- YAML entries are merged and split by `type`
- default or empty `type` is `SIDER`
- explicit `TOP` entries feed the top account dropdown
- root and child entries are sorted by `order`
- sidebar and breadcrumb derive from the menu tree

Rules:

- treat `menu.yaml` as the source of truth for labels, icons, URIs, and hierarchy
- do not duplicate menu semantics manually in layout code
- keep `uri` values aligned with actual route paths
- use `text` as the user-facing label and breadcrumb source

## 7. Sidebar and Header Layout Pattern

The shared shell should contain:

- left collapsible sidebar
- top header with breadcrumb and user menu
- main content area with inner scroll container
- route outlet inside the content card area

Rules:

- preserve one shared app shell for all authenticated pages
- page components should render business content only
- shell chrome belongs to layout
- breadcrumb should derive from menu metadata, not page-local hardcoding

## 8. HTTP Client Pattern

Centralize HTTP helpers in `src/utils/http.rs`.

Rules:

- expose typed helper methods for `get`, `get_text`, `post`, `post_empty`, `post_void`, `put`, `delete`
- keep raw request composition in one utility layer
- page code should call typed API modules, not build fetch logic inline

## 9. Naming and Composition Rules

- group pages by business domain such as `apps`, `desktop`, `security`, `tenant`, `personal`
- keep each domain’s exports in its own `mod.rs`
- use `*View` suffix for public page components
- use placeholder pages for route scaffolding only when the page is intentionally not implemented yet

## 10. Final Checklist

When generating a new admin page for this stack:

- choose the Rust admin stack explicitly
- read `.r2mo` requirements, API, domain, and design files first
- place the page under the correct domain module
- expose the page from `mod.rs`
- register the route in the shared layout router
- add or update `menu.yaml`
- centralize transport logic in API or HTTP helpers
- cover loading, error, and empty states

## 11. Final Rule

For this stack, use:

```text
domain module -> stable export -> layout route -> menu metadata -> typed HTTP helper
```

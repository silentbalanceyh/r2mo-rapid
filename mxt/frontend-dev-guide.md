# Frontend Development Guide (Harness 2.0)

Extracted from R2MO frontend rule sets and app-takeout-specific rules.
Scope: enterprise admin B-side frontends first; includes React/AntD admin patterns and Rust/Leptos+Tauri desktop patterns.

---

## 1. Two Frontend Stacks

| Dimension | React / Ant Design stack | Rust / Leptos + Tauri stack |
|---|---|---|
| Target | Web SPA (browser) | Desktop (Tauri) + WASM web |
| Language | JavaScript / TypeScript | Rust |
| UI kit | Ant Design-based framework wrappers | Tailwind CSS v4 |
| State | Redux + `Ux` / `Dsl` wrappers | Leptos signals + `create_resource` |
| Build | Webpack/Babel + route generation | Trunk + Tauri |
| Routing | Generated routes, admin-first | `main.rs -> app.rs`, Leptos Router |
| Style | SCSS modules + skin system | Tailwind utility classes |
| i18n | `src/cab/**` JSON resources | spec-driven, not yet formalized |

---

## 2. React / AntD Enterprise Admin Rules

### 2.1 Source Layout

```text
src/
├── components/                    # app pages like login/main
│   └── {page}/
│       ├── Cab.json
│       ├── Cab.module.scss
│       ├── Op.js
│       └── UI.js
├── extension/
│   ├── components/                # business pages
│   │   └── {domain}/{page}/
│   │       ├── Cab.json
│   │       ├── Cab.module.scss
│   │       ├── Op.js
│   │       ├── UI.js
│   │       └── form/
│   │           ├── UI.Add.js
│   │           ├── UI.Edit.js
│   │           └── UI.Filter.js
│   ├── cerebration/               # dev-center pages
│   ├── ecosystem/                 # reusable Ex*/Tx*/G2* components
│   └── library/
├── economy/                       # standard components
├── container/                     # layout containers
├── cab/                           # i18n resources
├── skin/                          # theming/skin API
├── ux/                            # core API
├── ex/                            # extension API
└── zone/                          # core utilities
```

### 2.2 Component Tier System

| Tier | Path | CSS Prefix | Use |
|---|---|---|---|
| `web` | `src/economy/` | `uni_` | standard form/table primitives |
| `ei` | `src/extension/ecosystem/` | `uex_` | reusable business components |
| `oi` | `src/extension/eclat/` | `uox_` | config-driven components |
| `uca` | `src/extension/components/` | `uca_` | feature pages |
| `my` | custom | `umy_` | app-specific components |

### 2.3 Mandatory Page Pattern

- Pages use `React.PureComponent`, not function components.
- Pages are decorated with `@Ux.zero(Ux.rxEtat(...).to())`.
- `render()` must be gated by `Ex.yoRender(this, fn, debug)` so the page waits for `$ready`.
- Initialization goes through `Ex.yiStandard(...)` or `Ex.yiAssist(...)`.

Example shape:

```js
@Ux.zero(Ux.rxEtat(require('./Cab'))
    .cab('UI')
    .to()
)
class Component extends React.PureComponent {
    componentDidMount() {
        Op.yiPage(this);
    }
    render() {
        return Ex.yoRender(this, () => <div />);
    }
}
```

### 2.4 Standard File Roles

| File | Role |
|---|---|
| `Cab.json` | page config, namespace, `_assist` mappings |
| `Cab.module.scss` | page-local styles |
| `Op.js` | event handlers / operations |
| `UI.js` | main page component |
| `UI.Add.js` / `UI.Edit.js` / `UI.Filter.js` | form variants |

### 2.5 CRUD and Form Patterns

#### ExList family

| Component | Use |
|---|---|
| `ExListFast` | quick list with search/pagination/actions |
| `ExListComplex` | full CRUD list with Add/Edit/Filter forms |

`ExListComplex` pattern:

```jsx
<ExListComplex {...Ex.yoAmbient(this)}
    config={Ux.fromHoc(this, 'grid')}
    $form={{ FormAdd, FormEdit, FormFilter }}
    $query={$query}
/>
```

#### ExForm modes

| Mode | Call |
|---|---|
| ADD | `Ex.yoForm(this, null)` |
| EDIT | `Ex.yoForm(this, null, $inited)` |
| FILTER | `Ex.yoFilter(this)` |

Filter decorators typically use `.raft(1).form()`.

### 2.6 Operation Handler Rules

- `Op.js` exports handlers whose keys must start with `$op`.
- Form submit/delete/filter uses `Ex.form(reference).add/save/remove/filter(...)`.
- Post-operation Redux sync can chain `.then(...)`.

Example:

```js
const $opSave = (reference) => (params) =>
    Ex.form(reference).save(params, {
        uri: '/api/resource/:key',
        dialog: 'saved'
    });
```

### 2.7 State Management Rules

- Do not store raw objects/arrays in Redux state.
- Wrap arrays/objects with `Dsl.getArray(...)` / `Dsl.getObject(...)`.
- `$t_xxx` = tabular/dictionary data.
- `$a_xxx` = auxiliary/component data.
- Read via `Ux.onDatum(reference, 'key')`.
- Use `Ex.yiAssist(reference)` to load `_assist` data declared in `Cab.json`.

### 2.8 HTTP / Ajax Rules

Preferred APIs:

- Framework APIs via `Ex.I.*`
- Custom HTTP via `Ux.ajaxGet/Post/Put/Delete`

Important rules:

- `Ux.ajax*` auto-extracts `response.data`
- Path params use `:param` style
- Handle `401` with `Ux.toUnauthorized(reference)`
- `404` can mean feature not enabled
- Parallel requests use `Ux.parallel(...)`

### 2.9 i18n Rules

- All user-facing strings come from `src/cab/**` JSON resources.
- Read text with `Ux.fromHoc(this, 'key')`.
- Do not hardcode display strings.

### 2.10 Environment Rules

All custom frontend env vars use `Z_` prefix.

Common keys:

| Variable | Purpose |
|---|---|
| `Z_ENDPOINT` | API base URL |
| `Z_APP` | app ID |
| `Z_LANGUAGE` | default language |
| `Z_ROUTE` | route prefix |
| `Z_K_SESSION` | session key prefix |
| `Z_CSS_COLOR` | primary color token |

### 2.11 Never Hand-Edit Generated Files

```text
src/container/index.js
src/components/index.js
src/extension/components/index.js
src/extension/cerebration/index.js
src/environment/routes.js
src/environment/datum.js
```

Route changes belong in route-generation scripts, not generated output.

### 2.12 Critical Anti-Patterns

| Anti-pattern | Why it is wrong |
|---|---|
| function components/hooks for pages | framework decorators depend on class components |
| raw objects in Redux | causes silent mutation/state bugs |
| skipping `$ready` gate | renders before async init completes |
| missing `$op` prefix | `RxEtat.bind()` may ignore handlers |
| hardcoded strings | breaks i18n contract |
| hand-editing generated files | changes get overwritten on next build |
| wrong `Sk.mix*` prefix | tiered CSS naming collisions |

---

## 3. Rust / Leptos + Tauri Rules

### 3.1 Stack

From `app-takeout-ui/Cargo.toml` and rules:

- Leptos `0.8` with CSR
- `leptos_router 0.8`
- Tauri desktop integration
- Tailwind CSS v4
- Trunk for WASM build
- `gloo-net`, `serde`, `serde_yaml`, `web-sys`

### 3.2 Source-of-Truth Order

Read these before coding:

1. `src/pages/{module}/requirement.module.md`
2. `src/pages/{module}/{page}/requirement.page.md`
3. `src/pages/{module}/metadata.yaml`
4. `src/pages/{module}/{page}/page.yaml`
5. `.r2mo/api/**`
6. `.r2mo/domain/**`
7. `.r2mo/design/spec.md` and `spec-page.md`

If spec data is missing, mark `TBD`; do not invent endpoints, models, or UI rules.

### 3.3 Rust UI Structure

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

### 3.4 Rust Workflow

1. Read requirements and annotations.
2. Build model types in `src/models/` from `.r2mo/domain` or `.r2mo/api/components/schemas`.
3. Build API client in `src/api/`.
4. Implement page view in `src/pages/{module}/{page}/view.rs`.
5. Extract repeated UI into `src/components/`.
6. Style with Tailwind using design tokens.

### 3.5 Rust Coding Rules

- No `unwrap()`, `expect()`, or `panic!()` in UI paths.
- Return `Result<T, AppError>` from API and async paths.
- Use signals for local state.
- Use `create_resource` for async fetching.
- Always implement loading/error/empty states.
- Keep Tailwind class order stable: layout -> size -> spacing -> type -> color -> state.

### 3.6 Rust Build Commands

```bash
trunk serve --port 6100
trunk build --release
cargo tauri dev
cargo tauri build
cargo check
cargo clippy --all-targets --all-features
cargo fmt --all
```

---

## 4. app-takeout Design System Additions (Rust desktop/admin UI)

These are app-specific rules extracted from `r2-frontend-design-system.mdc`.

### 4.1 Brand Colors

#### Amber admin theme

- primary background: `bg-amber-600` / `bg-amber-700`
- focus border: `focus:border-amber-500`
- active background: `bg-amber-100 dark:bg-amber-500/15`
- primary text: `text-amber-700 dark:text-amber-500`

#### Orange-red login theme

- gradient button/title: `from-orange-500 to-red-500`
- hover: `hover:from-orange-600 hover:to-red-600`

### 4.2 Neutral Palette

- page background: `bg-gray-50 dark:bg-gray-950`
- card background: `bg-white dark:bg-gray-900`
- input background: `bg-white dark:bg-gray-800`
- border: `border-gray-200 dark:border-gray-800`
- text primary: `text-gray-900 dark:text-gray-100`
- helper text: `text-gray-500 dark:text-gray-400`

### 4.3 Typography

| Usage | Class |
|---|---|
| input / placeholder | `text-[14px] placeholder:text-[14px]` |
| label | `text-sm text-gray-600 dark:text-gray-400` |
| page title | `text-lg font-bold text-gray-800 dark:text-gray-100` |
| login title | `text-3xl font-bold` |
| helper text | `text-xs text-gray-500 dark:text-gray-400` |

### 4.4 Spacing and Sizes

| Usage | Class |
|---|---|
| page padding | `p-6` |
| card padding | `p-8` |
| row spacing | `space-y-4` |
| form gap | `gap-8` |
| input/button height | `h-[42px]` |
| header height | `h-16` |
| sidebar width | `w-64` expanded / `w-16` collapsed |

### 4.5 Standard Form Layout

- standard form grid: `grid grid-cols-2 gap-8`
- single-line row: `flex items-center`
- multi-line row: `flex items-start`
- button row: `flex justify-center gap-4`
- two-column label: `w-1/4 text-right pr-4 shrink-0`

### 4.6 Standard Control Classes

#### Standard input

```text
h-[42px] px-3 bg-white dark:bg-gray-800 border border-gray-300 dark:border-gray-700 rounded text-[14px] text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-0 focus:shadow-none focus:border-amber-500 dark:focus:border-amber-500 transition-colors shadow-none appearance-none placeholder:text-[14px]
```

#### Standard textarea

```text
px-3 py-2 bg-white dark:bg-gray-800 border border-gray-300 dark:border-gray-700 rounded text-[14px] text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-0 focus:shadow-none focus:border-amber-500 dark:focus:border-amber-500 transition-colors shadow-none appearance-none resize-none placeholder:text-[14px]
```

#### Inline verify/bind button

```text
h-[42px] px-4 bg-white hover:bg-amber-50 dark:bg-gray-800 dark:hover:bg-amber-900/20 border border-amber-600 dark:border-amber-500 text-amber-700 dark:text-amber-500 text-sm rounded transition-colors whitespace-nowrap flex items-center
```

#### Primary action button

```text
px-8 py-2 bg-amber-600 hover:bg-amber-700 disabled:bg-gray-400 disabled:cursor-not-allowed text-white text-sm rounded transition-colors
```

#### Secondary button

```text
px-8 py-2 bg-white hover:bg-gray-50 text-gray-700 border border-gray-300 dark:bg-gray-800 dark:hover:bg-gray-700 dark:text-gray-300 dark:border-gray-600 text-sm rounded transition-colors
```

#### Login button

```text
w-full bg-gradient-to-r from-orange-500 to-red-500 hover:from-orange-600 hover:to-red-600 text-white font-semibold py-3.5 px-4 rounded-md transition-all shadow-lg shadow-orange-500/30
```

### 4.7 Container Patterns

- standard content card:
  `bg-white dark:bg-gray-900 rounded-lg shadow-sm border border-gray-200 dark:border-gray-800 p-8`
- login card:
  `bg-white/80 dark:bg-gray-800/80 backdrop-blur-xl rounded-lg shadow-2xl border border-orange-100 dark:border-gray-700`

### 4.8 Active States

- sidebar active item:
  `bg-amber-100 text-amber-700 font-semibold shadow-sm dark:bg-amber-500/15 dark:text-amber-500`
- submenu active item:
  `text-amber-700 bg-amber-100 font-medium dark:text-amber-500 dark:bg-amber-500/15`

---

## 5. Cross-Stack Rules

1. `.r2mo` is the contract between frontend and backend.
2. UI layer is independent from backend modules, but both sides must match `.r2mo` API/domain specs.
3. New page work should start from requirements/specs, not from ad-hoc component coding.
4. All pages must handle loading, error, and empty states.
5. Enterprise B-end defaults to list/detail/form workflows, not marketing-site interaction patterns.

---

## 6. Evidence

Primary evidence sources used for this guide:

- `/Users/lang/zero-cloud/app-zero/r2mo-apps/app-takeout/CLAUDE.md`
- `/Users/lang/zero-cloud/app-zero/r2mo-apps/app-takeout/app-takeout-ui/CLAUDE.md`
- `/Users/lang/zero-cloud/app-zero/r2mo-apps/app-takeout/.cursor/rules/r2-frontend-design-system.mdc`
- `/Users/lang/zero-cloud/app-zero/r2mo-apps/app-takeout/.r2mo/repo/r2mo-ai/.cursor/rules/r2-frontend-rust.mdc`
- `/Users/lang/zero-cloud/app-zero/r2mo-apps/app-takeout/.r2mo/repo/mxt/.cursor/rules/r2-ui-component-tiers.mdc`
- `/Users/lang/zero-cloud/app-zero/r2mo-apps/app-takeout/.r2mo/repo/mxt/.cursor/rules/r2-ui-exform-patterns.mdc`
- `/Users/lang/zero-cloud/app-zero/r2mo-apps/app-takeout/.r2mo/repo/mxt/.cursor/rules/r2-ui-exlist-patterns.mdc`
- `/Users/lang/zero-cloud/app-zero/r2mo-apps/app-takeout/.r2mo/repo/mxt/.cursor/rules/r2-ui-data-models.mdc`
- `/Users/lang/zero-cloud/app-zero/r2mo-apps/app-takeout/.r2mo/repo/mxt/.cursor/rules/r2-ui-ajax-patterns.mdc`
- `/Users/lang/zero-cloud/app-zero/r2mo-apps/app-takeout/.r2mo/repo/mxt/.cursor/rules/r2-ui-api-reference.mdc`
- `/Users/lang/zero-cloud/app-zero/r2mo-apps/app-takeout/.r2mo/repo/mxt/.cursor/rules/r2-ui-common-pitfalls.mdc`
- `/Users/lang/zero-cloud/app-zero/r2mo-apps/app-takeout/.r2mo/repo/mxt/.cursor/rules/r2-ui-ei-components.mdc`
- `/Users/lang/zero-cloud/app-zero/r2mo-apps/app-takeout/.r2mo/repo/mxt/.cursor/rules/r2-ui-environment.mdc`
- `/Users/lang/zero-cloud/app-zero/r2mo-apps/app-takeout/app-takeout-ui/package.json`
- `/Users/lang/zero-cloud/app-zero/r2mo-apps/app-takeout/app-takeout-ui/Cargo.toml`

---

## 7. r2mo-apps-admin Addendum (Leptos admin implementation)

This addendum captures reusable frontend rules from `app-r2mo/r2mo-apps-admin/r2mo-apps-admin-ui`.

### 7.1 Actual Leptos admin module structure

`src/pages/mod.rs` exports first-level admin domains:
- `apps`
- `desktop`
- `home`
- `login`
- `personal`
- `placeholder`
- `product`
- `security`
- `tenant`

Inside each domain, use a small-module layout:

```text
src/pages/{module}/
├── mod.rs
├── menu.yaml                  # module menu source
├── metadata.yaml              # if module is spec-driven
├── requirement.module.md      # if module is spec-driven
└── {page-or-submodule}/
    ├── view.rs
    ├── page.yaml              # if page is spec-driven
    └── requirement.page.md    # if page is spec-driven
```

Observed examples:
- `apps`: `onboard`, `offboard`, `history`
- `desktop`: `store`, `purchased`, `instance`, `my_license`, `my_subscription`
- `security`: `manage_user`, `manage_role`
- `personal`: kebab-case folders mapped with `#[path = "..."]`
- `tenant`: internal `list` module re-exported as `TenantListView`

### 7.2 Rust module export conventions

Use `mod.rs` to hide file layout details and export page views by domain name.

Patterns seen:

```rust
pub mod apps;
pub mod desktop;
pub mod personal;
pub mod security;
pub mod tenant;
```

For kebab-case directories, map them explicitly:

```rust
#[path = "sec-account/view.rs"]
pub mod sec_account;
```

For internal file names that should not leak into route code, re-export public view names:

```rust
mod list;
pub use list::TenantListView;
```

Rule:
- external callers should import stable `*View` exports from the module root
- internal file names may stay implementation-specific
- use explicit `#[path = ...]` when filesystem naming and Rust module naming differ

### 7.3 Router architecture

The admin Rust frontend uses a two-layer router.

Top-level router in `src/app.rs`:
- `/` → login page
- `/*any` → shared authenticated app shell

Inner router in `src/components/layout.rs`:
- all authenticated feature routes live inside the layout shell
- fallback stays inside the layout content area

Reusable rule:
- keep auth entry and app shell split at the top router
- keep feature page routes inside the layout component
- add new business pages to the inner route table, not the app root

### 7.4 Route registration rules

A new page is not complete until all 3 are wired:
1. page component exists in `src/pages/{module}/.../view.rs`
2. page is re-exported from the relevant `mod.rs`
3. route is registered in `src/components/layout.rs`

Representative route families already present:
- `/desktop/*`
- `/apps/*`
- `/security/*`
- `/product/*`
- `/tenant/*`
- `/personal/*`

Rule:
- route path prefixes should match module folder names
- keep route groups clustered by module in the layout route table
- use placeholder pages only as temporary shells, not as long-term structure

### 7.5 Menu-driven navigation rules

Menus are runtime-loaded from YAML, not hardcoded in component JSX.

Observed behavior from `src/menu.rs`:
- loader fetches `/menus/{module}.yaml` per known module
- YAML entries are merged and split by `type`
- default/empty `type` is `SIDER`
- explicit `TOP` entries feed the top account dropdown
- root and child entries are sorted by `order`
- sidebar and breadcrumb are derived from the menu tree

Reusable rule:
- treat `menu.yaml` as the source of truth for nav labels, icons, URIs, and hierarchy
- do not duplicate menu semantics manually in layout code
- keep `uri` values aligned with actual route paths
- use `text` as the user-facing label and breadcrumb source

### 7.6 Sidebar / header layout pattern

The admin shell in `src/components/layout.rs` follows this reusable structure:
- left collapsible sidebar
- top header with breadcrumb + user menu
- main content area with inner scroll container
- route outlet rendered inside the content card area

Reusable rule:
- preserve one shared app shell for all authenticated pages
- page components should render business content only; shell chrome belongs to layout
- breadcrumb should be derived from menu metadata, not hand-maintained per page

### 7.7 HTTP client pattern in Rust admin UI

The Rust admin UI centralizes HTTP helpers in `src/utils/http.rs`.

Reusable rule:
- expose typed helper methods for `get`, `get_text`, `post`, `post_empty`, `post_void`, `put`, `delete`
- keep raw request composition in one utility layer
- page code should call typed API modules, not build fetch logic inline

### 7.8 Enterprise admin visual rules

From the local frontend MDC rules (`r2-frontend-design-foundation.mdc`, `r2-frontend-admin-list-page.mdc`, `r2-frontend-admin-dialog-form.mdc`):

#### Shared design baseline
- clean enterprise UI
- compact admin density
- blue-first interaction palette
- standard body/form text: `14px`
- default radius: `6px`
- spacing rhythm: `4 / 8 / 12 / 16 / 20 / 24 / 32 / 40`

#### Admin list page blueprint
- outer page: `bg-gray-50`
- content sections in white cards
- top rhythm: header → filter card → stats cards → table card
- filter card uses bordered white card with compact controls
- table card contains bulk toolbar, body, and pagination in one surface
- status/type fields use tinted chips, not plain text
- row actions are compact inline text actions, not large filled buttons

#### Dialog form blueprint
- left-label / right-control rows are the default
- do not stack label above control unless explicitly required
- dialog body needs meaningful horizontal inset
- section titles are taller and more prominent than normal controls
- `备注信息` replaces raw `description` label in Chinese admin UI
- textarea in notes fields should be `resize-none`
- insert a neutral divider below notes before the next section

### 7.9 Naming and page composition rules

Observed page composition prefers domain-first grouping.

Reusable rule:
- group pages by business domain (`apps`, `desktop`, `security`, `tenant`, `personal`)
- keep each domain’s exports in its own `mod.rs`
- use `*View` suffix for public page components
- use placeholder pages for route scaffolding only when the page is intentionally not implemented yet

### 7.10 Practical implementation checklist for Harness 2.0

When generating a new admin frontend page for this stack:
- choose stack first: React/AntD or Rust/Leptos/Tauri
- read `.r2mo` requirements, API, domain, and design files first
- place the page under the correct business domain module
- expose the page from `mod.rs`
- register the route in the shared layout router
- add/update `menu.yaml` instead of hand-coding nav labels
- follow enterprise admin list/dialog blueprints
- centralize transport logic in API/http helpers
- cover loading, error, and empty states

## 8. r2mo-apps-admin Evidence

Primary evidence for this addendum:
- `/Users/lang/zero-cloud/app-zero/r2mo-apps/app-r2mo/r2mo-apps-admin/r2mo-apps-admin-ui/.cursor/rules/r2-frontend-design-foundation.mdc`
- `/Users/lang/zero-cloud/app-zero/r2mo-apps/app-r2mo/r2mo-apps-admin/r2mo-apps-admin-ui/.cursor/rules/r2-frontend-admin-list-page.mdc`
- `/Users/lang/zero-cloud/app-zero/r2mo-apps/app-r2mo/r2mo-apps-admin/r2mo-apps-admin-ui/.cursor/rules/r2-frontend-admin-dialog-form.mdc`
- `/Users/lang/zero-cloud/app-zero/r2mo-apps/app-r2mo/r2mo-apps-admin/r2mo-apps-admin-ui/.cursor/rules/r2-frontend-rust.mdc`
- `/Users/lang/zero-cloud/app-zero/r2mo-apps/app-r2mo/r2mo-apps-admin/r2mo-apps-admin-ui/.r2mo/repo/mxt/.cursor/rules/r2-ui-frontend-antd.mdc`
- `/Users/lang/zero-cloud/app-zero/r2mo-apps/app-r2mo/r2mo-apps-admin/r2mo-apps-admin-ui/.r2mo/repo/mxt/.cursor/rules/r2-ui-redux-architecture.mdc`
- `/Users/lang/zero-cloud/app-zero/r2mo-apps/app-r2mo/r2mo-apps-admin/r2mo-apps-admin-ui/.r2mo/repo/mxt/.cursor/rules/r2-ui-ajax-patterns.mdc`
- `/Users/lang/zero-cloud/app-zero/r2mo-apps/app-r2mo/r2mo-apps-admin/r2mo-apps-admin-ui/.r2mo/repo/mxt/.cursor/rules/r2-ui-i18n-system.mdc`
- `/Users/lang/zero-cloud/app-zero/r2mo-apps/app-r2mo/r2mo-apps-admin/r2mo-apps-admin-ui/.r2mo/repo/mxt/.cursor/rules/r2-ui-component-tiers.mdc`
- `/Users/lang/zero-cloud/app-zero/r2mo-apps/app-r2mo/r2mo-apps-admin/r2mo-apps-admin-ui/.r2mo/repo/mxt/.cursor/rules/r2-ui-exlist-patterns.mdc`
- `/Users/lang/zero-cloud/app-zero/r2mo-apps/app-r2mo/r2mo-apps-admin/r2mo-apps-admin-ui/.r2mo/repo/mxt/.cursor/rules/r2-ui-exform-patterns.mdc`
- `/Users/lang/zero-cloud/app-zero/r2mo-apps/app-r2mo/r2mo-apps-admin/r2mo-apps-admin-ui/src/app.rs`
- `/Users/lang/zero-cloud/app-zero/r2mo-apps/app-r2mo/r2mo-apps-admin/r2mo-apps-admin-ui/src/components/layout.rs`
- `/Users/lang/zero-cloud/app-zero/r2mo-apps/app-r2mo/r2mo-apps-admin/r2mo-apps-admin-ui/src/menu.rs`
- `/Users/lang/zero-cloud/app-zero/r2mo-apps/app-r2mo/r2mo-apps-admin/r2mo-apps-admin-ui/src/pages/mod.rs`
- `/Users/lang/zero-cloud/app-zero/r2mo-apps/app-r2mo/r2mo-apps-admin/r2mo-apps-admin-ui/src/pages/apps/mod.rs`
- `/Users/lang/zero-cloud/app-zero/r2mo-apps/app-r2mo/r2mo-apps-admin/r2mo-apps-admin-ui/src/pages/desktop/mod.rs`
- `/Users/lang/zero-cloud/app-zero/r2mo-apps/app-r2mo/r2mo-apps-admin/r2mo-apps-admin-ui/src/pages/security/mod.rs`
- `/Users/lang/zero-cloud/app-zero/r2mo-apps/app-r2mo/r2mo-apps-admin/r2mo-apps-admin-ui/src/pages/product/mod.rs`
- `/Users/lang/zero-cloud/app-zero/r2mo-apps/app-r2mo/r2mo-apps-admin/r2mo-apps-admin-ui/src/pages/personal/mod.rs`
- `/Users/lang/zero-cloud/app-zero/r2mo-apps/app-r2mo/r2mo-apps-admin/r2mo-apps-admin-ui/src/pages/tenant/mod.rs`
- `/Users/lang/zero-cloud/app-zero/r2mo-apps/app-r2mo/r2mo-apps-admin/r2mo-apps-admin-ui/src/utils/http.rs`

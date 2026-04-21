# Frontend React Admin Guide

> Final guide for the React / Ant Design enterprise admin stack used in the R2MO frontend ecosystem.

## 1. Scope

Use this file for:

- React-based web SPA admin frontends,
- Ant Design-style enterprise admin patterns,
- generated-route React pages,
- Redux and `Ux` / `Ex` / `Dsl` page architecture.

Do not use this file for:

- Rust / Leptos / Tauri work,
- app-specific design tokens,
- or Rust admin route/layout guidance.

## 2. Stack Identity

This stack is characterized by:

- React class-component page architecture,
- Ant Design-based framework wrappers,
- Redux state with `Ux` / `Dsl` helpers,
- Webpack/Babel build flow,
- generated routes and environment files,
- SCSS modules and tiered CSS prefixes.

## 3. Source Layout

```text
src/
├── components/
│   └── {page}/
│       ├── Cab.json
│       ├── Cab.module.scss
│       ├── Op.js
│       └── UI.js
├── extension/
│   ├── components/
│   │   └── {domain}/{page}/
│   │       ├── Cab.json
│   │       ├── Cab.module.scss
│   │       ├── Op.js
│   │       ├── UI.js
│   │       └── form/
│   │           ├── UI.Add.js
│   │           ├── UI.Edit.js
│   │           └── UI.Filter.js
│   ├── cerebration/
│   ├── ecosystem/
│   └── library/
├── economy/
├── container/
├── cab/
├── skin/
├── ux/
├── ex/
└── zone/
```

## 4. Component Tier System

| Tier | Path | CSS Prefix | Use |
|---|---|---|---|
| `web` | `src/economy/` | `uni_` | standard form/table primitives |
| `ei` | `src/extension/ecosystem/` | `uex_` | reusable business components |
| `oi` | `src/extension/eclat/` | `uox_` | config-driven components |
| `uca` | `src/extension/components/` | `uca_` | feature pages |
| `my` | custom | `umy_` | app-specific components |

## 5. Mandatory Page Pattern

- Pages use `React.PureComponent`, not function components.
- Pages are decorated with `@Ux.zero(Ux.rxEtat(...).to())`.
- `render()` must be gated by `Ex.yoRender(this, fn, debug)` so rendering waits for `$ready`.
- Initialization must go through `Ex.yiStandard(...)` or `Ex.yiAssist(...)`.

Reference shape:

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

## 6. Standard File Roles

| File | Role |
|---|---|
| `Cab.json` | page config, namespace, `_assist` mappings |
| `Cab.module.scss` | page-local styles |
| `Op.js` | event handlers / operations |
| `UI.js` | main page component |
| `UI.Add.js` / `UI.Edit.js` / `UI.Filter.js` | form variants |

## 7. CRUD and Form Rules

### ExList

| Component | Use |
|---|---|
| `ExListFast` | quick list with search, pagination, and actions |
| `ExListComplex` | full CRUD list with Add/Edit/Filter forms |

Reference pattern:

```jsx
<ExListComplex {...Ex.yoAmbient(this)}
    config={Ux.fromHoc(this, 'grid')}
    $form={{ FormAdd, FormEdit, FormFilter }}
    $query={$query}
/>
```

### ExForm

| Mode | Call |
|---|---|
| ADD | `Ex.yoForm(this, null)` |
| EDIT | `Ex.yoForm(this, null, $inited)` |
| FILTER | `Ex.yoFilter(this)` |

Filter decorators typically use `.raft(1).form()`.

## 8. Operation Rules

- `Op.js` handler keys must start with `$op`.
- Form submit/delete/filter should use `Ex.form(reference).add/save/remove/filter(...)`.
- Post-operation Redux sync may be chained through `.then(...)`.

Reference pattern:

```js
const $opSave = (reference) => (params) =>
    Ex.form(reference).save(params, {
        uri: '/api/resource/:key',
        dialog: 'saved'
    });
```

## 9. State Rules

- Do not store raw objects or arrays in Redux state.
- Wrap arrays and objects with `Dsl.getArray(...)` and `Dsl.getObject(...)`.
- `$t_xxx` means tabular or dictionary data.
- `$a_xxx` means auxiliary or component data.
- Read data through `Ux.onDatum(reference, 'key')`.
- Use `Ex.yiAssist(reference)` to load `_assist` data declared in `Cab.json`.

## 10. HTTP Rules

Preferred APIs:

- framework APIs via `Ex.I.*`
- custom HTTP via `Ux.ajaxGet/Post/Put/Delete`

Required behavior:

- `Ux.ajax*` auto-extracts `response.data`
- path params use `:param` style
- handle `401` with `Ux.toUnauthorized(reference)`
- treat `404` as potentially feature-disabled
- use `Ux.parallel(...)` for parallel requests

## 11. i18n and Environment Rules

### i18n

- All user-facing strings must come from `src/cab/**` JSON resources.
- Read text with `Ux.fromHoc(this, 'key')`.
- Do not hardcode display strings.

### Environment

All custom frontend env vars use the `Z_` prefix.

Common keys:

| Variable | Purpose |
|---|---|
| `Z_ENDPOINT` | API base URL |
| `Z_APP` | app ID |
| `Z_LANGUAGE` | default language |
| `Z_ROUTE` | route prefix |
| `Z_K_SESSION` | session key prefix |
| `Z_CSS_COLOR` | primary color token |

## 12. Generated Files

Never hand-edit:

```text
src/container/index.js
src/components/index.js
src/extension/components/index.js
src/extension/cerebration/index.js
src/environment/routes.js
src/environment/datum.js
```

Route changes belong in route-generation scripts, not in generated output.

## 13. Anti-Patterns

| Anti-pattern | Why it is wrong |
|---|---|
| function components/hooks for pages | framework decorators depend on class components |
| raw objects in Redux | causes silent mutation/state bugs |
| skipping `$ready` gate | renders before async init completes |
| missing `$op` prefix | `RxEtat.bind()` may ignore handlers |
| hardcoded strings | breaks i18n contract |
| hand-editing generated files | changes get overwritten on next build |
| wrong `Sk.mix*` prefix | tiered CSS naming collisions |

## 14. Final Rule

For this stack, use:

```text
Cab/Op/UI structure -> generated route discipline -> typed state handling -> i18n/env compliance
```

# Runtime Configuration Surface

> Final guide for the runtime configuration surface exposed by `r2mo-rapid`.

## 1. Scope

Use this file for:

- runtime env/application model reasoning,
- bootstrap-level runtime control points,
- provider-selection configuration reading,
- architecture-level interpretation of environment variables.

Do not use this file for:

- module topology,
- container-layer ownership,
- or general repository architecture mapping.

## 2. Why This Surface Exists

The bootstrap layer is not only a dependency bundle.
In practice, it also defines the runtime configuration surface that projects consume through application models and environment variables.

This means runtime configuration belongs closer to:

- `r2mo-boot-*`,
- shared abstractions,
- provider selection,

than to project-private service logic.

## 3. Architecture-Level Runtime Meaning

Environment variables should be treated as architecture-level control points because they can:

- shape tenant-aware runtime context,
- select locale and resource behavior,
- choose style or mode variants,
- switch application mode and bootstrap behavior,
- determine which shared providers are activated before business code starts.

## 4. Reading Order For Runtime Reasoning

When the task is configuration-heavy, read in this order:

1. runtime env/application model
2. bootstrap assembly chain
3. shared abstraction and provider-selection path
4. business code

This avoids misreading a runtime-selection issue as a business-service defect.

## 5. What Counts As Runtime Surface

Runtime surface usually includes:

- application mode selection,
- profile selection,
- tenant and environment selection,
- locale or i18n defaults,
- style or presentation mode selectors,
- storage, DB, cache, or security provider selectors,
- documentation and API exposure mode selectors.

## 6. What Does Not Belong Here

Do not treat the following as shared runtime surface:

- project-private business toggles,
- customer-only workflow switches,
- single-module feature flags that do not affect shared framework behavior.

Those belong in project-local application logic, not framework-level reasoning.

## 7. Final Rule

For runtime-heavy questions, use:

```text
runtime model -> bootstrap assembly -> provider selection -> business code
```

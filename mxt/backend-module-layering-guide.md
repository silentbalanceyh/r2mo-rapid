# Backend Module Layering Guide

> Final backend layering guide for the app-takeout / R2MO Spring backend pattern.

## 1. Scope

Use this file for:

- backend module topology,
- DPA ownership,
- generated vs handwritten service placement,
- controller vs provider vs domain boundaries.

## 2. Stack Identity

This app is Spring-first R2MO.
Its root parent is `io.zerows:r2mo-0216`.

Backend modules follow strict DPA layering:

- `app-takeout-domain`
- `app-takeout-provider`
- `app-takeout-api`

Dependency direction is fixed:

```text
api -> provider -> domain
```

## 3. Domain Layer Rules

The domain layer owns:

- entity definitions,
- enums,
- stable business vocabulary,
- shared base contracts.

Rules:

- entity naming follows `{Name}Entity`
- entities commonly inherit shared base types such as `BaseEntity`
- business enums live under `enums/`
- domain may host shared operation bases reused by provider implementations

## 4. Provider Layer Rules

The provider layer owns:

- business execution,
- persistence orchestration,
- mapper-backed implementation,
- integration logic.

Rules:

- generated CRUD-style services usually live under `service/gen/{entity}/`
- handwritten business or integration logic lives under `service/v1/`
- typical implementations are Spring `@Service` or `@Component`
- do not invent a new repository layer when DBE already owns persistence abstraction

## 5. API Layer Rules

The API layer owns:

- HTTP entrypoints,
- validation boundary,
- scheduling,
- app configuration.

Rules:

- controllers expose `R<T>`-wrapped responses
- request payloads use `@Valid`
- CRUD contracts may be declared in controller interfaces and implemented separately
- pagination and search endpoints accept JSON query objects and return framework pagination types

## 6. Generated and Handwritten Module Rules

- generated business modules follow stable names such as `I{Name}ServiceV1Impl`
- provider implementations may extend shared base operation classes for CRUD behavior
- CRUD controller contracts expose create/update/find/delete/page/all/import/export variants
- search endpoints commonly accept `JObject query` to stay aligned with DBE QR syntax

## 7. Business Workflow Placement

- keep sync and integration workflows under provider `service/v1/`
- use small components for focused responsibilities such as item calculation, payload building, or remote sync steps
- keep controller logic thin
- when branching grows, move it into provider services or components

## 8. Scheduling Boundary

In real Spring / R2MO landing:

- transport and schedule wiring belongs in `-api`
- business execution belongs in `-provider`

Example:

- Quartz job planner and job factory belong in `-api`
- sync and calculation services belong in `-provider`

## 9. Final Rule

For this backend pattern, use:

```text
domain defines -> provider executes -> api exposes and schedules
```

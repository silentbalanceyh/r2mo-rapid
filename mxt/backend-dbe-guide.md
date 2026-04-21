# Backend DBE Guide

> Final DBE usage guide for the app-takeout / R2MO backend pattern.

## 1. Scope

Use this file for:

- DBE usage,
- query envelope conventions,
- persistence-entry decisions,
- generated CRUD alignment with DBE.

## 2. Core DBE Rule

Prefer DBE as the entity-oriented persistence facade.

Reference patterns:

- `DBE.of(EntityClass.class, mapper)`
- `DBE.of(this.entityCls, this.mapper())`

Do not create a parallel repository abstraction unless DBE is genuinely insufficient.

## 3. Query Rules

Query criteria should be expressed with `JObject` QR/DBE syntax.

Common execution flow:

1. build criteria via `JObject`
2. call a generated service query method or DBE directly
3. diff remote and local data if needed
4. persist create or update batches through DBE

## 4. Search Endpoint Rules

- search endpoints commonly accept `JObject query`
- list queries should use QQuery/QR-style pagination and sorting
- do not invent ad-hoc query contracts when DBE already defines the shape

## 5. Generated CRUD Alignment

Generated CRUD services and controllers should stay aligned with DBE query conventions.

Rules:

- generated services should remain DBE-compatible
- CRUD contracts should preserve DBE query semantics
- page/all/import/export variants should not drift into project-private query shapes

## 6. Final Rule

For persistence and search, use:

```text
DBE facade -> JObject query shape -> generated CRUD compatibility -> no duplicate repository layer
```

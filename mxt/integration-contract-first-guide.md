# Integration Contract-First Guide

> Final contract-first integration guide for the app-takeout / R2MO pattern.

## 1. Scope

Use this file for:

- contract-first development,
- `.r2mo` source-of-truth rules,
- schema and entity alignment,
- reusable integration-field conventions.

## 2. Source Of Truth

`.r2mo` specifications are the source of truth for backend, frontend, and cross-layer integration.

Backend implementation must align with:

- `.r2mo/domain/*.proto`
- `.r2mo/api/components/schemas/*.md`
- `.r2mo/api/operations/{uri}/*.md`

Frontend generation also starts from the same `.r2mo` specs.

## 3. Data Model Definition Strategy

- prefer reuse before creating new entities
- search the current project first
- then search `ZERO_SPEC` (`$ZERO_SPEC`)
- create new definitions only when reuse fails

Supported contract sources:

- `r2mo-spec` markdown schemas
- protobuf in `.r2mo/domain/*.proto`
- Java domain entity classes
- OpenAPI-style schema markdown in `.r2mo/api/components/schemas/*.md`

## 4. Schema Alignment Rules

- Java entities and markdown schema definitions must stay structurally consistent
- relation sections in markdown must match foreign-key fields in Java entities
- contract drift is a defect, not a documentation issue

## 5. Standard Entity Model For Integration

Recommended common fields for management-style entities:

- identity: `id`
- isolation: `tenantId`, `appId`, `sigma`
- classification: `type`, `status`, `category`
- lifecycle: `active`, `language`, `metadata`, `version`
- audit: `createdAt`, `createdBy`, `updatedAt`, `updatedBy`

Rules:

- `tenantId` and `appId` are required in multi-tenant scenarios
- `metadata` is the controlled extension field
- UUID foreign keys should map consistently in Java and schema contracts

## 6. Final Rule

For integration modeling, use:

```text
.r2mo contract first -> reuse before create -> schema and entity alignment -> controlled extension through metadata
```

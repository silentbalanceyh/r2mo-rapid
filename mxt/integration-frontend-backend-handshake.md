# Integration Frontend Backend Handshake

> Final frontend/backend handshake guide for the app-takeout / R2MO pattern.

## 1. Scope

Use this file for:

- frontend/backend contract handshake,
- generated model and API-client rules,
- auth endpoint integration shape,
- DBE query envelope shape.

## 2. Backend Integration Boundary

Use strict DPA layering:

- `*-domain`: entities, enums, exceptions, service interfaces
- `*-provider`: mapper, service implementation, persistence logic
- `*-api`: controller, DTO, config, scheduled tasks

Rules:

- API handles HTTP, validation, and serialization
- provider owns business logic and persistence
- domain defines contracts and data shapes

## 3. Frontend Backend Handshake

- frontend reads `.r2mo` specs before coding
- model generation starts from `.r2mo/api/components/schemas/*.md` or `.r2mo/domain/*.proto`
- API client generation starts from `.r2mo/api/operations/*/*.md`
- UI implementation binds to requirements and design specs after model and API typing exist

Rules:

- Rust / Leptos guidance should use typed `serde` structs and `Result<T, AppError>`
- UI must consistently handle `loading`, `error`, `empty`, and `success` states

## 4. Authentication Integration Pattern

- auth contract belongs in domain
- auth implementation belongs in provider
- request handling belongs in API

Standard endpoints:

- `POST /api/auth/login`
- `POST /api/auth/logout`
- `POST /api/auth/refresh`
- `GET /api/auth/me`

Rules:

- token validation should be centralized in interceptor or middleware logic
- do not duplicate token validation across controllers
- logout invalidation may rely on cache or blacklist strategy

## 5. Query Contract Pattern

For DBE-style querying, use a normalized request envelope:

- `criteria`
- `pager`
- `sorter`
- `projection`

This gives a stable API query contract independent of storage implementation.

## 6. Final Rule

For frontend/backend integration, use:

```text
shared .r2mo contract -> typed models and API client -> centralized auth handling -> stable DBE query envelope
```

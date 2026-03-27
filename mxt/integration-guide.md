# App Takeout Integration Guide

## Scope
Extracted from integration-relevant MDC rules under `/Users/lang/zero-cloud/app-zero/r2mo-apps/app-takeout` for Harness 2.0 reuse.

## 1. Contract-first integration
- `.r2mo` specifications are the source of truth for backend, frontend, and cross-layer integration.
- Backend implementation must align with:
  - `.r2mo/domain/*.proto`
  - `.r2mo/api/components/schemas/*.md`
  - `.r2mo/api/operations/{uri}/*.md`
- Frontend generation also starts from the same `.r2mo` specs, then derives models, API clients, and pages.

## 2. Backend integration boundary: DPA
- Use strict DPA layering:
  - `*-domain`: entities, enums, exceptions, service interfaces
  - `*-provider`: mapper, service implementation, persistence logic
  - `*-api`: controller, DTO, config, scheduled tasks
- Dependency direction is fixed: `Api -> Provider -> Domain`.
- API layer handles HTTP, validation, and serialization only.
- Provider layer owns business logic and persistence.
- Domain layer defines contracts and data shapes.

## 3. Data model definition strategy
- Prefer reuse before creating new entities.
- Search current project first, then ZERO_SPEC (`$ZERO_SPEC`), then create new if necessary.
- Supported contract sources:
  - `r2mo-spec` markdown schemas
  - Protobuf in `.r2mo/domain/*.proto`
  - Java domain entity classes
  - OpenAPI-style schema markdown in `.r2mo/api/components/schemas/*.md`
- Java entities and markdown schema definitions must stay structurally consistent.
- Relation sections in markdown must match foreign-key fields in Java entities.

## 4. Standard entity model for integration
Recommended common fields for management-style entities:
- Identity: `id`
- Multi-tenant/app isolation: `tenantId`, `appId`, `sigma`
- Classification: `type`, `status`, `category`
- Lifecycle: `active`, `language`, `metadata`, `version`
- Audit: `createdAt`, `createdBy`, `updatedAt`, `updatedBy`

Guidance:
- `tenantId` and `appId` are required in multi-tenant scenarios.
- `metadata` is the extension field for domain-specific customization.
- UUID foreign keys should map consistently in Java and schema contracts.

## 5. Frontend/backend handshake
- Frontend reads `.r2mo` specs before coding.
- Model generation path:
  - `.r2mo/api/components/schemas/*.md` or `.r2mo/domain/*.proto`
  - map to typed frontend models
- API client generation path:
  - `.r2mo/api/operations/*/*.md`
  - map each operation to a typed API function
- UI implementation should then bind to requirement/design specs.
- Rust/Leptos guidance explicitly uses typed `serde` structs and `Result<T, AppError>` API calls.
- UI must consistently handle `loading`, `error`, `empty`, and `success` states.

## 6. Authentication integration pattern
- Auth contract belongs in Domain, auth implementation in Provider, request handling in API.
- Standard endpoints:
  - `POST /api/auth/login`
  - `POST /api/auth/logout`
  - `POST /api/auth/refresh`
  - `GET /api/auth/me`
- Token validation should be centralized in interceptor/middleware, not duplicated across controllers.
- JWT secret and expiry settings must be externalized.
- Logout invalidation can rely on cache/blacklist strategy.

## 7. Query contract pattern
For DBE-style querying, use a normalized request envelope:
- `criteria`: nested query tree
- `pager`: page/size
- `sorter`: ordered sort expressions
- `projection`: selected fields

This gives a stable API query contract independent of storage implementation.

## 8. Runtime contract and configuration
- Environment-specific configuration must be externalized.
- Use profile split:
  - `application.yml`
  - `application-dev.yml`
  - `application-test.yml`
  - `application-prod.yml`
- Sensitive values must come from environment variables, not hardcoded literals.
- This applies to MySQL, Redis, JWT, and similar runtime dependencies.
- Use typed `@ConfigurationProperties` classes and validation for configuration binding.

## 9. Harness 2.0 reusable rules
1. Start from `.r2mo` contracts, not from implementation guesses.
2. Keep DPA boundaries strict so generated integrations remain composable.
3. Normalize entity models around shared management fields.
4. Treat `tenantId`/`appId` as first-class isolation fields.
5. Generate frontend models and API clients from the same contract source as backend.
6. Externalize all runtime dependencies and secrets.
7. Use `metadata` for controlled extension instead of forking common schemas too early.

## Evidence
- `/Users/lang/zero-cloud/app-zero/r2mo-apps/app-takeout/.cursor/rules/r2-backend-spring-dpa.mdc:142`
  - backend must reference `.r2mo/domain/*.proto`, `.r2mo/api/components/schemas/*.md`, `.r2mo/api/operations/{uri}/*.md`
- `/Users/lang/zero-cloud/app-zero/r2mo-apps/app-takeout/.cursor/rules/r2-backend-spring-dpa.mdc:148`
  - `.r2mo` specifications are the source of truth
- `/Users/lang/zero-cloud/app-zero/r2mo-apps/app-takeout/.cursor/rules/r2-backend-spring-frontend.mdc:22`
  - frontend starts by reading `.r2mo` specifications
- `/Users/lang/zero-cloud/app-zero/r2mo-apps/app-takeout/.cursor/rules/r2-backend-spring-frontend.mdc:28`
  - generate data models from schema/proto contracts
- `/Users/lang/zero-cloud/app-zero/r2mo-apps/app-takeout/.cursor/rules/r2-backend-spring-frontend.mdc:32`
  - generate API client from `.r2mo/api/operations/`
- `/Users/lang/zero-cloud/app-zero/r2mo-apps/app-takeout/.cursor/rules/r2-spec-entity.mdc:181`
  - standard entity field set for management systems
- `/Users/lang/zero-cloud/app-zero/r2mo-apps/app-takeout/.cursor/rules/r2-spec-entity.mdc:187`
  - multi-tenancy fields `tenantId` and `appId`
- `/Users/lang/zero-cloud/app-zero/r2mo-apps/app-takeout/.cursor/rules/r2-backend-spring-config.mdc:9`
  - externalize config; avoid hardcoded credentials

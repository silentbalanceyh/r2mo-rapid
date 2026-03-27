# Harness 2.0 Backend Dev Guide

## app-takeout / R2MO backend patterns

### 1. Stack identity and module topology
- This app is Spring-first R2MO. Root parent is `io.zerows:r2mo-0216`.
- Backend is organized as strict DPA modules:
  - `app-takeout-domain`: entity models, enums, exceptions, shared base contracts
  - `app-takeout-provider`: service implementations, mapper access, DBE-based persistence, integration logic
  - `app-takeout-api`: controllers, request/response contracts, Quartz job wiring, app boot layer
- Module dependency is one-way: `api -> provider -> domain`.

### 2. Domain layer rules
- Domain owns entity definitions and stable business vocabulary.
- Entity naming is `{Name}Entity` and most entities inherit shared base types such as `BaseEntity`.
- Business enums live in `enums/` and are reused across provider and api layers.
- Domain may also host shared operation bases used by provider implementations.

### 3. Provider layer rules
- Provider implements business capabilities and owns persistence orchestration.
- Generated CRUD-style services commonly live under `service/gen/{entity}/`.
- Handwritten business/integration logic lives under `service/v1/`.
- Typical implementation style is Spring `@Service` or `@Component` plus mapper-backed execution.
- Avoid inventing custom repository layers when DBE already provides the persistence abstraction.

### 4. API layer rules
- API owns HTTP entrypoints, validation boundary, scheduling, and app configuration.
- Controllers expose `R<T>`-wrapped responses and accept `@Valid` request payloads.
- CRUD interfaces may be defined as controller contracts and implemented separately.
- Pagination/search endpoints accept JSON query objects and return framework pagination types.

### 5. DBE usage pattern
- Prefer `DBE.of(EntityClass.class, mapper)` or generic `DBE.of(this.entityCls, this.mapper())`.
- Use DBE as the entity-oriented persistence façade instead of building a new repository abstraction.
- Query criteria are expressed with `JObject` QR/DBE syntax.
- A common pattern is:
  1. build criteria via `JObject`
  2. call generated service query method or DBE directly
  3. diff remote/local data if needed
  4. persist create/update batches through DBE

### 6. DPA in real Spring/R2MO landing
- DPA is not only folder naming; it maps to execution ownership:
  - Domain defines entities and shared contracts
  - Provider executes business logic and persistence
  - API wires transport concerns and schedulers
- In app-takeout, Quartz job planner and job factory are in `-api`, while sync/calculation services are in `-provider`.
- This is the intended split: transport/schedule in API, business execution in Provider.

### 7. CRUD and generated-module conventions
- Generated business modules follow stable naming such as `I{Name}ServiceV1Impl`.
- Provider implementations often extend a shared base operation class to inherit common CRUD behavior.
- CRUD controller contracts expose create/update/find/delete/page/all/import/export variants.
- Search endpoints commonly accept `JObject query` to stay aligned with DBE QR syntax.

### 8. Business module guidance
- Keep sync/integration workflows in provider `service/v1/` packages.
- Use small components for focused responsibilities such as item/product calculation, payload building, or remote sync steps.
- Keep controller logic thin; when business branching grows, move it into provider services/components.
- For scheduled workflows, pass runtime context through `JobDataMap` and autowire jobs through a custom `SpringBeanJobFactory`.

### 9. Validation and exception guidance from local MDC
- Validate at API boundary with JSR-303 / Bean Validation.
- Business validation that depends on database state belongs in provider services.
- Throw typed exceptions in provider/business code and format them uniformly in API global handlers.
- For list queries, use QQuery/QR-style pagination and sorting instead of ad-hoc query contracts.

## Evidence

### Root module layout
- `app-takeout/pom.xml:6-19` declares parent `io.zerows:r2mo-0216` and modules `app-takeout-domain`, `app-takeout-provider`, `app-takeout-api`.

### Domain evidence
- `app-takeout-domain/src/main/java/com/formaltech/apps/takeout/domain/OrderEntity.java`
- `app-takeout-domain/src/main/java/com/formaltech/apps/takeout/domain/ProductEntity.java`
- `app-takeout-domain/src/main/java/com/formaltech/apps/takeout/enums/OrderStatus.java`
- `app-takeout-domain/src/main/java/com/formaltech/apps/takeout/enums/ShopType.java`

### Provider evidence
- `app-takeout-provider/src/main/java/com/formaltech/apps/takeout/service/gen/merchant/IMerchantServiceV1Impl.java:15-26`
- `app-takeout-provider/src/main/java/com/formaltech/apps/takeout/service/v1/job/AbstractSyncService.java:19-30`
- `app-takeout-provider/src/main/java/com/formaltech/apps/takeout/service/v1/job/CalcServiceV1ItemComponent.java:25-62`

### API evidence
- `app-takeout-api/src/main/java/com/formaltech/apps/takeout/controller/ProfileController.java:22-107`
- `app-takeout-api/src/main/java/com/formaltech/apps/takeout/controller/gen/orderlog/OrderLogCrudController.java:22-63`
- `app-takeout-api/src/main/java/com/formaltech/apps/takeout/task/MerchantJobPlanner.java:14-97`
- `app-takeout-api/src/main/java/com/formaltech/apps/takeout/config/TakeoutAppJobFactory.java:12-23`

### Local MDC evidence
- `.cursor/rules/r2-backend-spring-dpa.mdc`
- `.cursor/rules/r2-backend-spring-dbe.mdc`
- `.cursor/rules/r2-backend-spring-validation.mdc`
- `.cursor/rules/r2-backend-spring-exception.mdc`
- `.cursor/rules/r2-backend-spring-pagination.mdc`
- `.cursor/rules/r2-backend-spring-job.mdc`

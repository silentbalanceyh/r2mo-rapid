# Dual-Side Development

R2MO explicitly adopts a dual-container model:

- Spring Boot container
- Vert.x container

The README also makes it clear that **some implementation models are mutually exclusive**.

This means development must always distinguish:

1. Is this a shared cross-side problem, or a single-side implementation problem?
2. Am I writing an abstraction, or the implementation of one specific container?

## 1. Core Principles of Dual-Side Development

### Principle 1: Abstract first, implement second

If a requirement may serve both Spring and Vert.x, prefer considering abstraction in the following layers first:

- `r2mo-ams`
- `r2mo-spec`
- `r2mo-dbe`
- `r2mo-io`
- `r2mo-jaas`
- `r2mo-jce`

Then implement it in `r2mo-spring*` or `r2mo-vertx*`.

### Principle 2: Container-specific logic must not pretend to be shared logic

For example:

- Spring MVC exception handling belongs to Spring.
- Spring Security filter chains belong to Spring.
- Vert.x async DB context belongs to Vert.x.

These should not be forcibly extracted into the shared layer.

### Principle 3: Shared contracts must be interpretable by both sides

If some schema, claim, error code, or query structure can only be understood on Spring, it is not suitable for the bilateral shared layer.

## 2. Suggested Workflow for Dual-Side Development

### Scenario A: Add a foundational capability

For example, adding a unified storage protocol.

Suggested order:

1. Define the interface and data structures in the abstraction layer.
2. Decide whether SPI is required.
3. Evaluate whether both Spring and Vert.x need implementations.
4. Even if one side is not implemented yet, do not design the abstraction so that it only works for the other side.

### Scenario B: Add a single-side plugin

For example, adding OAuth2 login only to Spring.

Suggested order:

1. Confirm first that the impact is really Spring-only.
2. Keep shared claims / error codes / schemas in the shared layer.
3. Put Filters, Providers, configuration, and AutoConfiguration into `r2mo-spring-security-*`.

### Scenario C: Modify query capabilities

If you are changing JSON query syntax, CRUD models, or paging/sorting structures, that usually affects both sides and should be inspected in `r2mo-dbe` first.

## 3. How to Choose Spring vs Vert.x Quickly

### Choose Spring first when

- The feature is driven by Spring MVC, Bean lifecycle, or Spring Security extension hooks.
- The real landing modules are `r2mo-spring`, `r2mo-spring-security`, `r2mo-spring-*`, or `r2mo-boot-spring*`.
- You need Boot auto-assembly, container-managed filters, Web exception mapping, or Spring-side configuration binding.

### Choose Vert.x first when

- The feature is driven by async runtime flow, non-blocking execution, or jOOQ runtime/generation integration.
- The real landing modules are `r2mo-vertx`, `r2mo-vertx-jooq*`, or `r2mo-boot-vertx`.
- You need DB context wiring or runtime bridges that only make sense in Vert.x.

### Choose neither side first when

- The requirement is really about shared schema, shared error semantics, shared query language, shared storage abstraction, or shared auth primitives.
- The first nouns in the requirement are `DBE`, `SPI`, `spec`, `claim`, `schema`, `error code`, `HFS`, or `RFS` instead of Spring/Vert.x nouns.

## 4. How to Handle Cross-Side Differences

### Spring side leans more toward sync/container assembly

Typical modules:

- `r2mo-spring`
- `r2mo-spring-security`
- `r2mo-boot-spring`
- `r2mo-boot-spring-default`

### Vert.x side leans more toward async/runtime bridging

Typical modules:

- `r2mo-vertx`
- `r2mo-vertx-jooq`
- `r2mo-vertx-jooq-jdbc`
- `r2mo-vertx-jooq-generate`
- `r2mo-boot-vertx`

## 4. Capabilities That Should Naturally Be Unified Across Both Sides

- Unified JSON model semantics
- Query trees / CRUD / paging / sorting expressions
- File storage abstractions
- User claim / token base structures
- Error structures and API contracts

## 5. Capabilities That Are Usually Single-Side

- Spring Security login flows
- Spring Bean auto-assembly
- Vert.x async database execution context
- Jetty / Web-container-level configuration

## 6. Common Errors in Dual-Side Development

### Error 1: Writing Spring first, then forcefully carving out abstractions

This usually drags Spring assumptions into the shared layer.

### Error 2: Doing lowest-common-denominator design in the name of "cross-side unification"

Shared abstractions should extract commonality, not sacrifice usability.

### Error 3: Forgetting that some implementation chains are mutually exclusive

For example, typed / DB / security implementation chains should not be assumed to be simultaneously enabled.

## 7. Decision Template

Every time a dual-side-related requirement appears, ask first:

1. What is the minimal shared core of this requirement?
2. Which parts belong only to Spring?
3. Which parts belong only to Vert.x?
4. Which parts should become swappable through SPI?
5. Which parts should never leave the business project?

Once these five questions are answered clearly, the boundary is usually stable.

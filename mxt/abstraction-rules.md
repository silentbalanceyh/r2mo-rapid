# Abstraction Rules

This file answers one core question: **what requirements should be escalated to the framework abstraction layer, and what should remain in business projects.**

## 1. Admission Criteria for the Framework Layer

### Rule 1: Cross-project reuse must genuinely exist

Abstraction is only worth doing when a requirement already appears, or is likely to appear, across multiple business projects.

Suitable for escalation:

- Unified file storage protocols
- Common login patterns
- Generic query syntax
- Shared error codes and OpenAPI schemas

Not suitable for escalation:

- A customer-specific approval rule
- An industry-specific report format
- A project-specific custom menu field

### Rule 2: Abstraction solves commonality, not naming wrappers

If you only rename business logic with a more generic label, it is still business logic and should not enter the framework.

Bad examples:

- Wrapping a "financial voucher approval flow" as a "generic workflow engine extension"
- Wrapping a "hotel check-in state machine" as a "generic state-driven module"

### Rule 3: Dual-container-sensitive capabilities should be abstracted first

If a capability can naturally be shared by both Spring and Vert.x, prefer abstracting from `spec / dbe / io / jaas / jce / ams` rather than hardcoding it into Spring first.

Examples:

- Unified authentication claim structures
- Unified IO provider protocols
- Unified query trees / condition expressions
- Unified error response structures

### Rule 4: The boot layer only does assembly, not business

The responsibility of `r2mo-boot-spring` / `r2mo-boot-vertx` is:

- Aggregating base dependencies
- Providing default bootstrap assembly
- Reducing project onboarding cost

It should not contain:

- Business default values
- Industry-specific default flows
- Customer-specific configuration

### Rule 5: The spec layer holds contracts only, not container implementations

`r2mo-spec` may contain:

- OpenAPI specifications
- Shared schemas
- Error codes
- Markdown documentation conventions

`r2mo-spec` must not contain:

- Spring Bean assembly logic
- Vert.x runtime logic
- Business service calls
- Container-specific annotation behavior

## 2. Criteria for Staying in Business Projects

The following should explicitly remain in business projects:

1. Fields, enums, state machines, and flow names are clearly business language.
2. Requirements depend on specific departments, tenants, organizational structures, or customer contracts.
3. The reuse radius is limited to a single project.
4. The work is only "using framework capabilities", not "extending framework capabilities".
5. It will not form shared APIs, SPIs, or schemas for external reuse.

## 3. Decision Table

| Question | Yes | No |
|---|---|---|
| Reused across multiple projects? | Continue evaluating for abstraction | Stay in business project |
| Needs a unified contract or unified provider? | Becomes a framework candidate | Stay in business project |
| Depends on Spring/Vert.x private mechanisms? | Put it in the container layer | Check whether it can be pushed down to spec/abstraction |
| Only a business rule with a different name? | Stay in business project | Continue evaluating |
| Should be assembled through SPI? | Move into the extension layer | It may only be in-project composition |

## 4. Recommended Placement Rules

### Enter `r2mo-spec`

When the requirement is a shared contract:

- Shared schemas
- OpenAPI descriptions
- Error codes
- Documentation protocols

### Enter `r2mo-dbe` / `r2mo-io` / `r2mo-jaas` / `r2mo-jce`

When the requirement is a cross-container base capability:

- Data access abstraction
- Storage / transmission abstraction
- Login / claim / token abstraction
- Encryption and signing abstraction

### Enter `r2mo-spring-*`

When the requirement is Spring-specific integration:

- Auto Configuration
- MVC / Web interception
- Spring Security Filter / Provider
- Spring Bean-level extensions

### Enter `r2mo-vertx-*`

When the requirement is Vert.x-specific integration:

- Async DB context
- jOOQ generation and runtime bridging
- Vert.x test support

### Stay in business projects

When the work is only:

- Calling DBE
- Calling HFS / RFS
- Combining existing security modules
- Implementing project-specific UserService / approval flow / menu logic

## 5. Anti-Patterns

### Anti-pattern 1: Designing a "large and complete" abstraction for a single project

Such abstractions usually have no second consumer and eventually turn the framework into a business dumping ground.

### Anti-pattern 2: Modifying `spec` directly when a requirement is Spring-specific

`spec` is not a Spring annex repository.

### Anti-pattern 3: Mistaking default implementations for abstractions

`r2mo-boot-spring-default` is only a default combination, not the only standard answer.

## 6. Final Hard Test

Escalation is only justified if all three questions still hold after reflection:

1. If it stays out of the framework, will multiple projects reinvent the same wheel?
2. Can that wheel be clearly expressed as a contract, abstraction, or pluggable extension?
3. After removing business nouns, does it still make sense?

If the third test fails, it is almost certainly business code and should not be escalated.

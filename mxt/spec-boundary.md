# Spec Boundary

`r2mo-spec` is one of the easiest directories in the repository to misuse.

## 1. What `r2mo-spec` Actually Is

From `r2mo-spec/pom.xml`, its positioning is:

1. OpenAPI documentation conventions shared across Spring / Vert.x.
2. Core reference Markdown for AI-driven development workflows.
3. `*.md` definitions and built-in YAML sections that can be parsed by annotations.
4. Unified error code and schema definitions.

This means `spec` is a **contract and documentation layer**, not a runtime container layer.

## 2. What May Be Placed into `r2mo-spec`

### 2.1 Interface Contracts

- OpenAPI documentation fragments
- Shared API structure descriptions
- Request / response model constraints

### 2.2 Shared Schemas

- `components/schemas` objects
- Data structure definitions reused across multiple services
- Standard structures that are business-neutral or weakly business-coupled

### 2.3 Error Codes and Error Models

- Unified error codes
- Unified error response structures
- Container-independent failure semantics

### 2.4 Documentation Conventions

- Markdown fragments
- Specification templates
- Descriptive resources read by the framework or generators

## 3. What Must Not Be Placed into `r2mo-spec`

### 3.1 Spring / Vert.x Runtime Logic

The following must not be stuffed into `spec`:

- Spring Bean configuration
- Filter / Interceptor / AutoConfiguration
- Vert.x runtime logic
- Data source connection logic

### 3.2 Concrete Implementation Details

- MyBatis Plus mapper behavior
- jOOQ runtime execution logic
- Local file storage implementation
- Concrete JWT signing algorithm assembly

### 3.3 Strong Business Definitions

Do not disguise tenant- or project-private structures as shared schemas.

## 4. Why the Boundary Must Be Protected

### Reason 1: `spec` is a dual-side shared layer

Once Spring-private logic is mixed in, Vert.x can no longer reuse it naturally.

### Reason 2: `spec` should remain stable

Contract layers require more stability than implementation layers. Once they are frequently polluted by implementation details, upstream and downstream become harder to coordinate.

### Reason 3: `spec` should be consumable by docs and tools

It behaves more like a "standard input" than executable code.

## 5. Scenarios That Fit Escalation into `spec`

- Multiple services are repeatedly maintaining the same schema set.
- OpenAPI documentation conventions are copied across different projects.
- The error code system needs unification.
- Some Markdown / YAML resource is consumed by multiple modules.

## 6. Scenarios That Do Not Fit Escalation into `spec`

- A DTO used only by one service.
- A tenant-specific custom error code.
- Annotation behavior used only inside one Spring service.
- Field rules that only apply to one database implementation.

## 7. Boundary Relationship with Other Layers

### `spec` vs `spring`

- `spec`: defines "what it looks like".
- `spring`: defines "how it runs in Spring".

### `spec` vs `dbe`

- `spec`: defines API / schema / error.
- `dbe`: defines database access abstractions and query models.

### `spec` vs business projects

- `spec`: shared contract.
- Business projects: concrete service semantics and workflows.

## 8. Common Escalation Signals Into `spec`

A requirement is a strong candidate for `spec` when agents see one or more of these signals:

- The same schema or error definition is copied across multiple services.
- The requirement is being described in OpenAPI or contract terms rather than container terms.
- Both Spring and Vert.x would need to interpret the same payload shape.
- The value comes from shared documentation, validation, or generator consumption instead of runtime framework hooks.
- The requirement can be explained without mentioning Spring Beans, filters, interceptors, or Vert.x runtime APIs.

Weak escalation signals that should be rejected:

- "It might be reused one day."
- "It is easier to put it in spec than to decide the real runtime landing zone."
- "The DTO looks generic."

## 9. Quick Decision Template

If a change is about to enter `spec`, ask:

1. After removing Spring / Vert.x names, does this definition still stand?
2. After removing the current business project, is it still worth keeping?
3. Is it a contract to be **consumed**, or code to be **executed**?

Only when the answers converge on "shared contract" should it enter `spec`.

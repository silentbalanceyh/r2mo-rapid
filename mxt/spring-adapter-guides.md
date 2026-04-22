# Spring Adapter Guides

> Combined guide for `r2mo-spring-json`, `r2mo-spring-template`, and `r2mo-spring-mybatisplus` in `r2mo-rapid`.

These three modules share a common pattern: they land a shared or third-party capability into the Spring Boot runtime through auto-configuration. They are adapters, not independent business modules.

## 1. `r2mo-spring-json`

### Ownership

- Depends on: `r2mo-spring`
- Role: Spring Boot auto-configuration for JSON serialization
- Provides: Jackson customization, serialization conventions, Spring MVC message converter alignment
- For: Spring projects that need consistent JSON behavior across request/response, persistence, and messaging

### When to read this module

- JSON serialization strategy differs from expected behavior
- Jackson `ObjectMapper` customization is needed
- request/response content-type negotiation requires adjustment
- `r2mo-typed-hutool` or `r2mo-typed-vertx` data types need Spring-side serialization wiring

### Boundary with `r2mo-typed-hutool`

`r2mo-typed-hutool` provides Hutool-based data-type implementations (no Spring dependency). `r2mo-spring-json` provides Spring-side Jackson configuration. They are complementary, not overlapping.

## 2. `r2mo-spring-template`

### Ownership

- Depends on: `r2mo-spring`, `spring-boot-starter-thymeleaf`
- Role: Spring Boot auto-configuration for Thymeleaf template rendering
- Provides: template engine setup, view resolution, template-related Spring conventions
- For: Spring projects that render server-side HTML through Thymeleaf

### When to read this module

- template resolution path needs customization
- Thymeleaf dialect or expression configuration is required
- server-side rendering behavior differs from expected

### Boundary

This module is Spring-specific. Template abstractions that should work on both Spring and Vert.x do not belong here.

## 3. `r2mo-spring-mybatisplus`

### Ownership

- Depends on: `r2mo-spring`, `r2mo-dbe-mybatisplus`, `mybatis-plus-spring-boot3-starter`, `dynamic-datasource-spring-boot-starter`
- Role: Spring Boot auto-configuration landing for the MyBatis-Plus DBE implementation
- Provides: starter auto-configuration, dynamic datasource integration, MyBatis-Plus Spring wiring
- For: Spring projects using MyBatis-Plus as their persistence layer

### When to read this module

- datasource configuration or multi-datasource routing is needed
- MyBatis-Plus auto-configuration behavior differs from expected
- Spring-side MyBatis-Plus starter conflicts with framework defaults

### Boundary with `r2mo-dbe-mybatisplus`

`r2mo-dbe-mybatisplus` provides the core MyBatis-Plus implementation of DBE abstractions (can be used without Spring Boot). `r2mo-spring-mybatisplus` provides the Spring Boot auto-configuration wiring. Read the DBE module first for query/CRUD logic. Read this module for Spring configuration and datasource setup.

## 4. Common Pattern

All three adapters follow the same structure:

```text
shared or third-party capability -> Spring auto-configuration -> boot integration
```

When debugging an adapter, read in this order:

1. the upstream capability module (`r2mo-ams`, `r2mo-dbe-mybatisplus`, etc.),
2. the adapter module itself,
3. `r2mo-boot-spring-default` for default inclusion decisions.

## 5. Do Not Do

- Do not put business serialization logic into `r2mo-spring-json`.
- Do not put business template content into `r2mo-spring-template`.
- Do not put business query logic into `r2mo-spring-mybatisplus`; it belongs in `r2mo-dbe-mybatisplus` or the business project.

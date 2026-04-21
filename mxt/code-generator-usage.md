# Code Generator Usage

> Final operational guide for the generator surface in `r2mo-rapid`.
> Use this file when the task is about generated code, generation customization, templates, or generator ownership.

## 1. Generator Families

This repository does not have one generator pipeline.
It has three generator families:

1. Spring scaffold generation in `r2mo-boot-spring`
2. MyBatis-Plus CRUD/SQL generation in `r2mo-dbe-mybatisplus`
3. jOOQ source/type generation in `r2mo-vertx-jooq-generate`

Agents should classify the family before reading code or changing output.

## 2. Spring Scaffold Generator

Primary module:

- `r2mo-boot-spring`

Primary class:

- `io.r2mo.boot.spring.generator.SourceGenerator`

### How it works

`SourceGenerator` accepts a `GenConfig` implementation class and then:

1. instantiates the config,
2. reads `GenMeta.spi`,
3. resolves a concrete `GenProcessor` through `SPI.findOne(...)`,
4. runs processor generation for all configured entities,
5. runs a shared normalizer for upper-layer scaffold output.

It also:

- clears the previous schema file before generation,
- creates lock files under `generated/`,
- skips upper-layer generation for locked entities.

### What it generates

The shared Spring-side scaffold processors generate files such as:

- `*CrudController`
- `*CrudControllerV*`
- `I*ServiceV*`
- `I*ServiceV*Impl`

Those outputs come from processors such as:

- `GenProcessorController`
- `GenProcessorServiceV1`

and from FreeMarker templates such as:

- `controller-v1-interface.ftl`
- `controller-v1-impl.ftl`
- `service-v1-interface.ftl`
- `service-v1-impl.ftl`

### Minimal usage model

At code level, the typical flow is:

```java
SourceGenerator generator = new SourceGenerator(MyGenConfig.class);
generator.generate();
```

Use `generate()` when the task is full configured generation.

Important nuance:

`generate(Class<?> entity)` only runs the upper-layer normalizer path for one entity.
It does not rerun the whole lower-layer processor chain.

## 3. MyBatis-Plus Generator

Primary module:

- `r2mo-dbe-mybatisplus`

Primary SPI target:

- `io.r2mo.base.generator.GenProcessor`

Registered implementation:

- `io.r2mo.dbe.mybatisplus.generator.GenProcessorMybatisPlus`

### What it generates

`GenProcessorMybatisPlus` delegates to sub-processors for:

- SQL
- Mapper interface
- Mapper XML
- Service interface
- Service implementation

The scaffold layer and the DBE/MyBatis-Plus generator layer are related, but they are not the same thing.

### When to read this family

Read this family when the task is about:

- CRUD generator output,
- SQL schema generation,
- Mapper/XML generation,
- field rendering,
- or entity-driven service generation.

## 4. jOOQ Generator

Primary module:

- `r2mo-vertx-jooq-generate`

Primary classes:

- `JooqSourceConfigurer`
- `TypeOfJooq`
- `TypeOfJooqBase`

### How it works

`JooqSourceConfigurer` builds the jOOQ generator configuration and:

- resolves the database-specific jOOQ meta database class,
- builds `Jdbc`, `Generator`, `Target`, and `Strategy` sections,
- collects `ForcedType` entries through `SPI.findMany(TypeOfJooq.class)`.

This means jOOQ customization is extension-driven, not hardcoded in one class.

### How regex-based type mapping works

`TypeOfJooqBase` converts three kinds of declarations into jOOQ `ForcedType.includeExpression` values:

- `regexMeta()`
- `regexField()`
- `regexExpression()`

It then emits expressions such as:

```text
.*\.TABLE_NAME\.FIELD_NAME
```

This is the right extension seam when the task is:

- adding a new forced type,
- mapping custom user types,
- adding converter-based field rules,
- or extending jOOQ codegen behavior without patching the core configurer.

## 5. Extension Rules

### Change the generator, not the generated output

If the task says:

- `the generated controller is wrong`
- `the generated service name is wrong`
- `the Mapper XML shape should change`
- `a new jOOQ forced type is needed`

the likely fix belongs in:

- a generator processor,
- a template,
- a `GenConfig`/`GenMeta` decision,
- or a `TypeOfJooq` SPI implementation,

not in the generated file.

### Prefer SPI when a generator family already uses SPI

Examples:

- new CRUD generator backend -> implement and register `GenProcessor`
- new jOOQ type family -> implement and register `TypeOfJooq`

## 6. Fast Decision Table

| Task | Read first |
|---|---|
| change generated Spring controller/service scaffold | `r2mo-boot-spring` |
| change CRUD SQL/Mapper/XML/service generation | `r2mo-dbe-mybatisplus` |
| add or change jOOQ forced type mapping | `r2mo-vertx-jooq-generate` |
| understand why generated files are skipped | `SourceGenerator` lock behavior |
| choose a processor by generator name | `GenMeta.spi` plus `SPI.findOne(GenProcessor.class, ...)` |

## 7. Search Terms That Usually Work

Search these first:

- `SourceGenerator`
- `GenConfig`
- `GenMeta`
- `GenProcessor`
- `GenProcessorMybatisPlus`
- `JooqSourceConfigurer`
- `TypeOfJooq`
- `withForcedTypes`
- `generated/*.lock`

## 8. Final Rule

If the task is about generation, use this order:

```text
generator family -> processor/SPI entry -> template/config -> generated output
```

That order is the default final rule.

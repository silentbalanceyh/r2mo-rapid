package io.r2mo.vertx.jooq.generate.builder;

import io.r2mo.vertx.jooq.generate.VertxGenerator;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.jooq.codegen.JavaWriter;
import org.jooq.meta.SchemaDefinition;
import org.jooq.meta.TableDefinition;
import org.jooq.meta.TypedElementDefinition;
import org.jooq.tools.JooqLogger;
import org.jooq.tools.json.JSONArray;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

/**
 * A {@code VertxGenerator} that delegates some method calls to components.
 *
 * @author jensklingsporn
 */
class ComponentBasedVertxGenerator extends VertxGenerator {

    static final JooqLogger logger = JooqLogger.getLogger(ComponentBasedVertxGenerator.class);

    VertxGeneratorBuilder.APIType apiType;
    RenderQueryExecutorTypesComponent renderQueryExecutorTypesDelegate;
    Consumer<JavaWriter> writeDAOImportsDelegate;
    RenderQueryExecutorComponent renderQueryExecutorDelegate;
    RenderDAOInterfaceComponent renderDAOInterfaceDelegate;
    WriteConstructorComponent writeConstructorDelegate;
    Supplier<String> renderFQVertxNameDelegate;
    Supplier<String> renderDAOExtendsDelegate;
    Collection<OverwriteDAOComponent> overwriteDAODelegates = new ArrayList<>();
    Consumer<JavaWriter> writeDAOClassAnnotationDelegate = (w) -> {
    };
    Consumer<JavaWriter> writeDAOConstructorAnnotationDelegate = (w) -> {
    };
    Collection<BiFunction<SchemaDefinition, Function<File, JavaWriter>, JavaWriter>> writeExtraDataDelegates = new ArrayList<>();
    Collection<BiConsumer<JavaWriter, TableDefinition>> pojoClassAnnotationsDelegates = new ArrayList<>();

    NamedInjectionStrategy namedInjectionStrategy = PredefinedNamedInjectionStrategy.DISABLED;
    BuildOptions buildOptions = new BuildOptions();
    VertxGenerator activeGenerator = this;

    @Override
    public String renderFQVertxName() {
        return this.renderFQVertxNameDelegate.get();
    }

    @Override
    public String renderFindOneType(final String pType) {
        return this.renderQueryExecutorTypesDelegate.renderFindOneType(pType);
    }

    @Override
    public String renderFindManyType(final String pType) {
        return this.renderQueryExecutorTypesDelegate.renderFindManyType(pType);
    }

    @Override
    public String renderExecType() {
        return this.renderQueryExecutorTypesDelegate.renderExecType();
    }

    @Override
    public String renderInsertReturningType(final String tType) {
        return this.renderQueryExecutorTypesDelegate.renderInsertReturningType(tType);
    }

    @Override
    public String renderQueryExecutor(final String rType, final String pType, final String tType) {
        return this.renderQueryExecutorDelegate.renderQueryExecutor(rType, pType, tType);
    }

    @Override
    public String renderDAOInterface(final String rType, final String pType, final String tType) {
        return this.renderDAOInterfaceDelegate.renderDAOInterface(rType, pType, tType);
    }

    @Override
    public void writeDAOImports(final JavaWriter out) {
        this.writeDAOImportsDelegate.accept(out);
    }

    @Override
    public void writeDAOConstructor(final JavaWriter out, final String className, final String tableIdentifier, final String rType, final String pType, final String tType, final String schema) {
        this.writeConstructorDelegate.writeConstructor(out, className, tableIdentifier, rType, pType, tType, schema);
    }

    @Override
    public void overwriteDAOMethods(final SchemaDefinition schema, final JavaWriter out, final String className, final String tableIdentifier, final String rType, final String pType, final String tType) {
        this.overwriteDAODelegates.forEach(o -> o.overwrite(schema, out, className, tableIdentifier, rType, pType, tType));
    }

    @Override
    public String renderDaoExtendsClassName() {
        return this.renderDAOExtendsDelegate.get();
    }

    @Override
    public void writeDAOClassAnnotation(final JavaWriter out) {
        this.writeDAOClassAnnotationDelegate.accept(out);
    }

    @Override
    protected void writeDAOConstructorAnnotation(final JavaWriter out) {
        this.writeDAOConstructorAnnotationDelegate.accept(out);
    }

    @Override
    protected Collection<JavaWriter> writeExtraData(final SchemaDefinition definition, final Function<File, JavaWriter> writerGenerator) {
        return this.writeExtraDataDelegates.stream().map(d -> d.apply(definition, writerGenerator)).collect(Collectors.toList());
    }

    @Override
    protected boolean handleCustomTypeFromJson(final TypedElementDefinition<?> column, final String setter, final String columnType, final String javaMemberName, final JavaWriter out) {
        final boolean hasConverter = column.getType().getConverter() != null;
        final boolean hasBinding = column.getType().getBinding() != null;
        if (hasConverter || hasBinding) {
            final String instance = hasConverter ? column.getType().getConverter() : column.getType().getBinding();
            if (JsonObject.class.equals(this.tryGetPgConverterFromType(columnType, instance))) {
                out.tab(2).println("%s(%s.pgConverter().from(json.getJsonObject(\"%s\")));",
                    setter,
                    VertxGeneratorBuilder.resolveConverterInstance(instance, column.getSchema(), this),
                    javaMemberName);
                return true;
            } else if (JsonArray.class.equals(this.tryGetPgConverterFromType(columnType, instance))) {
                out.tab(2).println("%s(%s.pgConverter().from(json.getJsonArray(\"%s\")));",
                    setter,
                    VertxGeneratorBuilder.resolveConverterInstance(instance, column.getSchema(), this),
                    javaMemberName);
                return true;
            }
        }
        return false;
    }

    @Override
    protected boolean handleCustomTypeToJson(final TypedElementDefinition<?> column, final String getter, final String columnType, final String javaMemberName, final JavaWriter out) {
        final boolean hasConverter = column.getType().getConverter() != null;
        final boolean hasBinding = column.getType().getBinding() != null;
        if (hasConverter || hasBinding) {
            final String instance = hasConverter ? column.getType().getConverter() : column.getType().getBinding();
            final Class<?> pgConverterFromType = this.tryGetPgConverterFromType(columnType, instance);
            if (JsonObject.class.equals(pgConverterFromType) || JSONArray.class.equals(pgConverterFromType)) {
                out.tab(2).println("json.put(\"%s\",%s.pgConverter().to(%s()));",
                    this.getJsonKeyName(column),
                    VertxGeneratorBuilder.resolveConverterInstance(instance, column.getSchema(), this),
                    getter);
                return true;
            }
        }
        return false;
    }

    ComponentBasedVertxGenerator setWriteConstructorDelegate(final WriteConstructorComponent writeConstructorDelegate) {
        this.writeConstructorDelegate = writeConstructorDelegate;
        return this;
    }

    ComponentBasedVertxGenerator setRenderFQVertxNameDelegate(final Supplier<String> renderFQVertxNameDelegate) {
        this.renderFQVertxNameDelegate = renderFQVertxNameDelegate;
        return this;
    }

    ComponentBasedVertxGenerator setRenderDAOInterfaceDelegate(final RenderDAOInterfaceComponent renderDAOInterfaceDelegate) {
        this.renderDAOInterfaceDelegate = renderDAOInterfaceDelegate;
        return this;
    }

    ComponentBasedVertxGenerator setRenderQueryExecutorTypesDelegate(final RenderQueryExecutorTypesComponent renderQueryExecutorTypesDelegate) {
        this.renderQueryExecutorTypesDelegate = renderQueryExecutorTypesDelegate;
        return this;
    }

    ComponentBasedVertxGenerator setWriteDAOImportsDelegate(final Consumer<JavaWriter> writeDAOImportsDelegate) {
        this.writeDAOImportsDelegate = writeDAOImportsDelegate;
        return this;
    }

    ComponentBasedVertxGenerator setRenderQueryExecutorDelegate(final RenderQueryExecutorComponent renderQueryExecutorDelegate) {
        this.renderQueryExecutorDelegate = renderQueryExecutorDelegate;
        return this;
    }

    ComponentBasedVertxGenerator setApiType(final VertxGeneratorBuilder.APIType apiType) {
        this.apiType = apiType;
        return this;
    }

    ComponentBasedVertxGenerator addOverwriteDAODelegate(final OverwriteDAOComponent overwriteDelegate) {
        this.overwriteDAODelegates.add(overwriteDelegate);
        return this;
    }

    ComponentBasedVertxGenerator setRenderDAOExtendsDelegate(final Supplier<String> renderDAOExtendsDelegate) {
        this.renderDAOExtendsDelegate = renderDAOExtendsDelegate;
        return this;
    }

    ComponentBasedVertxGenerator setWriteDAOClassAnnotationDelegate(final Consumer<JavaWriter> writeDAOClassAnnotationDelegate) {
        this.writeDAOClassAnnotationDelegate = writeDAOClassAnnotationDelegate;
        return this;
    }

    ComponentBasedVertxGenerator setWriteDAOConstructorAnnotationDelegate(final Consumer<JavaWriter> writeDAOConstructorAnnotationDelegate) {
        this.writeDAOConstructorAnnotationDelegate = writeDAOConstructorAnnotationDelegate;
        return this;
    }

    ComponentBasedVertxGenerator addWriteExtraDataDelegate(final BiFunction<SchemaDefinition, Function<File, JavaWriter>, JavaWriter> writeExtraDataDelegate) {
        this.writeExtraDataDelegates.add(writeExtraDataDelegate);
        return this;
    }

    public ComponentBasedVertxGenerator setNamedInjectionStrategy(final NamedInjectionStrategy namedInjectionStrategy) {
        this.namedInjectionStrategy = namedInjectionStrategy;
        return this;
    }

    ComponentBasedVertxGenerator addGeneratePojoClassAnnotationDelegate(final BiConsumer<JavaWriter, TableDefinition> consumer) {
        this.pojoClassAnnotationsDelegates.add(consumer);
        return this;
    }

    /**
     *
     * @return The {@code VertxGenerator} that is actually used. When using a {@code ComponentBasedVertxGenerator} inside a {@code DelegatingVertxGenerator}
     * the {@code VertxGenerator}-methods accessed by the delegating components are referring to a generator that is not used.
     */
    public VertxGenerator getActiveGenerator() {
        return this.activeGenerator;
    }

    ComponentBasedVertxGenerator setActiveGenerator(final VertxGenerator activeGenerator) {
        this.activeGenerator = activeGenerator;
        return this;
    }

    final public File generateTargetFile(final SchemaDefinition schema, final String pkg, final String name) {
        final java.nio.file.Path p = java.nio.file.Paths.get(this.getActiveGenerator().getStrategy().getTargetDirectory())
            // Resolve the package
            .resolve((this.getActiveGenerator().getStrategy().getJavaPackageName(schema) + pkg).replaceAll("\\.", Matcher.quoteReplacement(File.separator))).toAbsolutePath();
        try {
            java.nio.file.Files.createDirectories(p);
        } catch (final java.io.IOException e) {
            throw new RuntimeException("Cannot create target file parent " + p, e);
        }
        return p.resolve(name).toFile();
    }

    @Override
    protected void generatePojoClassAnnotations(final JavaWriter out, final TableDefinition schema) {
        this.pojoClassAnnotationsDelegates.forEach(c -> c.accept(out, schema));
    }
}

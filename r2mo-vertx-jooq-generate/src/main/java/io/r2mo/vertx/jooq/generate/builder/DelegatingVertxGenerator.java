package io.r2mo.vertx.jooq.generate.builder;

import io.r2mo.vertx.jooq.generate.VertxGenerator;
import org.jooq.codegen.GeneratorStrategy;
import org.jooq.codegen.JavaWriter;
import org.jooq.meta.SchemaDefinition;
import org.jooq.meta.TableDefinition;
import org.jooq.meta.TypedElementDefinition;

import java.io.File;
import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * A {@code VertxGenerator} that delegates all methods to another {@code VertxGenerator}.
 *
 * @author jensklingsporn
 */
public class DelegatingVertxGenerator extends VertxGenerator {

    private final ComponentBasedVertxGenerator delegate;

    public DelegatingVertxGenerator(final ComponentBasedVertxGenerator delegate) {
        this.delegate = delegate;
        delegate.setActiveGenerator(this);
    }

    @Override
    protected boolean handleCustomTypeToJson(final TypedElementDefinition<?> column, final String getter, final String columnType, final String javaMemberName, final JavaWriter out) {
        return this.delegate.handleCustomTypeToJson(column, getter, columnType, javaMemberName, out);
    }

    @Override
    protected boolean handleCustomTypeFromJson(final TypedElementDefinition<?> column, final String setter, final String columnType, final String javaMemberName, final JavaWriter out) {
        return this.delegate.handleCustomTypeFromJson(column, setter, columnType, javaMemberName, out);
    }

    @Override
    protected String renderFindOneType(final String pType) {
        return this.delegate.renderFindOneType(pType);
    }

    @Override
    protected String renderFindManyType(final String pType) {
        return this.delegate.renderFindManyType(pType);
    }

    @Override
    protected String renderExecType() {
        return this.delegate.renderExecType();
    }

    @Override
    protected String renderInsertReturningType(final String tType) {
        return this.delegate.renderInsertReturningType(tType);
    }

    @Override
    protected String renderQueryExecutor(final String rType, final String pType, final String tType) {
        return this.delegate.renderQueryExecutor(rType, pType, tType);
    }

    @Override
    protected String renderDAOInterface(final String rType, final String pType, final String tType) {
        return this.delegate.renderDAOInterface(rType, pType, tType);
    }

    @Override
    protected void writeDAOImports(final JavaWriter out) {
        this.delegate.writeDAOImports(out);
    }

    @Override
    protected void writeDAOConstructor(final JavaWriter out, final String className, final String tableIdentifier, final String rType, final String pType, final String tType, final String schema) {
        this.delegate.writeDAOConstructor(out, className, tableIdentifier, rType, pType, tType, schema);
    }

    @Override
    protected void overwriteDAOMethods(final SchemaDefinition schema, final JavaWriter out, final String className, final String tableIdentifier, final String rType, final String pType, final String tType) {
        this.delegate.overwriteDAOMethods(schema, out, className, tableIdentifier, rType, pType, tType);
    }

    @Override
    protected String renderDaoExtendsClassName() {
        return this.delegate.renderDaoExtendsClassName();
    }

    @Override
    protected void writeDAOClassAnnotation(final JavaWriter out) {
        this.delegate.writeDAOClassAnnotation(out);
    }

    @Override
    protected void writeDAOConstructorAnnotation(final JavaWriter out) {
        this.delegate.writeDAOConstructorAnnotation(out);
    }

    @Override
    protected Collection<JavaWriter> writeExtraData(final SchemaDefinition definition, final Function<File, JavaWriter> writerGenerator) {
        return this.delegate.writeExtraDataDelegates.stream().map(d -> d.apply(definition, writerGenerator)).collect(Collectors.toList());
    }

    @Override
    public void setStrategy(final GeneratorStrategy strategy) {
        super.setStrategy(strategy);
        this.delegate.setStrategy(strategy);
    }

    @Override
    protected void generatePojoClassAnnotations(final JavaWriter out, final TableDefinition schema) {
        this.delegate.generatePojoClassAnnotations(out, schema);
    }
}


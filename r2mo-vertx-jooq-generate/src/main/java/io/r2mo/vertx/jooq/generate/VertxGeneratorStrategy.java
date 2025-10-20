package io.r2mo.vertx.jooq.generate;

import io.r2mo.vertx.jooq.shared.internal.VertxPojo;
import org.jooq.codegen.DefaultGeneratorStrategy;
import org.jooq.codegen.GeneratorStrategy;
import org.jooq.meta.Definition;
import org.jooq.meta.TypedElementDefinition;

import java.util.List;

/**
 * Created by jensklingsporn on 08.02.18.
 */
public class VertxGeneratorStrategy extends DefaultGeneratorStrategy {

    private final GeneratorStrategy delegate;

    public VertxGeneratorStrategy(final GeneratorStrategy delegate) {
        this.delegate = delegate;
    }

    public VertxGeneratorStrategy() {
        this(new DefaultGeneratorStrategy());
    }

    public String getJsonKeyName(final TypedElementDefinition<?> column) {
        return column.getName();
    }

    public String getRowMappersSubPackage() {
        return "mappers";
    }

    @Override
    public List<String> getJavaClassImplements(final Definition definition, final Mode mode) {
        final List<String> javaClassImplements = this.delegate.getJavaClassImplements(definition, mode);
        if (mode.equals(Mode.INTERFACE) || mode.equals(Mode.POJO) || mode.equals(Mode.RECORD)) {
            //let POJO and RECORD also implement VertxPojo to fix #37
            javaClassImplements.add(VertxPojo.class.getName());
        }
        return javaClassImplements;
    }

    @Override
    public boolean getInstanceFields() {
        return this.delegate.getInstanceFields();
    }

    @Override
    public void setInstanceFields(final boolean instanceFields) {
        this.delegate.setInstanceFields(instanceFields);
    }

    @Override
    public boolean getJavaBeansGettersAndSetters() {
        return this.delegate.getJavaBeansGettersAndSetters();
    }

    @Override
    public void setJavaBeansGettersAndSetters(final boolean javaBeansGettersAndSetters) {
        this.delegate.setJavaBeansGettersAndSetters(javaBeansGettersAndSetters);
    }

    @Override
    public String getTargetDirectory() {
        return this.delegate.getTargetDirectory();
    }

    @Override
    public void setTargetDirectory(final String directory) {
        this.delegate.setTargetDirectory(directory);
    }

    @Override
    public String getTargetPackage() {
        return this.delegate.getTargetPackage();
    }

    @Override
    public void setTargetPackage(final String packageName) {
        this.delegate.setTargetPackage(packageName);
    }

    @Override
    public String getFileHeader(final Definition definition, final Mode mode) {
        return this.delegate.getFileHeader(definition, mode);
    }

    @Override
    public String getJavaIdentifier(final Definition definition) {
        return this.delegate.getJavaIdentifier(definition);
    }

    @Override
    public String getJavaSetterName(final Definition definition, final Mode mode) {
        return this.delegate.getJavaSetterName(definition, mode);
    }

    @Override
    public String getJavaGetterName(final Definition definition, final Mode mode) {
        return this.delegate.getJavaGetterName(definition, mode);
    }

    @Override
    public String getJavaMethodName(final Definition definition, final Mode mode) {
        return this.delegate.getJavaMethodName(definition, mode);
    }

    @Override
    public String getJavaClassExtends(final Definition definition, final Mode mode) {
        return this.delegate.getJavaClassExtends(definition, mode);
    }

    @Override
    public String getJavaClassName(final Definition definition, final Mode mode) {
        return this.delegate.getJavaClassName(definition, mode);
    }

    @Override
    public String getJavaPackageName(final Definition definition, final Mode mode) {
        return this.delegate.getJavaPackageName(definition, mode);
    }

    @Override
    public String getJavaMemberName(final Definition definition, final Mode mode) {
        return this.delegate.getJavaMemberName(definition, mode);
    }

    @Override
    public String getOverloadSuffix(final Definition definition, final Mode mode, final String overloadIndex) {
        return this.delegate.getOverloadSuffix(definition, mode, overloadIndex);
    }

}

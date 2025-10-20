package io.r2mo.vertx.jooq.generate.builder;

import java.util.function.UnaryOperator;

public enum ConverterInstantiationMethod implements UnaryOperator<String> {

    NEW(converter -> String.format("new %s()", converter)),
    SINGLETON(converter -> converter.replaceAll("\\.", "_").toUpperCase() + "_INSTANCE");

    private final UnaryOperator<String> instanceResolver;

    ConverterInstantiationMethod(final UnaryOperator<String> instanceResolver) {
        this.instanceResolver = instanceResolver;
    }


    @Override
    public String apply(final String s) {
        return this.instanceResolver.apply(s);
    }
}

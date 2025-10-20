package io.r2mo.vertx.jooq.generate.builder;

import java.util.function.UnaryOperator;

/**
 * Some predefined {@code NamedInjectionStrategies}.
 *
 * @see NamedInjectionStrategy
 */
public enum PredefinedNamedInjectionStrategy implements NamedInjectionStrategy {
    /**
     * No named injection.
     */
    DISABLED(s -> ""),
    /**
     * Add {@code javax.inject.Named} annotations with the DAO's underlying schema as name.
     */
    SCHEMA(s -> String.format("@javax.inject.Named(\"%s\")", s.toUpperCase()));

    private final UnaryOperator<String> op;

    PredefinedNamedInjectionStrategy(final UnaryOperator<String> op) {
        this.op = op;
    }

    @Override
    public String apply(final String s) {
        return this.op.apply(s);
    }
}

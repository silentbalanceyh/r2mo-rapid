package io.r2mo.function;

/**
 * @author lang : 2025-09-02
 */
@FunctionalInterface
public interface Supplier<T> {

    T get() throws Throwable;
}

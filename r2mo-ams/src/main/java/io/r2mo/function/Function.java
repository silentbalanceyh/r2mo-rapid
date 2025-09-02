package io.r2mo.function;

/**
 * @author lang : 2025-09-02
 */
@FunctionalInterface
public interface Function<T, R> {
    R apply(T t) throws Throwable;
}

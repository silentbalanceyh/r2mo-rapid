package io.r2mo.function;

/**
 * @author lang : 2025-09-02
 */
@FunctionalInterface
public interface Predicate<T> {
    boolean test(T t) throws Throwable;
}

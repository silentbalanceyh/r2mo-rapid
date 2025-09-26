package io.r2mo.base.async;

/**
 * @author lang : 2025-09-26
 */
public interface HBox<T> {

    T data();

    <R extends HBox<T>> R data(T data);
}

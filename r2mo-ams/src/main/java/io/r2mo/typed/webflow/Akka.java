package io.r2mo.typed.webflow;

/**
 * 同异步兼容专用接口
 *
 * @param <T>
 */
public interface Akka<T> {
    T v();
}

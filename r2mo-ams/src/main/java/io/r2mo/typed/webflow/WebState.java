package io.r2mo.typed.webflow;

/**
 * HTTP 请求状态，封装所有可用的状态信息，统一调用，此处的封装取决于实现模块，如
 * <pre>
 *     1. Spring 实现时使用 org.springframework.http.HttpStatus 内置
 *     2. Vertx 实现时使用 io.vertx.core.http.HttpStatusCode 内置
 * </pre>
 * 如此这种结构才可用于抽象层，但必须保证状态最终形成一个池化常量，使用的时候才会出现
 *
 * @author lang : 2025-08-28
 */
public interface WebState {

    String name();

    int state();

    <T> T value();
}

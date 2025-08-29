package io.r2mo.typed.process;

/**
 * HTTP 请求状态，封装所有可用的状态信息，统一调用
 *
 * @author lang : 2025-08-28
 */
public interface WebState {

    String name();

    int state();

    <T extends Enum<T>> T value();
}

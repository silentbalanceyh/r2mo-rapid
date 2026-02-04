package io.r2mo.typed.webflow;

import io.r2mo.typed.exception.web._501NotSupportException;

/**
 * 同异步兼容专用接口
 *
 * @param <T>
 */
public interface Akka<T> {
    default T get() {
        throw new _501NotSupportException("[ R2MO ] 异步流程不支持同步 Sync 调用！");
    }

    default <V> V compose() {
        throw new _501NotSupportException("[ R2MO ] 同步流程不支持异步 Async 调用！");
    }
}

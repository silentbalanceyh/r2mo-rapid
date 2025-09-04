package io.r2mo.typed.webflow;

import jakarta.servlet.http.HttpServletRequest;

import java.io.Serializable;
import java.util.UUID;

/**
 * @author lang : 2025-08-28
 */
public interface WebRequest<T> extends Serializable {

    default T data() {
        return null;
    }

    default Serializable id() {
        return null;
    }

    /**
     * 确认 ID 没有发生任何变化，防止请求过程中 Body 的 id 和路径上的 id 不一致的情况
     *
     * @param id 路径上的 id
     */
    default void id(final Serializable id) {

    }

    default HttpServletRequest request() {
        return null;
    }

    UUID getAppId();

    UUID getTenantId();
}

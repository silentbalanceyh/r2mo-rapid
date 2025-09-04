package io.r2mo.typed.webflow;

import jakarta.servlet.http.HttpServletResponse;

import java.io.Serializable;

/**
 * @author lang : 2025-08-28
 */
public interface WebResponse<T> extends Serializable {

    @SuppressWarnings("all")
    default <E extends WebResponse<E>> E data(final T data) {
        return (E) this;
    }

    default HttpServletResponse response() {
        return null;
    }
}

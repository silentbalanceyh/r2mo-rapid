package io.r2mo.typed.webflow;

import java.io.Serializable;

/**
 * @author lang : 2025-08-28
 */
public interface WebResponse<T> extends Serializable {

    @SuppressWarnings("all")
    default <E extends WebResponse<E>> E data(final T data) {
        return (E) this;
    }
}

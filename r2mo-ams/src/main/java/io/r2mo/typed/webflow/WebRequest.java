package io.r2mo.typed.webflow;

import java.io.Serializable;

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
}

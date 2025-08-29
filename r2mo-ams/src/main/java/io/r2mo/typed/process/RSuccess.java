package io.r2mo.typed.process;

import lombok.Data;

import java.io.Serializable;

/**
 * @author lang : 2025-08-28
 */
@Data
class RSuccess<T> implements Serializable {

    private WebState state;

    private T data;

    RSuccess(final T data, final WebState state) {
        this.state = state;
        this.data = data;
    }
}

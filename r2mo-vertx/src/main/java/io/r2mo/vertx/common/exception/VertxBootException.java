package io.r2mo.vertx.common.exception;

import io.r2mo.typed.exception.JvmException;

/**
 * @author lang : 2025-09-26
 */
public class VertxBootException extends JvmException {
    private final VertxE error;

    public VertxBootException(final VertxE error,
                              final Object... args) {
        super(error.state(), error.message(), args);
        this.error = error;
    }

    @Override
    public int getCode() {
        return this.error.code();
    }
}

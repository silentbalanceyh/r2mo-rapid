package io.r2mo.spring.common.exception;

import io.r2mo.typed.exception.WebException;

/**
 * @author lang : 2025-09-24
 */
public class SpringException extends WebException {
    private final SpringE error;

    public SpringException(final SpringE error,
                           final Object... args) {
        super(error.state(), error.message(), args);
        this.error = error;
    }

    @Override
    public int getCode() {
        return error.code();
    }
}

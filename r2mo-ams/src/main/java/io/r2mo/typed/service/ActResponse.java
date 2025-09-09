package io.r2mo.typed.service;

import io.r2mo.typed.exception.AbstractException;
import io.r2mo.typed.exception.web._500ServerInternalException;

/**
 * @author lang : 2025-08-28
 */
public class ActResponse<T> {

    private final T data;
    private final ActState state;
    private final AbstractException error;

    private ActResponse(final T data, final ActState state) {
        this.data = data;
        this.state = state;
        this.error = null;
    }

    private ActResponse(final T data, final AbstractException error) {
        this.data = data;
        this.state = ActState.FAILURE;
        this.error = error;
    }

    public static <T> ActResponse<T> success(final T data, final ActState state) {
        return new ActResponse<>(data, state);
    }

    public static <T> ActResponse<T> success(final T data) {
        return new ActResponse<>(data, ActState.SUCCESS);
    }

    public static <T> ActResponse<T> success() {
        return new ActResponse<>(null, ActState.SUCCESS_204_NO_DATA);
    }

    public static <T> ActResponse<T> failure(final T data) {
        return new ActResponse<>(data, new _500ServerInternalException("Unknown error"));
    }

    public static <T> ActResponse<T> failure(final T data, final AbstractException error) {
        return new ActResponse<>(data, error);
    }

    public AbstractException error() {
        return this.error;
    }

    public T data() {
        return this.data;
    }

    public ActState state() {
        return this.state;
    }

    @Override
    public String toString() {
        return "ActResponse{" +
            "data=" + data +
            ", state=" + state +
            ", error=" + error +
            '}';
    }
}

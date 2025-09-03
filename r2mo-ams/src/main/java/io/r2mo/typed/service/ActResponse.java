package io.r2mo.typed.service;

/**
 * @author lang : 2025-08-28
 */
public record ActResponse<T>(T data, ActState state) {

    public static <T> ActResponse<T> success(final T data) {
        return new ActResponse<>(data, ActState.SUCCESS);
    }

    public static <T> ActResponse<T> success201(final T data) {
        return new ActResponse<>(data, ActState.SUCCESS_CREATED);
    }

    public static <T> ActResponse<T> success() {
        return new ActResponse<>(null, ActState.SUCCESS_NO_DATA);
    }

    public static <T> ActResponse<T> failure(final T data) {
        return new ActResponse<>(data, ActState.FAILURE);
    }
}

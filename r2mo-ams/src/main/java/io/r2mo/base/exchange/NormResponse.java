package io.r2mo.base.exchange;

import io.r2mo.typed.exception.WebException;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author lang : 2025-12-05
 */
class NormResponse implements UniResponse {
    private final boolean success;
    private final Object content;
    private final WebException error;
    private final Map<String, Object> meta = new HashMap<>();

    NormResponse(final Object content) {
        this.success = true;
        this.content = content;
        this.error = null;
    }

    NormResponse(final WebException ex) {
        Objects.requireNonNull(ex, "[ R2MO ] 失败模式下，异常对象不能为空！");
        this.success = false;
        this.content = null;
        this.error = ex;
    }

    @Override
    public String message() {
        return Objects.isNull(this.error) ? null : this.error.getMessage();
    }

    @Override
    public boolean isSuccess() {
        return this.success;
    }

    @Override
    public Object content() {
        return this.content;
    }

    @Override
    public UniResponse meta(final String key, final Object value) {
        this.meta.put(key, value);
        return this;
    }

    @Override
    public Object meta(final String key) {
        return this.meta.get(key);
    }
}

package io.r2mo.base.exchange;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lang : 2025-12-05
 */
public abstract class BaseContext implements UniContext {

    private final Map<String, Object> data = new HashMap<>();

    @Override
    public UniContext set(final String key, final Object value) {
        this.data.put(key, value);
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(final String key) {
        return (T) this.data.get(key);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getOrDefault(final String key, final T defaultValue) {
        return (T) this.data.getOrDefault(key, defaultValue);
    }

    public String getProtocol() {
        return this.get(KEY_PROTOCOL);
    }

    public boolean isSsl() {
        return this.get(KEY_SSL);
    }

    public int getTimeout() {
        return this.get(KEY_TIMEOUT);
    }
}

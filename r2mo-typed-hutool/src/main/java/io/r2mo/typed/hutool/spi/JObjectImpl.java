package io.r2mo.typed.hutool.spi;

import cn.hutool.json.JSONObject;
import io.r2mo.typed.json.JArray;
import io.r2mo.typed.json.JObject;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * @author lang : 2025-08-28
 */
class JObjectImpl implements JObject {
    private final JSONObject data;

    JObjectImpl() {
        this.data = new JSONObject();
    }

    JObjectImpl(final JSONObject data) {
        this.data = data;
    }

    JObjectImpl(final String jsonStr) {
        this.data = new JSONObject(jsonStr);
    }

    @Override
    public int getInt(final String key, final int defaultValue) {
        return this.data.getInt(key, defaultValue);
    }

    @Override
    public boolean getBool(final String key, final boolean defaultValue) {
        return this.data.getBool(key, defaultValue);
    }

    @Override
    public JObject getJObject(final String key) {
        return (JObject) this.data.getObj(key);
    }

    @Override
    public JArray getJArray(final String key) {
        return (JArray) this.data.getObj(key);
    }

    @Override
    public JObject put(final String key, final Object value) {
        this.data.set(key, value);
        return this;
    }

    @Override
    public boolean containsKey(final String key) {
        return this.data.containsKey(key);
    }

    @Override
    public Map<String, Object> toMap() {
        return new HashMap<>(this.data);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <V> void itKv(final BiConsumer<String, V> action) {
        this.data.forEach((k, v) -> action.accept(k, (V) v));
    }

    @Override
    public boolean isEmpty() {
        return this.data.isEmpty();
    }

    @Override
    public String encode() {
        return this.data.toString();
    }

    @Override
    public String encodePretty() {
        return this.data.toStringPretty();
    }

    @Override
    @SuppressWarnings("unchecked")
    public JSONObject data() {
        return this.data;
    }
}

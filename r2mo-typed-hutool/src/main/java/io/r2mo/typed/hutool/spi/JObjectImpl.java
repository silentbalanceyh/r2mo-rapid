package io.r2mo.typed.hutool.spi;

import cn.hutool.json.JSONObject;
import io.r2mo.spi.SPI;
import io.r2mo.typed.json.JArray;
import io.r2mo.typed.json.JBase;
import io.r2mo.typed.json.JObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

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
    public String getString(final String key, final String defaultValue) {
        return this.data.getStr(key, defaultValue);
    }

    @Override
    public Object get(final String key) {
        return this.data.getObj(key);
    }

    @Override
    public JObject getJObject(final String key) {
        return this.getJson(key);
    }

    private <T extends JBase> T getJson(final String key) {
        final Object value = this.get(key);
        if (Objects.isNull(value)) {
            return null;
        }
        return JUtilImpl.boxIn(value);
    }

    @Override
    public JArray getJArray(final String key) {
        return this.getJson(key);
    }

    @Override
    public JObject put(final String key, final Object value) {
        this.data.set(key, JUtilImpl.boxOut(value));
        return this;
    }

    @Override
    public JObject put(final Map<String, Object> map) {
        map.forEach(this::put);
        return this;
    }

    @Override
    public JObject remove(final String... keys) {
        Arrays.stream(keys).forEach(this::remove);
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
    public Stream<Map.Entry<String, Object>> itKv() {
        return this.data.entrySet().stream();
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
    public String encodeYaml() {
        return SPI.V_UTIL.toYaml(this);
    }

    @Override
    @SuppressWarnings("unchecked")
    public JSONObject data() {
        return this.data;
    }

    @Override
    @SuppressWarnings("unchecked")
    public JObject subset(final String... fields) {
        final JObject subset = new JObjectImpl();
        Arrays.stream(fields)
            .filter(this::containsKey)
            .forEach(field -> subset.put(field, this.get(field)));
        return subset;
    }

    @Override
    public String toString() {
        return this.encode();
    }
}

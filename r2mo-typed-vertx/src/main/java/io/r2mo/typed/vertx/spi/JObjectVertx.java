package io.r2mo.typed.vertx.spi;

import io.r2mo.function.Fn;
import io.r2mo.spi.SPI;
import io.r2mo.typed.json.JArray;
import io.r2mo.typed.json.JBase;
import io.r2mo.typed.json.JObject;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

/**
 * @author lang : 2025-09-25
 */
class JObjectVertx implements JObject {
    private final JsonObject data;

    JObjectVertx() {
        this.data = new JsonObject();
    }

    JObjectVertx(final JsonObject data) {
        this.data = data;
    }

    JObjectVertx(final String jsonStr) {
        this.data = new JsonObject(jsonStr);
    }

    @Override
    public int getInt(final String key, final int defaultValue) {
        final Object valueObj = this.data.getValue(key);
        if (Objects.isNull(valueObj)) {
            return defaultValue;
        }
        if (valueObj instanceof Number) {
            return ((Number) valueObj).intValue();
        }
        return Integer.parseInt(valueObj.toString());
    }

    @Override
    public long getLong(final String key, final long defaultValue) {
        final Object valueObj = this.data.getValue(key);
        if (Objects.isNull(valueObj)) {
            return defaultValue;
        }
        if (valueObj instanceof Number) {
            return ((Number) valueObj).longValue();
        }
        return Long.parseLong(valueObj.toString());
    }

    @Override
    public boolean getBool(final String key, final boolean defaultValue) {
        return this.data.getBoolean(key, defaultValue);
    }

    @Override
    public String getString(final String key, final String defaultValue) {
        return this.data.getString(key, defaultValue);
    }

    @Override
    public Object get(final String key) {
        final Object value = this.data.getValue(key);
        if (value instanceof JsonObject ||
            value instanceof JsonArray) {
            return JUtilVertx.boxIn(value);
        }
        return this.data.getValue(key);
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
        return JUtilVertx.boxIn(value);
    }

    @Override
    public JArray getJArray(final String key) {
        return this.getJson(key);
    }

    @Override
    public JObject put(final String key, final Object value) {
        Objects.requireNonNull(key, "[ R2MO ] JSON 键不能为空");
        this.data.put(key, JUtilVertx.boxOut(value));
        return this;
    }

    @Override
    public JObject remove(final String... keys) {
        Arrays.stream(keys).forEach(this.data::remove);
        return this;
    }

    @Override
    public boolean containsKey(final String key) {
        return this.data.containsKey(key);
    }

    @Override
    public Set<String> fieldNames() {
        return this.data.fieldNames();
    }

    @Override
    public Map<String, Object> toMap() {
        return this.data.getMap();
    }

    @Override
    public Stream<Map.Entry<String, Object>> itKv() {
        return this.data.stream();
    }

    @Override
    public boolean isEmpty() {
        return this.data.isEmpty();
    }

    @Override
    @SuppressWarnings("unchecked")
    public JsonObject data() {
        return this.data;
    }

    @Override
    @SuppressWarnings("unchecked")
    public JObject subset(final String... fields) {
        final JObject subset = new JObjectVertx();
        Arrays.stream(fields)
            .filter(this::containsKey)
            .forEach(field -> subset.put(field, this.get(field)));
        return subset;
    }

    @Override
    @SuppressWarnings("unchecked")
    public JObject copy() {
        return new JObjectVertx(Fn.jvmOr(() -> this.data.copy()));
    }

    @Override
    public String encode() {
        return this.data.encode();
    }

    @Override
    public String encodePretty() {
        return this.data.encodePrettily();
    }

    @Override
    public String encodeYaml() {
        return SPI.V_UTIL.toYaml(this);
    }

    @Override
    public String toString() {
        return this.encode();
    }
}

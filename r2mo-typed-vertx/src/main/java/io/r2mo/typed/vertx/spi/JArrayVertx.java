package io.r2mo.typed.vertx.spi;

import io.r2mo.function.Fn;
import io.r2mo.spi.SPI;
import io.r2mo.typed.json.JArray;
import io.r2mo.typed.json.JObject;
import io.vertx.core.json.JsonArray;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

/**
 * @author lang : 2025-09-25
 */
class JArrayVertx implements JArray {
    private final JsonArray data;

    JArrayVertx() {
        this.data = new JsonArray();
    }

    JArrayVertx(final JsonArray data) {
        this.data = data;
    }

    JArrayVertx(final String jsonStr) {
        this.data = new JsonArray(jsonStr);
    }

    @Override
    public Stream<String> itString() {
        return this.data.stream()
            .filter(item -> item instanceof String)
            .map(item -> (String) item);
    }

    @Override
    public Stream<JObject> itObject() {
        return this.data.stream()
            .map(JUtilVertx::boxIn)
            .filter(item -> item instanceof JObject)
            .map(item -> (JObject) item);
    }

    @Override
    public <T> JArray addAll(final Collection<T> values) {
        values.forEach(this::add);
        return this;
    }

    @Override
    public <T> JArray add(final T value) {
        this.data.add(value);
        return this;
    }

    @Override
    @SuppressWarnings("all")
    public List toList() {
        return this.data.stream().toList();
    }

    @Override
    public JObject findOne(final String field, final Object value) {
        return this.findInternal(field, value).findAny().orElse(null);
    }

    @Override
    public JArray findMany(final String field, final Object value) {
        final JArray foundList = new JArrayVertx();
        this.findInternal(field, value).forEach(foundList::add);
        return foundList;
    }

    private Stream<JObject> findInternal(final String field, final Object value) {
        return this.itObject()
            .filter(item -> value.equals(item.get(field)));
    }

    @Override
    public boolean isEmpty() {
        return this.data.isEmpty();
    }

    @Override
    @SuppressWarnings("all")
    public JsonArray data() {
        return this.data;
    }

    @Override
    @SuppressWarnings("unchecked")
    public JArray subset(final String... fields) {
        final JArray subset = new JArrayVertx();
        this.itObject().map(JObject::subset).forEach(subset::add);
        return subset;
    }

    @Override
    public String toString() {
        return this.encode();
    }

    @Override
    @SuppressWarnings("unchecked")
    public JArray copy() {
        return new JArrayVertx(Fn.jvmOr(() -> this.data.copy()));
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
}

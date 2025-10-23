package io.r2mo.typed.hutool.spi;

import cn.hutool.json.JSONArray;
import io.r2mo.function.Fn;
import io.r2mo.spi.SPI;
import io.r2mo.typed.json.JArray;
import io.r2mo.typed.json.JObject;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

/**
 * @author lang : 2025-08-28
 */
class JArrayImpl implements JArray {
    private final JSONArray data;

    JArrayImpl(final JSONArray jsonArray) {
        this.data = jsonArray;
    }

    JArrayImpl(final String jsonStr) {
        this.data = new JSONArray(jsonStr);
    }

    JArrayImpl() {
        this.data = new JSONArray();
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
            .map(JUtilImpl::boxIn)     // JSONObject -> JObject
            .filter(item -> item instanceof JObject)
            .map(item -> (JObject) item);
    }

    @Override
    public <T> JArray addAll(final Collection<T> values) {
        this.data.addAll(values);
        return this;
    }

    @Override
    public <T> JArray add(final T value) {
        this.data.add(JUtilImpl.boxOut(value));
        return this;
    }

    @Override
    public int size() {
        return this.data.size();
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
    @SuppressWarnings("all")
    public JSONArray data() {
        return this.data;
    }

    @Override
    @SuppressWarnings("all")
    public List toList() {
        return this.data.stream().toList();
    }

    @Override
    public String toString() {
        return this.encode();
    }

    @Override
    public JObject findOne(final String field, final Object value) {
        return this.findInternal(field, value).findAny().orElse(null);
    }

    @Override
    public JArray findMany(final String field, final Object value) {
        final JArray foundList = new JArrayImpl();
        this.findInternal(field, value).forEach(foundList::add);
        return foundList;
    }

    private Stream<JObject> findInternal(final String field, final Object value) {
        return this.itObject()
            .filter(item -> value.equals(item.get(field)));
    }

    @Override
    @SuppressWarnings("unchecked")
    public JArray subset(final String... fields) {
        final JArray subset = new JArrayImpl();
        this.itObject().map(JObject::subset).forEach(subset::add);
        return subset;
    }

    @Override
    @SuppressWarnings("unchecked")
    public JArray copy() {
        return new JArrayImpl((JSONArray) Fn.jvmOr(this.data::clone));
    }
}

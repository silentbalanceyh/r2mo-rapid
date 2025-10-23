package io.r2mo.typed.json;

import io.r2mo.spi.SPI;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author lang : 2025-08-27
 */
@SuppressWarnings("all")
public interface JArray extends JBase {

    Stream<String> itString();

    Stream<JObject> itObject();

    <T> JArray addAll(Collection<T> values);

    <T> JArray add(T value);

    List toList();

    int size();

    // ----------- 追加数组计算方法
    JObject findOne(String field, Object value);

    JArray findMany(String field, Object value);

    default <V> V mapOne(final String field) {
        final Set<V> valueSet = this.mapSet(field);
        if (1 < valueSet.size()) {
            throw new IllegalArgumentException("[ R2MO ] 本集合不符合调用条件( size = 0|1 ) / size = " + valueSet.size());
        }
        return valueSet.isEmpty() ? null : valueSet.iterator().next();
    }

    default <V> Set<V> mapSet(final String field) {
        return this.itObject()
            .map(item -> item.get(field))
            .filter(Objects::nonNull)
            .map(value -> (V) value)
            .collect(Collectors.toSet());
    }

    @SuppressWarnings("unchecked")
    default <K> Map<K, JArray> groupBy(final String field) {
        final Map<K, JArray> map = new HashMap<>();
        this.itObject().forEach(item -> {
            final K key = (K) item.get(field);
            if (Objects.nonNull(key)) {
                final JArray group = map.computeIfAbsent(key, k -> SPI.A());
                group.add(item);
                map.put(key, group);
            }
        });
        return map;
    }

    default <K> Map<K, JObject> mapBy(final String field) {
        final Map<K, JObject> map = new HashMap<>();
        this.itObject().forEach(item -> {
            final K key = (K) item.get(field);
            if (Objects.nonNull(key)) {
                map.put(key, item);
            }
        });
        return map;
    }
}

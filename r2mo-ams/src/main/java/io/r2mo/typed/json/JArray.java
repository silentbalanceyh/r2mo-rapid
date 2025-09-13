package io.r2mo.typed.json;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

    // ----------- 追加数组计算方法
    JObject findOne(String field, Object value);

    JArray findMany(String field, Object value);

    <V> V mapOne(String field);

    <V> Set<V> mapSet(String field);

    <K> Map<K, JArray> groupBy(String field);

    <K> Map<K, JObject> mapBy(String field);
}

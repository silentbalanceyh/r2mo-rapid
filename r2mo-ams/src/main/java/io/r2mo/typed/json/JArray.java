package io.r2mo.typed.json;

import java.util.Collection;
import java.util.List;
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
}

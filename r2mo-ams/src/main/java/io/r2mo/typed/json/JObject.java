package io.r2mo.typed.json;

import java.util.Map;
import java.util.function.BiConsumer;

/**
 * 统一封装底层结构，方便后续做多种实现
 *
 * @author lang : 2025-08-27
 */
public interface JObject extends JBase {

    int getInt(String key, int defaultValue);

    default int getInt(final String key) {
        return this.getInt(key, -1);
    }

    boolean getBool(String key, boolean defaultValue);

    default boolean getBool(final String key) {
        return this.getBool(key, false);
    }

    String getString(String key, String defaultValue);

    default String getString(String key) {
        return this.getString(key, null);
    }

    Object get(String key);

    JObject getJObject(String key);

    JArray getJArray(String key);

    JObject put(String key, Object value);

    JObject put(Map<String, Object> map);

    boolean containsKey(String key);

    Map<String, Object> toMap();

    <V> void itKv(BiConsumer<String, V> action);
}

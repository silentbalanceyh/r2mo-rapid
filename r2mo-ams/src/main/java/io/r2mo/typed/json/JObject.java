package io.r2mo.typed.json;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * 统一封装底层结构，方便后续做多种实现
 *
 * @author lang : 2025-08-27
 */
public interface JObject extends JBase {
    // 读取 int 值
    int getInt(String key, int defaultValue);

    default int getInt(final String key) {
        return this.getInt(key, -1);
    }

    // 读取 long 值
    long getLong(String key, long defaultValue);

    default long getLong(final String key) {
        return this.getLong(key, -1L);
    }

    // 读取 bool 值
    boolean getBool(String key, boolean defaultValue);

    default boolean getBool(final String key) {
        return this.getBool(key, false);
    }

    // 读取 String 值
    String getString(String key, String defaultValue);

    default String getString(final String key) {
        return this.getString(key, null);
    }

    // 读取 Object
    Object get(String key);

    // 读取 JObject
    JObject getJObject(String key);

    // 读取 JArray
    JArray getJArray(String key);

    /**
     * 此处的 put 方法，主要是为了方便链式调用，并且此处的 Object 一旦监测到 {@link JArray} 或 {@link JObject} 类型，就直接调用其
     * data() 方法提取底层数据结构进行存储，以防止递归过程中的封装问题，这是一种约定。为了兼容不同层面的 Json 数据结构，这种做法是必须的，
     * 毕竟不可以使用原生态的类型来继承统一类型
     *
     * @param key   键
     * @param value 值
     *
     * @return 当前对象
     */
    // 添加 key = value
    JObject put(String key, Object value);

    // 批量添加 key = value
    default JObject put(final Map<String, Object> map) {
        if (Objects.nonNull(map)) {
            map.forEach(this::put);
        }
        return this;
    }

    default JObject put(final JObject source) {
        return this.put(source.toMap());
    }

    JObject remove(String... keys);

    // 判断 key 属性是否存在
    boolean containsKey(String key);

    /**
     * 如果是通过转换，那此处的 toMap 一定是原生数据结构，Object 不应该包含{@link JArray} 和 {@link JObject} 两种类型，此处取决于在
     * put 过程中的一种承诺，即 put 进去的 value 只能是原生类型，不能是 {@link JArray} 和 {@link JObject}，否则无法做递归，最简单的
     * 模式是直接调用 {@link JObject#data()} 提取实际类型进行相关操作，最终执行都会归于内部结构。
     *
     * @return Map<String, Object>
     */
    // 转换为 Map
    Map<String, Object> toMap();

    // 迭代键值对
    Stream<Map.Entry<String, Object>> itKv();
}

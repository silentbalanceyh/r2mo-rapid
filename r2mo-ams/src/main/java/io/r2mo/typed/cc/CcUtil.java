package io.r2mo.typed.cc;

import io.r2mo.spi.SPI;
import io.r2mo.typed.json.JArray;
import io.r2mo.typed.json.JObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;

/**
 * @author lang : 2025-08-27
 */
class CcUtil {

    private static final ConcurrentMap<String, Set<String>> KEY_MAP = new ConcurrentHashMap<>();

    static <K, V> JArray momThread(final ConcurrentMap<String, V> store) {
        // 1. 准备返回数组
        final JArray memoryA = SPI.A();

        // [Debug] 先看一眼总数，如果这里打印 0，说明传入的 map 本身就是空的
        if (store == null || store.isEmpty()) {
            return memoryA;
        }

        // 2. 中间聚合容器：Map<盐值, Map<组件类型名, 数量>>
        // 使用 String 作为类型 Key，避免 Class 对象因代理/加载器不同导致 equals 失败
        final Map<String, Map<String, Integer>> aggregator = new HashMap<>();

        store.forEach((keyStr, component) -> {
            try {
                // [安全检查] 忽略空值
                if (component == null) {
                    return;
                }
                // [安全检查] 防止 key 不是 String (泛型擦除可能导致 raw type 混入)
                if (keyStr == null) {
                    return;
                }

                // --- A. 解析盐值 ---
                // 逻辑：截取 @ 之后的内容。如果没有 @，则归类为 "default" 或直接用 key
                final int idx = keyStr.lastIndexOf('@');
                final String salt = (idx > -1) ? keyStr.substring(idx + 1) : "default";

                // --- B. 解析类型 ---
                // 建议：如果不需要包含包名，用 getSimpleName()；如果怕重名，用 getName()
                // 进阶：如果遇到 CGLIB/Spring 代理类，这里可能需要清理类名 (如 split("$$")[0])
                final String typeName = component.getClass().getName();

                // --- C. 聚合计数 ---
                aggregator
                    .computeIfAbsent(salt, k -> new HashMap<>())
                    .merge(typeName, 1, Integer::sum);

            } catch (final Exception e) {
                // [Debug] 打印异常，防止静默失败
                System.err.println("[R2MO-ERROR] Error processing key: " + keyStr);
                e.printStackTrace();
            }
        });

        // 3. 组装结果
        // 只要 aggregator 里有数据，这里就一定会有输出
        aggregator.forEach((salt, typeMap) -> {
            typeMap.forEach((typeName, size) -> {
                final JObject memory = SPI.J();
                memory.put("key", salt);      // 盐值 (您提到的类型标识)
                memory.put("type", typeName); // 组件类名
                memory.put("size", size);     // 线程数
                memory.put("thread", true);

                memoryA.add(memory);
            });
        });

        return memoryA;
    }

    static <K, V> JObject mom(final ConcurrentMap<K, V> store) {
        final JObject memory = SPI.J();
        store.forEach((key, component) -> {
            memory.put("key", key);
            memory.put("type", component.getClass().getName());
            memory.put("hash", component.hashCode());
        });
        return memory;
    }

    static <V> V poolThread(final ConcurrentMap<String, V> pool, final Supplier<V> poolFn) {
        return poolThread(pool, poolFn, null);
    }

    static <V> V poolThread(final ConcurrentMap<String, V> pool, final Supplier<V> poolFn, final String marker) {
        final String keyOf = keyOf(marker);
        return pool(pool, keyOf, poolFn);
    }

    /**
     * 提取当前线程所有的 key set 集合
     *
     * @return key set 集合
     */
    static Set<String> keySet() {
        final String threadName = Thread.currentThread().getName();
        return KEY_MAP.getOrDefault(threadName, Set.of());
    }

    /**
     * 生成线程级的 key 值
     * <pre>
     *      1. marker = null or blank -> key       = thread "name"
     *      2. marker = not blank -> key           = thread "name@marker"
     *      说明：当 marker 不为空时，表示当前线程下有多个不同
     * </pre>
     *
     * @param marker 标记
     *
     * @return 线程级 key
     */
    static String keyOf(final String marker) {
        final String threadName = Thread.currentThread().getName();
        final String keyOf;
        if (marker == null || marker.isBlank()) {
            keyOf = threadName;
        } else {
            keyOf = threadName.concat("@").concat(marker);
            // 在线程的 Map 中追加 thread name = Set<String> 的记录
            KEY_MAP.computeIfAbsent(threadName, k -> ConcurrentHashMap.newKeySet()).add(marker);
        }
        return keyOf;
    }

    static <K, V> V pool(final ConcurrentMap<K, V> pool, final K key, final Supplier<V> poolFn) {
        Objects.requireNonNull(pool, "[ R2MO ] pool 参数不可为空！");
        Objects.requireNonNull(key, "[ R2MO ] key 参数不可为空！");
        Objects.requireNonNull(poolFn, "[ R2MO ] poolFn 参数不可为空！");

        V value = pool.get(key);
        if (Objects.isNull(value)) {
            value = poolFn.get();
            if (Objects.nonNull(value)) {
                pool.put(key, value);
            }
        }
        return value;
    }
}

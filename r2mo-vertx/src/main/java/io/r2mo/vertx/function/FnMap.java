package io.r2mo.vertx.function;

import io.vertx.core.Future;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BinaryOperator;

/**
 * @author lang : 2026-01-02
 */
class FnMap {
    private FnMap() {
    }

    @SuppressWarnings("all")
    static <K, T> Future<ConcurrentMap<K, T>> combineM(final ConcurrentMap<K, Future<T>> futureMap) {
        // 1. 快速路径：如果是空 Map，直接返回空结果，避免后续对象创建
        if (futureMap == null || futureMap.isEmpty()) {
            return Future.succeededFuture(new ConcurrentHashMap<>());
        }

        // 2. 预分配结果容器（ConcurrentHashMap 线程安全，可以直接在异步线程写入）
        // 设置初始容量避免扩容，LoadFactor 设为 1.0 因为大小是固定的
        final ConcurrentMap<K, T> resultMap = new ConcurrentHashMap<>(futureMap.size());
        final List<Future<?>> futures = new ArrayList<>(futureMap.size());

        // 3. 转换 Future：每个 Future 成功后，自动将结果 Put 到 resultMap 中
        futureMap.forEach((key, future) -> {
            // 使用 map 而不是 onSuccess，确保返回一个新的 Future 参与 join
            // 这样即使 put 操作本身报错也能被捕获（虽然几率很小）
            if (key != null && future != null) {
                futures.add(future.map(value -> {
                    if (value != null) {
                        resultMap.put(key, value);
                    }
                    return null; // 返回 Void，我们只关心副作用
                }));
            }
        });

        // 4. 等待所有操作完成
        // 使用 Future.join 等待所有任务结束（无论成功失败，join 都会等待所有）
        // 如果其中有一个失败，整体结果为 failed，触发 otherwise
        return Future.join(futures)
            .map(resultMap) // 直接返回已经填充好的 resultMap
            .otherwise(FnVertx.otherwiseFn(ConcurrentHashMap::new));
    }

    /*
     * List<Future<Map<String,Tool>>> futures ->
     *      Future<Map<String,Tool>>
     * Exchange data by key here.
     *      The binary operator should ( Tool, Tool ) -> Tool
     */
    static <T> Future<ConcurrentMap<String, T>> compressM(
        final List<Future<ConcurrentMap<String, T>>> futures,
        final BinaryOperator<T> binaryOperator
    ) {
        /* thenResponse */
        return Future.join(new ArrayList<>(futures)).compose(finished -> {
            final ConcurrentMap<String, T> resultMap = new ConcurrentHashMap<>();
            if (Objects.nonNull(finished)) {
                final List<ConcurrentMap<String, T>> result = finished.list();

                final BinaryOperator<T> mergeOperator = Objects.isNull(binaryOperator) ?
                    /*
                     * Default set merged function to
                     * latest replace original Tool in result map
                     * For other situation, the system should call binaryOperator
                     * to merge (Tool, Tool) -> Tool
                     * 1) JsonArray
                     * 2) List<Tool>
                     * 3) Others
                     *
                     * */
                    (original, latest) -> latest : binaryOperator;
                /*
                 * List<ConcurrentMap<String,Tool>> result ->
                 *      ConcurrentMap<String,Tool>
                 */
                result.stream().filter(Objects::nonNull).forEach(each -> each.keySet()
                    .stream().filter(key -> Objects.nonNull(each.get(key))).forEach(key -> {
                        final T combined;
                        if (resultMap.containsKey(key)) {
                            /*
                             * Merged key -> findRunning to result
                             */
                            final T original = resultMap.get(key);
                            final T latest = each.get(key);
                            combined = mergeOperator.apply(original, latest);
                        } else {
                            /*
                             * Extract combined
                             */
                            combined = each.get(key);
                        }
                        resultMap.put(key, combined);
                    }));
            }
            return Future.succeededFuture(resultMap);
        }).otherwise(FnVertx.otherwiseFn(ConcurrentHashMap::new));
    }
}

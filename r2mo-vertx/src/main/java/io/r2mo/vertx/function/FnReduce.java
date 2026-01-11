package io.r2mo.vertx.function;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

class FnReduce {
    static Future<JsonArray> compressA(final List<Future<JsonArray>> futures) {
        final List<Future<?>> futureList = new ArrayList<>(futures);
        return Future.join(futureList).compose(finished -> {
            final JsonArray resultMap = new JsonArray();

            // 无值中断
            if (Objects.isNull(finished) || Objects.isNull(finished.list())) {
                return Future.succeededFuture(resultMap);
            }

            // 有值计算
            final int size = finished.list().size();
            for (int index = 0; index < size; index++) {
                final Object item = finished.list().get(index);
                if (item instanceof JsonArray) {
                    resultMap.addAll((JsonArray) item);
                }
            }
            return Future.succeededFuture(resultMap);
        }).otherwise(FnOut.otherwiseFn(JsonArray::new));
    }

    @SuppressWarnings("unchecked")
    static <T> Future<List<T>> compressL(final List<Future<List<T>>> futures) {
        final List<Future<?>> futureList = new ArrayList<>(futures);
        return Future.join(futureList).compose(finished -> {
            final List<T> result = new ArrayList<>();


            // 无值中断
            if (Objects.isNull(finished) || Objects.isNull(finished.list())) {
                return Future.succeededFuture(result);
            }


            // 有值计算
            final int size = finished.list().size();
            for (int index = 0; index < size; index++) {
                final Object item = finished.list().get(index);
                if (item instanceof List) {
                    final List<T> grouped = (List<T>) item;
                    if (!grouped.isEmpty()) {
                        result.addAll(grouped);
                    }
                }
            }
            return Future.succeededFuture(result);
        }).otherwise(FnOut.otherwiseFn(ArrayList::new));
    }
}

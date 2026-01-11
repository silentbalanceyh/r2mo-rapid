package io.r2mo.vertx.function;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;

@Slf4j
class FnJObject {

    @SafeVarargs
    static Future<JsonObject> combineJ(
        final Future<JsonObject> source, final Function<JsonObject, List<Future<?>>> generateFun,
        final BiConsumer<JsonObject, JsonObject>... operatorFun
    ) {
        return source.compose(first -> Future.join(generateFun.apply(first)).compose(finished -> {
            if (Objects.nonNull(finished) && Objects.nonNull(finished.list())) {
                final List<JsonObject> secondary = finished.list();
                // Zipper Operation, the base list is first
                final int size = secondary.size();
                for (int index = 0; index < size; index++) {
                    final JsonObject item = secondary.get(index);
                    operatorFun[index].accept(first, item);
                }
            }
            return Future.succeededFuture(first);
        })).otherwise(FnOut.otherwiseFn(JsonObject::new));
    }

    @SafeVarargs
    static Future<JsonObject> combineJ(final Future<JsonObject>... futures) {
        return Future.join(Arrays.asList(futures)).compose(finished -> {
            final JsonObject resultMap = new JsonObject();
            if (Objects.isNull(finished) || Objects.isNull(finished.list())) {
                return Future.succeededFuture(resultMap);
            }
            final int size = finished.list().size();
            for (int index = 0; index < size; index++) {
                final JsonObject item = (JsonObject) finished.list().get(index);
                if (Objects.isNull(item)) {
                    continue;
                }
                resultMap.put(String.valueOf(index), item);
            }
            return Future.succeededFuture(resultMap);
        }).otherwise(FnOut.otherwiseFn(JsonObject::new));
    }

    static <J, A> Future<JsonObject> combineJ(
        final JsonObject input, final String field,
        final Function<JsonObject, Future<J>> itemFnJ, final Function<JsonArray, Future<A>> itemFnA) {
        final Object value = input.getValue(field);
        if (value instanceof final JsonArray valueA) {
            // 提取参数为 JsonArray
            return itemFnA.apply(valueA).compose(processed -> {
                input.put(field, processed);
                return Future.succeededFuture(input);
            });
        } else if (value instanceof final JsonObject valueJ) {
            // 提取参数为 JsonObject
            return itemFnJ.apply(valueJ).compose(processed -> {
                input.put(field, processed);
                return Future.succeededFuture(input);
            });
        } else {
            // 什么都不做
            log.warn("[ ZERO ] 输入数值既不是 JsonObject 也不是 JsonArray，无法处理：{}", value);
            return Future.succeededFuture(input);
        }
    }

}

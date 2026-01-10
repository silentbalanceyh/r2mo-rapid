package io.r2mo.vertx.function;

import io.r2mo.base.util.R2MO;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BinaryOperator;
import java.util.function.Function;

@Slf4j
class FnJArray {

    @SuppressWarnings("all")
    static Future<JsonArray> combineA(final Future<JsonArray> source,
                                      final Function<JsonObject, Future<JsonObject>> generateOf, final BinaryOperator<JsonObject> combinerOf) {
        return source.compose(first -> {
            // 并行异步
            final List<Future<?>> secondFutures = new ArrayList<>();
            first.stream()
                .filter(item -> item instanceof JsonObject)
                .map(item -> (JsonObject) item)
                .map(generateOf::apply)
                .forEach(secondFutures::add);
            // 组合结果
            return Future.join(secondFutures).compose(finished -> {
                final List<JsonObject> secondary = finished.list();
                // 拉平后执行组合
                final List<JsonObject> completed = R2MO.elementZip(first.getList(), secondary, combinerOf);
                return Future.succeededFuture(new JsonArray(completed));
            }).otherwise(FnOut.otherwiseFn(JsonArray::new));
        }).otherwise(FnOut.otherwiseFn(JsonArray::new));
    }

    static Future<JsonArray> combineA(final JsonArray input,
                                      final Function<JsonObject, Future<JsonObject>> generateOf) {
        final List<Future<JsonObject>> futures = new ArrayList<>();
        input.stream()
            .filter(Objects::nonNull)
            .filter(item -> item instanceof JsonObject)
            .map(item -> (JsonObject) item)
            .map(generateOf)
            .forEach(futures::add);
        return combineA(futures);
    }

    static Future<JsonArray> combineA(final List<Future<JsonObject>> futures) {
        return Future.join(new ArrayList<>(futures)).compose(finished -> {
            final JsonArray result = Objects.isNull(finished)
                ? new JsonArray() : new JsonArray(finished.list());
            return Future.succeededFuture(result);
        }).otherwise(FnOut.otherwiseFn(JsonArray::new));
    }
}

package io.r2mo.vertx.jooq;

import io.r2mo.function.Fn;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.Objects;

/**
 * @author lang : 2025-10-27
 */
public class DBGap {

    public static JsonArray outputA(final JsonArray outputA) {
        Objects.requireNonNull(outputA, "[ R2MO ] ( Out ) 输出数组不能为空");
        final JsonArray result = new JsonArray();
        outputA.stream().filter(item -> item instanceof JsonObject)
            .map(item -> (JsonObject) item)
            .map(DBGap::outputJ)
            .forEach(result::add);
        return result;
    }

    public static JsonObject outputJ(final JsonObject outputJ) {
        Objects.requireNonNull(outputJ, "[ R2MO ] ( Out ) 输出对象不能为空");
        final JsonObject converted = new JsonObject();
        for (final String key : outputJ.fieldNames()) {
            final Object value = outputJ.getValue(key);
            if (value instanceof final String valueS) {
                final Object replaced;
                if (valueS.startsWith("{") && valueS.endsWith("}")) {
                    replaced = Fn.jvmOr(() -> new JsonObject(valueS));
                } else if (valueS.startsWith("[") && valueS.endsWith("]")) {
                    replaced = Fn.jvmOr(() -> new JsonArray(valueS));
                } else {
                    replaced = null;
                }
                if (Objects.nonNull(replaced)) {
                    converted.put(key, replaced);
                }
            }
        }
        if (converted.isEmpty()) {
            return outputJ;
        }
        outputJ.mergeIn(converted, true);
        return outputJ;
    }

    public static JsonArray inputA(final JsonArray inputA) {
        Objects.requireNonNull(inputA, "[ R2MO ] ( In ) 输入数组不能为空");
        final JsonArray result = new JsonArray();
        inputA.stream().filter(item -> item instanceof JsonObject)
            .map(item -> (JsonObject) item)
            .map(DBGap::inputJ)
            .forEach(result::add);
        return result;
    }

    public static JsonObject inputJ(final JsonObject inputJ) {
        Objects.requireNonNull(inputJ, "[ R2MO ] ( In ) 输入对象不能为空");
        final JsonObject converted = new JsonObject();
        for (final String key : inputJ.fieldNames()) {
            final Object value = inputJ.getValue(key);
            if (value instanceof final JsonObject valueJ) {
                converted.put(key, valueJ.encode());
            } else if (value instanceof final JsonArray valueA) {
                converted.put(key, valueA.encode());
            }
        }
        if (converted.isEmpty()) {
            return inputJ;
        }
        inputJ.mergeIn(converted, true);
        return inputJ;
    }
}

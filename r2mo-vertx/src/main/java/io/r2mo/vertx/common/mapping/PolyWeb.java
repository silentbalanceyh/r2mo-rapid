package io.r2mo.vertx.common.mapping;

import io.r2mo.base.program.R2Vector;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.Objects;

/**
 * @author lang : 2025-10-21
 */
class PolyWeb extends PolyBase<JsonObject, JsonArray> {

    PolyWeb(final R2Vector vector, final Class<?> entityCls) {
        super(vector, entityCls);
    }

    @Override
    public PolyPhase phase() {
        return PolyPhase.INPUT;
    }

    @Override
    public JsonObject mapOne(final JsonObject serialized) {
        final JsonObject mapped = new JsonObject();
        this.vector.mapBy((fieldJson, field) -> {
            final Object value = serialized.getValue(fieldJson);
            if (Objects.nonNull(value)) {
                mapped.put(field, value);
            }
        });
        return mapped;
    }

    @Override
    public JsonArray mapMany(final JsonArray array) {
        final JsonArray mapped = new JsonArray();
        array.stream()
            .map(item -> (JsonObject) item)
            .map(this::mapOne)
            .forEach(mapped::add);
        return mapped;
    }
}

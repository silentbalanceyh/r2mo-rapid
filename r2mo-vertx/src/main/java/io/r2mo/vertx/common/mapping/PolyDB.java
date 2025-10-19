package io.r2mo.vertx.common.mapping;

import io.r2mo.base.program.R2Vector;
import io.r2mo.base.util.R2MO;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.List;
import java.util.Objects;

/**
 * @author lang : 2025-10-19
 */
class PolyDB<E> extends PolyBase<E, List<E>> {

    PolyDB(final R2Vector vector, final Class<?> entityCls) {
        super(vector, entityCls);
    }

    @Override
    public PolyPhase phase() {
        return PolyPhase.OUTPUT;
    }

    @Override
    public JsonObject mapOne(final E input) {
        final JsonObject serialized = R2MO.serializeJ(input);
        if (Objects.isNull(this.vector) || !this.vector.hasMapping()) {
            return serialized;
        }

        
        final JsonObject mapped = new JsonObject();
        this.vector.mapBy((fieldJson, field) -> {
            final Object value = serialized.getValue(field);
            if (Objects.isNull(value)) {
                mapped.putNull(fieldJson);
            } else {
                mapped.put(fieldJson, value);
            }
        });
        return mapped;
    }

    @Override
    public JsonArray mapMany(final List<E> input) {
        final JsonArray mapped = new JsonArray();
        input.stream().map(this::mapOne).forEach(mapped::add);
        return mapped;
    }
}

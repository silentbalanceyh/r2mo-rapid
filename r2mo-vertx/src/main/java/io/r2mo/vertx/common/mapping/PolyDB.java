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

    /**
     * 🔄 将实体对象映射为 {@link JsonObject}
     * <p>
     * ⚡️ 空值优化：为空的时候不做任何处理，减少网络带宽
     * 🎯 字段映射：根据向量配置进行字段名称转换
     * 📦 数据压缩：只包含非空字段，优化传输效率
     *
     * @param input 📥 待映射的实体对象
     *
     * @return 📤 映射后的 {@link JsonObject}
     * @since 💡 1.0.0
     */
    @Override
    public JsonObject mapOne(final E input) {
        final JsonObject serialized = R2MO.serializeJ(input);
        if (Objects.isNull(this.vector) || !this.vector.hasMapping()) {
            return serialized;
        }


        final JsonObject mapped = new JsonObject();
        this.vector.mapBy((fieldJson, field) -> {
            final Object value = serialized.getValue(field);
            if (Objects.nonNull(value)) {
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

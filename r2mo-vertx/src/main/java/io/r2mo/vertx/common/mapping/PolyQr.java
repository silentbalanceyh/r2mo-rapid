package io.r2mo.vertx.common.mapping;

import io.r2mo.base.dbe.constant.QCV;
import io.r2mo.base.program.R2Vector;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.Locale;
import java.util.Objects;

/**
 * @author lang : 2025-10-21
 */
class PolyQr implements Poly.Qr {
    private final Class<?> entityCls;
    private final R2Vector vector;

    PolyQr(final R2Vector vector, final Class<?> entityCls) {
        this.vector = vector;
        this.entityCls = entityCls;
    }

    @Override
    public JsonObject map(final JsonObject query) {
        if (Objects.isNull(this.vector) || Objects.isNull(query)) {
            return query;
        }
        if (query.containsKey(QCV.P_CRITERIA)) {
            // criteria
            query.put(QCV.P_CRITERIA, this.mapCriteria(query.getJsonObject(QCV.P_CRITERIA)));
        }
        if (query.containsKey(QCV.P_SORTER)) {
            // sorter
            query.put(QCV.P_SORTER, this.mapSort(query.getJsonArray(QCV.P_SORTER)));
        }
        if (query.containsKey(QCV.P_PROJECTION)) {
            // projection
            query.put(QCV.P_PROJECTION, this.mapProjection(query.getJsonArray(QCV.P_PROJECTION)));
        }
        return query;
    }

    @Override
    public JsonObject mapCriteria(final JsonObject tree) {
        if (Objects.isNull(this.vector) || Objects.isNull(tree)) {
            return tree;
        }
        return this.mapTreeInternal(tree);
    }

    private JsonObject mapTreeInternal(final JsonObject tree) {
        final JsonObject condition = new JsonObject();

        for (final String rawKey : tree.fieldNames()) {
            final Object rawVal = tree.getValue(rawKey);

            // 解析 key: "field,op" 或 "field"
            final String[] parts = rawKey.split(",", 2);
            final String rawField = parts[0].trim();
            final String mapped = this.vector.mapBy(rawField);
            final String finalField = (mapped == null || mapped.isBlank()) ? rawField : mapped;
            final String newKey = (parts.length == 2) ? (finalField + "," + parts[1]) : finalField;

            if (rawVal instanceof final JsonObject jo) {
                condition.put(newKey, this.mapTreeInternal(jo)); // 仅对象递归
            } else {
                condition.put(newKey, rawVal);                   // 数组 & 标量原样保留
            }
        }
        return condition;
    }

    @Override
    public JsonArray mapSort(final JsonArray sorter) {
        if (Objects.isNull(this.vector) || Objects.isNull(sorter)) {
            return sorter;
        }
        final JsonArray array = new JsonArray();
        sorter.stream()
            .filter(Objects::nonNull)
            .map(Object::toString)
            .map(item -> item.split(","))
            .filter(items -> 0 < items.length && items.length <= 2)
            .forEach(splits -> {
                final String fieldJson = splits[0];
                final String field = this.vector.mapBy(fieldJson.trim());
                final String mark = 1 == splits.length ? "ASC" : splits[1].trim().toUpperCase(Locale.ROOT);
                array.add(field + "," + mark);
            });
        return array;
    }

    @Override
    public JsonArray mapProjection(final JsonArray projection) {
        if (Objects.isNull(this.vector) || Objects.isNull(projection)) {
            return projection;
        }
        final JsonArray array = new JsonArray();
        projection.stream()
            .filter(Objects::nonNull)
            .map(Object::toString)
            .map(this.vector::mapBy)
            .filter(Objects::nonNull)
            .forEach(array::add);
        return array;
    }
}

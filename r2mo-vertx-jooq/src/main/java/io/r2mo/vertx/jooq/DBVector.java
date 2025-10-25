package io.r2mo.vertx.jooq;

import io.r2mo.base.program.R2Vector;
import io.r2mo.base.util.R2MO;
import io.r2mo.typed.cc.Cc;
import io.r2mo.vertx.common.mapping.Poly;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lang : 2025-10-19
 */
class DBVector<T> {
    private static final Cc<String, DBVector<?>> CC_SKELETON = Cc.openThread();
    private final R2Vector vector;
    private final Class<T> entityCls;

    private DBVector(final R2Vector vector, final Class<T> entityCls) {
        this.vector = vector;
        this.entityCls = entityCls;
    }

    @SuppressWarnings("unchecked")
    static <T> DBVector<T> of(final AsyncMeta metadata) {
        final String cached = metadata.metaEntity() + "/" + metadata.metaVector();
        return (DBVector<T>) CC_SKELETON.pick(() -> new DBVector<>(metadata.metaVector(), metadata.metaEntity()), cached);
    }

    // 同步转同步
    <E> JsonObject one(final E entity) {
        return Poly.<E>ofDB(this.entityCls, this.vector).mapOne(entity);
    }

    @SuppressWarnings("unchecked")
    <E> E one(final JsonObject entityJ) {
        return (E) R2MO.deserializeJ(Poly.ofWeb(this.entityCls, this.vector).mapOne(entityJ), this.entityCls);
    }

    // 同步转异步
    <E> Future<JsonObject> oneTo(final E entity) {
        return Future.succeededFuture(this.one(entity));
    }

    <E> Future<E> oneTo(final JsonObject entityJ) {
        return Future.succeededFuture(this.one(entityJ));
    }

    // 同步转同步
    <E> JsonArray many(final List<E> entity) {
        return Poly.<E>ofDB(this.entityCls, this.vector).mapMany(entity);
    }

    <E> List<E> many(final JsonArray entityJA) {
        final List<E> entities = new ArrayList<>();
        entityJA.stream().map(item -> (JsonObject) item).map(this::<E>one).forEach(entities::add);
        return entities;
    }

    // 同步转异步
    <E> Future<JsonArray> manyTo(final List<E> entity) {
        return Future.succeededFuture(this.many(entity));
    }

    <E> Future<List<E>> manyTo(final JsonArray entityA) {
        return Future.succeededFuture(this.many(entityA));
    }

    // 搜索参数转换
    JsonObject mapQuery(final JsonObject query) {
        return Poly.ofQr(this.entityCls, this.vector).map(query);
    }

    JsonObject mapCriteria(final JsonObject criteria) {
        return Poly.ofQr(this.entityCls, this.vector).mapCriteria(criteria);
    }

    JsonArray mapSort(final JsonArray sorter) {
        return Poly.ofQr(this.entityCls, this.vector).mapSort(sorter);
    }

    JsonArray mapProjection(final JsonArray projection) {
        return Poly.ofQr(this.entityCls, this.vector).mapProjection(projection);
    }
}

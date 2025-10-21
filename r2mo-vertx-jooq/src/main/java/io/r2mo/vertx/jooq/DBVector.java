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
public class DBVector<T> {
    private static final Cc<String, DBVector<?>> CC_SKELETON = Cc.openThread();
    private final R2Vector vector;
    private final Class<T> entityCls;

    private DBVector(final R2Vector vector, final Class<T> entityCls) {
        this.vector = vector;
        this.entityCls = entityCls;
    }

    @SuppressWarnings("unchecked")
    public static <T> DBVector<T> of(final AsyncMeta metadata) {
        final String cached = metadata.metaEntity() + "/" + metadata.metaVector();
        return (DBVector<T>) CC_SKELETON.pick(() -> new DBVector<>(metadata.metaVector(), metadata.metaEntity()), cached);
    }

    // 同步转同步
    public <E> JsonObject one(final E entity) {
        return Poly.<E>ofDB(this.entityCls, this.vector).mapOne(entity);
    }

    @SuppressWarnings("unchecked")
    public <E> E one(final JsonObject entityJ) {
        return (E) R2MO.deserializeJ(Poly.ofWeb(this.entityCls, this.vector).mapOne(entityJ), this.entityCls);
    }

    // 同步转异步
    public <E> Future<JsonObject> oneTo(final E entity) {
        return Future.succeededFuture(this.one(entity));
    }

    public <E> Future<E> oneTo(final JsonObject entityJ) {
        return Future.succeededFuture(this.one(entityJ));
    }

    // 异步转异步
    public <E> Future<JsonObject> thenOne(final Future<E> entity) {
        return entity.compose(e -> Future.succeededFuture(this.one(e)));
    }

    public <E> Future<E> thenOneJ(final Future<JsonObject> entityJ) {
        return entityJ.compose(eJ -> Future.succeededFuture(this.one(eJ)));
    }

    // 同步转同步
    public <E> JsonArray many(final List<E> entity) {
        return Poly.<E>ofDB(this.entityCls, this.vector).mapMany(entity);
    }

    public <E> List<E> many(final JsonArray entityJA) {
        final List<E> entities = new ArrayList<>();
        entityJA.stream().map(item -> (JsonObject) item).map(this::<E>one).forEach(entities::add);
        return entities;
    }

    // 同步转异步
    public <E> Future<JsonArray> manyTo(final List<E> entity) {
        return Future.succeededFuture(this.many(entity));
    }

    public <E> Future<List<E>> manyTo(final JsonArray entityA) {
        return Future.succeededFuture(this.many(entityA));
    }

    // 异步转异步
    public <E> Future<JsonArray> thenMany(final Future<List<E>> entity) {
        return entity.compose(e -> Future.succeededFuture(this.many(e)));
    }

    public <E> Future<List<E>> thenManyA(final Future<JsonArray> entityJA) {
        return entityJA.compose(eJA -> Future.succeededFuture(this.many(eJA)));
    }
}

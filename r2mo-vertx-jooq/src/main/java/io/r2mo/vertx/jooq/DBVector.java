package io.r2mo.vertx.jooq;

import io.r2mo.base.program.R2Vector;
import io.r2mo.typed.cc.Cc;
import io.r2mo.vertx.common.mapping.Poly;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

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

    public <E> JsonObject outOne(final E entity) {
        return Poly.<E>ofDB(this.entityCls, this.vector).mapOne(entity);
    }

    public <E> Future<JsonObject> outOne(final Future<E> entity) {
        return entity.compose(e -> Future.succeededFuture(this.outOne(e)));
    }

    public <E> JsonArray outMany(final List<E> entity) {
        return Poly.<E>ofDB(this.entityCls, this.vector).mapMany(entity);
    }

    public <E> Future<JsonArray> outMany(final Future<List<E>> entity) {
        return entity.compose(e -> Future.succeededFuture(this.outMany(e)));
    }
}

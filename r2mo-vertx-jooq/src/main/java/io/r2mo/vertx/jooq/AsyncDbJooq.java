package io.r2mo.vertx.jooq;

import io.r2mo.base.dbe.constant.OpType;
import io.r2mo.vertx.dbe.AsyncDb;
import io.r2mo.vertx.jooq.classic.VertxDAO;
import io.vertx.core.Future;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author lang : 2025-10-19
 */
class AsyncDbJooq<T> extends AsyncDBEAction<T> implements AsyncDb<T> {

    @SuppressWarnings("all")
    protected AsyncDbJooq(final Class<T> entityCls, final VertxDAO vertxDAO) {
        super(entityCls, vertxDAO);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Future<T> executeAsync(final T entity, final OpType opType) {
        if (Objects.isNull(entity)) {
            return Future.succeededFuture();
        }

        this.setter.setPrimaryKey(entity, opType);
        return switch (opType) {
            case CREATE -> (Future<T>) this.executor().insert(entity);
            case UPDATE -> (Future<T>) this.executor().update(entity);
            case REMOVE -> Future.succeededFuture(this.dbe.remove(entity));
            case SAVE -> Future.succeededFuture(this.dbe.save(entity));
        };
    }

    @Override
    @SuppressWarnings("unchecked")
    public Future<List<T>> executeAsync(final List<T> entities, final OpType opType) {
        if (Objects.isNull(entities) || entities.isEmpty()) {
            return Future.succeededFuture(new ArrayList<>());
        }

        this.setter.setPrimaryKey(entities, opType);
        return switch (opType) {
            case CREATE -> (Future<List<T>>) this.executor().insert(entities);
            case UPDATE -> Future.succeededFuture(this.dbe.update(entities));
            case REMOVE -> Future.succeededFuture(this.dbe.remove(entities));
            case SAVE -> Future.succeededFuture(this.dbe.save(entities));
        };
    }
}

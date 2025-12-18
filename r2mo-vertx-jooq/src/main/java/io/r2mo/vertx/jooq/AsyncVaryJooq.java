package io.r2mo.vertx.jooq;

import io.r2mo.base.dbe.constant.OpType;
import io.r2mo.base.dbe.syntax.QQuery;
import io.r2mo.typed.common.Pagination;
import io.r2mo.vertx.dbe.AsyncDb;
import io.r2mo.vertx.dbe.AsyncVary;
import io.r2mo.vertx.jooq.classic.VertxDAO;
import io.vertx.core.Future;
import lombok.extern.slf4j.Slf4j;
import org.jooq.Condition;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author lang : 2025-10-19
 */
@Slf4j
class AsyncVaryJooq<T> extends AsyncDBEAction<T> implements AsyncVary<T, Condition> {
    private final AsyncDb<T> db;

    @SuppressWarnings("all")
    protected AsyncVaryJooq(final Class<T> entityCls, final VertxDAO vertxDAO) {
        super(entityCls, vertxDAO);
        this.db = new AsyncDbJooq<>(entityCls, vertxDAO);
    }

    @Override
    public Future<Pagination<T>> findPageAsync(final QQuery query) {
        if (Objects.isNull(query)) {
            return Future.succeededFuture(new Pagination<>());
        }
        return Future.succeededFuture(this.dbe.findPage(query));
    }

    @Override
    @SuppressWarnings("all")
    public Future<Boolean> removeByAsync(final Condition condition) {
        if (Objects.isNull(condition)) {
            return Future.succeededFuture(false);
        }
        // class java.lang.Integer cannot be cast to class java.lang.Boolean (java.lang.Integer and java.lang.Boolean are in module java.base of loader 'bootstrap')
        final Future<Integer> rows = (Future<Integer>) this.executor().deleteByCondition(condition);
        return rows.compose(result -> {
            log.info("[ R2MO ] ( Jooq ) 删除记录数: {}", result);
            return Future.succeededFuture(Boolean.TRUE);
        });
        // return (Future<Boolean>) this.executor().deleteByCondition(condition);
    }

    @Override
    @SuppressWarnings("all")
    public Future<List<T>> findManyAsync(final Condition condition) {
        if (Objects.isNull(condition)) {
            return Future.succeededFuture(new ArrayList<>());
        }
        return (Future<List<T>>) this.executor().findManyByCondition(condition);
    }

    @Override
    @SuppressWarnings("all")
    public Future<List<T>> findAllAsync() {
        return (Future<List<T>>) this.executor().findAll();
    }

    @Override
    @SuppressWarnings("all")
    public Future<T> findOneAsync(final Condition condition) {
        if (Objects.isNull(condition)) {
            return Future.succeededFuture();
        }
        return (Future<T>) this.executor().findOneByCondition(condition);
    }

    @Override
    public Future<Boolean> removeByIdAsync(final Serializable id) {
        if (Objects.isNull(id)) {
            return Future.succeededFuture(false);
        }
        return this.removeByAsync(this.setter.whereId(id));
    }

    @Override
    public Future<T> saveAsync(final Future<T> queried, final T latest) {
        return queried.compose(stored -> {
            if (Objects.isNull(stored)) {
                // 添加
                return this.db.executeAsync(latest, OpType.CREATE);
            } else {
                // 更新
                this.setter.copyFrom(stored, latest);
                return this.db.executeAsync(stored, OpType.UPDATE);
            }
        });
    }

    @Override
    public Future<T> updateAsync(final Future<T> queried, final T latest) {
        return queried.compose(stored -> {
            if (Objects.isNull(stored)) {
                return Future.succeededFuture();
            } else {
                // 更新
                this.setter.copyFrom(stored, latest);
                return this.db.executeAsync(stored, OpType.UPDATE);
            }
        });
    }
}

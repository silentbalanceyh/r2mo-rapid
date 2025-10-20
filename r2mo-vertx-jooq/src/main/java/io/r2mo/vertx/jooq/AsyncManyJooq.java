package io.r2mo.vertx.jooq;

import cn.hutool.core.util.StrUtil;
import io.r2mo.base.dbe.syntax.QQuery;
import io.r2mo.base.dbe.syntax.QTree;
import io.r2mo.vertx.dbe.AsyncMany;
import io.r2mo.vertx.jooq.classic.VertxDAO;
import io.vertx.core.Future;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author lang : 2025-10-19
 */
class AsyncManyJooq<T> extends AsyncDBEAction<T> implements AsyncMany<T> {
    @SuppressWarnings("all")
    protected AsyncManyJooq(Class<T> entityCls, VertxDAO vertxDAO) {
        super(entityCls, vertxDAO);
    }

    @Override
    public Future<List<T>> executeAsync(final QQuery query) {
        if (Objects.isNull(query)) {
            return Future.succeededFuture(new ArrayList<>());
        }

        return this.findManyAsync(this.analyzer().where(query));
    }

    @Override
    public Future<List<T>> executeAsync(final QTree tree) {
        if (Objects.isNull(tree) || !tree.isOk()) {
            return Future.succeededFuture(new ArrayList<>());
        }

        return this.findManyAsync(this.analyzer().where(tree));
    }

    @Override
    @SuppressWarnings("all")
    public Future<List<T>> executeAsync(final Serializable... ids) {
        if (0 == ids.length) {
            return Future.succeededFuture(new ArrayList<>());
        }


        final String primaryKey = this.meta.keyPrimary();
        return this.findManyAsync(this.analyzer().whereIn(primaryKey, ids));
    }

    @Override
    public Future<List<T>> executeAsync(final String field, final Object value) {
        if (StrUtil.isEmpty(field)) {
            return Future.succeededFuture(new ArrayList<>());
        }


        return this.findManyAsync(this.analyzer().where(field, value));
    }

    @Override
    public Future<List<T>> executeAsync(final Map<String, Object> condition) {
        if (Objects.isNull(condition) || condition.isEmpty()) {
            return Future.succeededFuture(new ArrayList<>());
        }


        return this.findManyAsync(this.analyzer().where(condition));
    }
}

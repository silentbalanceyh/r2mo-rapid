package io.r2mo.vertx.jooq;

import cn.hutool.core.util.StrUtil;
import io.github.jklingsporn.vertx.jooq.classic.VertxDAO;
import io.r2mo.base.dbe.syntax.QTree;
import io.r2mo.vertx.dbe.AsyncOne;
import io.vertx.core.Future;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

/**
 * @author lang : 2025-10-19
 */
class AsyncOneJooq<T> extends AsyncDBEAction<T> implements AsyncOne<T> {
    @SuppressWarnings("all")
    protected AsyncOneJooq(final Class<T> entityCls, final VertxDAO vertxDAO) {
        super(entityCls, vertxDAO);
    }

    @Override
    public Future<T> executeAsync(final QTree syntax) {
        if (Objects.isNull(syntax) || !syntax.isOk()) {
            return Future.succeededFuture();
        }
        return this.findOneAsync(this.analyzer().where(syntax));
    }

    @Override
    public Future<T> executeAsync(final Serializable id) {
        if (Objects.isNull(id)) {
            return Future.succeededFuture();
        }


        return this.findOneAsync(this.setter.whereId(id));
    }

    @Override
    public Future<T> executeAsync(final String field, final Object value) {
        if (StrUtil.isEmpty(field)) {
            return Future.succeededFuture();
        }
        return this.findOneAsync(this.analyzer().where(field, value));
    }

    @Override
    public Future<T> executeAsync(final Map<String, Object> condition) {
        if (Objects.isNull(condition) || condition.isEmpty()) {
            return Future.succeededFuture();
        }


        return this.findOneAsync(this.analyzer().where(condition));
    }
}

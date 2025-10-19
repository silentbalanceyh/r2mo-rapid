package io.r2mo.vertx.jooq;

import io.github.jklingsporn.vertx.jooq.classic.VertxDAO;
import io.r2mo.base.dbe.constant.QCV;
import io.r2mo.base.dbe.syntax.QTree;
import io.r2mo.vertx.dbe.AsyncAggr;
import io.vertx.core.Future;

import java.util.Map;

/**
 * @author lang : 2025-10-19
 */
class AsyncAggrJooq<T> extends AsyncDBEAction<T> implements AsyncAggr {
    @SuppressWarnings("all")
    protected AsyncAggrJooq(final Class<T> entityCls, final VertxDAO vertxDAO) {
        super(entityCls, vertxDAO);
    }

    @Override
    public <N extends Number> Future<N> executeAsync(final String aggrField, final Class<N> returnCls, final QCV.Aggr aggr, final String field, final Object value) {
        return null;
    }

    @Override
    public <N extends Number> Future<N> executeAsync(final String aggrField, final Class<N> returnCls, final QCV.Aggr aggr, final QTree criteria) {
        return null;
    }

    @Override
    public <N extends Number> Future<N> executeAsync(final String aggrField, final Class<N> returnCls, final QCV.Aggr aggr, final Map<String, Object> map) {
        return null;
    }
}

package io.r2mo.vertx.jooq;

import io.r2mo.base.dbe.operation.QrAnalyzer;
import io.r2mo.vertx.dbe.AsyncAggr;
import io.r2mo.vertx.dbe.AsyncDb;
import io.r2mo.vertx.dbe.AsyncMany;
import io.r2mo.vertx.dbe.AsyncOne;
import io.r2mo.vertx.dbe.AsyncVary;
import io.r2mo.vertx.dbe.FactoryDBAsync;

/**
 * @author lang : 2025-10-18
 */
public class FactoryDBAsyncJooq implements FactoryDBAsync {
    @Override
    public <T, EXECUTOR> AsyncAggr opAggr(final Class<T> entityCls, final EXECUTOR executor) {
        return null;
    }

    @Override
    public <T, EXECUTOR> AsyncDb<T> opDb(final Class<T> entityCls, final EXECUTOR executor) {
        return null;
    }

    @Override
    public <T, EXECUTOR, CONDITION> AsyncVary<T, CONDITION> opVary(final Class<T> entityCls, final EXECUTOR executor) {
        return null;
    }

    @Override
    public <T, EXECUTOR> AsyncOne<T> qrOne(final Class<T> entityCls, final EXECUTOR executor) {
        return null;
    }

    @Override
    public <T, EXECUTOR> AsyncMany<T> qrMany(final Class<T> entityCls, final EXECUTOR executor) {
        return null;
    }

    @Override
    public <T, EXECUTOR, CONDITION> QrAnalyzer<CONDITION> qrAnalyzer(final Class<T> entityCls, final EXECUTOR executor) {
        return null;
    }
}

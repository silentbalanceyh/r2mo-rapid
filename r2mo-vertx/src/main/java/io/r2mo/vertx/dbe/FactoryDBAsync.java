package io.r2mo.vertx.dbe;

import io.r2mo.base.dbe.operation.QrAnalyzer;

/**
 * @author lang : 2025-10-18
 */
public interface FactoryDBAsync {

    <T, EXECUTOR> AsyncAggr opAggr(Class<T> entityCls, EXECUTOR executor);

    <T, EXECUTOR> AsyncDb<T> opDb(Class<T> entityCls, EXECUTOR executor);

    <T, EXECUTOR, CONDITION> AsyncVary<T, CONDITION> opVary(Class<T> entityCls, EXECUTOR executor);

    <T, EXECUTOR> AsyncOne<T> qrOne(Class<T> entityCls, EXECUTOR executor);

    <T, EXECUTOR> AsyncMany<T> qrMany(Class<T> entityCls, EXECUTOR executor);

    <T, EXECUTOR, CONDITION> QrAnalyzer<CONDITION> qrAnalyzer(Class<T> entityCls, EXECUTOR executor);
}

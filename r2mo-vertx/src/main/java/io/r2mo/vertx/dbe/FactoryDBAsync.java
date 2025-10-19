package io.r2mo.vertx.dbe;

import io.r2mo.base.dbe.operation.QrAnalyzer;

/**
 * @author lang : 2025-10-18
 */
public interface FactoryDBAsync {

    <T, METADATA> AsyncAggr opAggr(Class<T> entityCls, METADATA executor);

    <T, METADATA> AsyncDb<T> opDb(Class<T> entityCls, METADATA executor);

    <T, METADATA, CONDITION> AsyncVary<T, CONDITION> opVary(Class<T> entityCls, METADATA executor);

    <T, METADATA> AsyncOne<T> qrOne(Class<T> entityCls, METADATA executor);

    <T, METADATA> AsyncMany<T> qrMany(Class<T> entityCls, METADATA executor);

    <T, METADATA, CONDITION> QrAnalyzer<CONDITION> qrAnalyzer(Class<T> entityCls, METADATA executor);
}

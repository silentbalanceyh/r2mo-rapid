package io.r2mo.spi;

import io.r2mo.base.dbe.operation.*;

/**
 * @author lang : 2025-08-28
 */
public interface FactoryDBAction {

    <T, EXECUTOR> OpAggr opAggr(Class<T> entityCls, EXECUTOR executor);

    <T, EXECUTOR> OpDb<T> opDb(Class<T> entityCls, EXECUTOR executor);

    <T, EXECUTOR, CONDITION> OpVary<T, CONDITION> opVary(Class<T> entityCls, EXECUTOR executor);

    <T, EXECUTOR> QrOne<T> qrOne(Class<T> entityCls, EXECUTOR executor);

    <T, EXECUTOR> QrMany<T> qrMany(Class<T> entityCls, EXECUTOR executor);

    <T, EXECUTOR, CONDITION> QrAnalyzer<CONDITION> qrAnalyzer(Class<T> entityCls, EXECUTOR executor);
}

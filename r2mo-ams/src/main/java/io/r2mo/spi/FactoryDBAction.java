package io.r2mo.spi;

import io.r2mo.base.dbe.DBS;
import io.r2mo.base.dbe.Database;
import io.r2mo.base.dbe.join.DBRef;
import io.r2mo.base.dbe.operation.OpAggr;
import io.r2mo.base.dbe.operation.OpDb;
import io.r2mo.base.dbe.operation.OpJoin;
import io.r2mo.base.dbe.operation.OpVary;
import io.r2mo.base.dbe.operation.QrAnalyzer;
import io.r2mo.base.dbe.operation.QrMany;
import io.r2mo.base.dbe.operation.QrOne;

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

    <EXECUTOR, CONDITION> OpJoin<CONDITION> opJoin(DBRef ref, EXECUTOR executor);

    /*
     * 追加前置方法，用于处理配置模式 --> 非自动配置下将 Database -> DBS
     */
    default DBS configure(final Database database) {
        return null;
    }
}

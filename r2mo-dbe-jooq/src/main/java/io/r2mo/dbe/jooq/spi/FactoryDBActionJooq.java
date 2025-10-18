package io.r2mo.dbe.jooq.spi;

import io.r2mo.base.dbe.DBS;
import io.r2mo.base.dbe.Database;
import io.r2mo.base.dbe.operation.OpAggr;
import io.r2mo.base.dbe.operation.OpDb;
import io.r2mo.base.dbe.operation.OpVary;
import io.r2mo.base.dbe.operation.QrAnalyzer;
import io.r2mo.base.dbe.operation.QrMany;
import io.r2mo.base.dbe.operation.QrOne;
import io.r2mo.dbe.jooq.core.domain.JooqDBS;
import io.r2mo.spi.FactoryDBAction;

/**
 * @author lang : 2025-10-18
 */
public class FactoryDBActionJooq implements FactoryDBAction {

    @Override
    public <T, EXECUTOR> OpAggr opAggr(final Class<T> entityCls, final EXECUTOR executor) {
        return null;
    }

    @Override
    public <T, EXECUTOR> OpDb<T> opDb(final Class<T> entityCls, final EXECUTOR executor) {
        return null;
    }

    @Override
    public <T, EXECUTOR, CONDITION> OpVary<T, CONDITION> opVary(final Class<T> entityCls, final EXECUTOR executor) {
        return null;
    }

    @Override
    public <T, EXECUTOR> QrOne<T> qrOne(final Class<T> entityCls, final EXECUTOR executor) {
        return null;
    }

    @Override
    public <T, EXECUTOR> QrMany<T> qrMany(final Class<T> entityCls, final EXECUTOR executor) {
        return null;
    }

    @Override
    public <T, EXECUTOR, CONDITION> QrAnalyzer<CONDITION> qrAnalyzer(final Class<T> entityCls, final EXECUTOR executor) {
        return null;
    }

    @Override
    public DBS configure(final Database database) {
        // 配置单个数据库
        return JooqDBS.getOrCreate(database);
    }
}

package io.r2mo.dbe.jooq.spi;

import io.r2mo.base.dbe.DBS;
import io.r2mo.base.dbe.Database;
import io.r2mo.base.dbe.common.DBLoad;
import io.r2mo.base.dbe.common.DBRef;
import io.r2mo.base.dbe.operation.OpAggr;
import io.r2mo.base.dbe.operation.OpDb;
import io.r2mo.base.dbe.operation.OpJoin;
import io.r2mo.base.dbe.operation.OpVary;
import io.r2mo.base.dbe.operation.QrAnalyzer;
import io.r2mo.base.dbe.operation.QrMany;
import io.r2mo.base.dbe.operation.QrOne;
import io.r2mo.dbe.common.operation.FactoryDBActionBase;
import io.r2mo.dbe.jooq.core.domain.JooqDBS;
import io.r2mo.typed.cc.Cc;
import org.jooq.DSLContext;

/**
 * @author lang : 2025-10-18
 */
@SuppressWarnings("unchecked")
public class FactoryDBActionJooq extends FactoryDBActionBase {


    @Override
    public <T, EXECUTOR, CONDITION> OpJoin<T, CONDITION> opJoin(final DBRef ref, final EXECUTOR executor) {
        return null;
    }

    @Override
    protected <T, EXECUTOR> OpAggr ofAggr(final Class<T> entityCls, final EXECUTOR executor) {
        return new OpAggrJooq<>(entityCls, (DSLContext) executor);
    }

    @Override
    protected Class<?> ofAggr() {
        return OpAggrJooq.class;
    }

    @Override
    protected <T, EXECUTOR> OpDb<T> ofDb(final Class<T> entityCls, final EXECUTOR executor) {
        return new OpDbJooq<>(entityCls, (DSLContext) executor);
    }

    @Override
    protected Class<?> ofDb() {
        return OpDbJooq.class;
    }

    @Override
    protected <T, EXECUTOR> QrOne<T> ofOne(final Class<T> entityCls, final EXECUTOR executor) {
        return new QrOneJooq<>(entityCls, (DSLContext) executor);
    }

    @Override
    protected Class<?> ofOne() {
        return QrOneJooq.class;
    }

    @Override
    protected <T, EXECUTOR> QrMany<T> ofMany(final Class<T> entityCls, final EXECUTOR executor) {
        return new QrManyJooq<>(entityCls, (DSLContext) executor);
    }

    @Override
    protected Class<?> ofMany() {
        return QrManyJooq.class;
    }

    @Override
    protected <T, EXECUTOR, CONDITION> OpVary<T, CONDITION> ofVary(final Class<T> entityCls, final EXECUTOR executor) {
        return (OpVary<T, CONDITION>) new OpVaryJooq<>(entityCls, (DSLContext) executor);
    }

    @Override
    protected Class<?> ofVary() {
        return OpVaryJooq.class;
    }

    @Override
    protected <T, EXECUTOR, CONDITION> QrAnalyzer<CONDITION> ofAnalyzer(final Class<T> entityCls, final EXECUTOR executor) {
        return (QrAnalyzer<CONDITION>) new QrAnalyzerJooq(entityCls, (DSLContext) executor);
    }

    @Override
    protected Class<?> ofAnalyzer() {
        return QrAnalyzerJooq.class;
    }

    // 子类可选
    @Override
    public DBS configure(final Database database) {
        // 配置单个数据库
        return JooqDBS.getOrCreate(database);
    }

    // 子类必须
    private static final Cc<String, DBLoad> CC_DB_LOAD = Cc.openThread();

    @Override
    public DBLoad loader() {
        return CC_DB_LOAD.pick(LoadJooq::new);
    }
}

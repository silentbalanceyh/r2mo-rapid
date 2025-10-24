package io.r2mo.dbe.common.operation;

import io.r2mo.base.dbe.common.DBLoad;
import io.r2mo.base.dbe.operation.OpAggr;
import io.r2mo.base.dbe.operation.OpDb;
import io.r2mo.base.dbe.operation.OpVary;
import io.r2mo.base.dbe.operation.QrAnalyzer;
import io.r2mo.base.dbe.operation.QrMany;
import io.r2mo.base.dbe.operation.QrOne;
import io.r2mo.spi.FactoryDBAction;
import io.r2mo.typed.cc.Cc;

/**
 * @author lang : 2025-10-19
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public abstract class FactoryDBActionBase implements FactoryDBAction {
    private static final Cc<String, OpAggr> CCT_OP_AGGR = Cc.openThread();
    private static final Cc<String, OpDb> CCT_OP_DB = Cc.openThread();
    private static final Cc<String, QrAnalyzer> CCT_QR_ANALYZER = Cc.openThread();
    private static final Cc<String, QrOne> CCT_QR_ONE = Cc.openThread();
    private static final Cc<String, QrMany> CCT_QR_MANY = Cc.openThread();
    private static final Cc<String, OpVary> CCT_OP_VARY = Cc.openThread();

    private static final Cc<String, DBLoad> CC_DB_LOAD = Cc.openThread();

    private String keyCached(final Class<?> entityCls, final Object executor, final Class<?> implCls) {
        return entityCls.getName() + "@" + implCls.getName() + "@" + executor.hashCode();
    }

    @Override
    public <T, EXECUTOR> OpAggr opAggr(final Class<T> entityCls, final EXECUTOR executor) {
        final String cacheKey = this.keyCached(entityCls, executor, this.ofAggr());
        return CCT_OP_AGGR.pick(() -> this.ofAggr(entityCls, executor), cacheKey);
    }

    @Override
    public <T, EXECUTOR> OpDb<T> opDb(final Class<T> entityCls, final EXECUTOR executor) {
        final String cacheKey = this.keyCached(entityCls, executor, this.ofDb());
        return CCT_OP_DB.pick(() -> this.ofDb(entityCls, executor), cacheKey);
    }

    @Override
    public <T, EXECUTOR> QrOne<T> qrOne(final Class<T> entityCls, final EXECUTOR executor) {
        final String cacheKey = this.keyCached(entityCls, executor, this.ofOne());
        return CCT_QR_ONE.pick(() -> this.ofOne(entityCls, executor), cacheKey);
    }

    @Override
    public <T, EXECUTOR> QrMany<T> qrMany(final Class<T> entityCls, final EXECUTOR executor) {
        final String cacheKey = this.keyCached(entityCls, executor, this.ofMany());
        return CCT_QR_MANY.pick(() -> this.ofMany(entityCls, executor), cacheKey);
    }

    @Override
    public <T, EXECUTOR, CONDITION> OpVary<T, CONDITION> opVary(final Class<T> entityCls, final EXECUTOR executor) {
        final String cacheKey = this.keyCached(entityCls, executor, this.ofVary());
        return CCT_OP_VARY.pick(() -> this.ofVary(entityCls, executor), cacheKey);
    }

    @Override
    public <T, EXECUTOR, CONDITION> QrAnalyzer<CONDITION> qrAnalyzer(final Class<T> entityCls, final EXECUTOR executor) {
        final String cacheKey = this.keyCached(entityCls, executor, this.ofAnalyzer());
        return CCT_QR_ANALYZER.pick(() -> this.ofAnalyzer(entityCls, executor), cacheKey);
    }

    // ==================== 抽象方法 ===================T

    protected abstract <T, EXECUTOR> OpAggr ofAggr(final Class<T> entityCls, final EXECUTOR executor);

    protected abstract Class<?> ofAggr();

    protected abstract <T, EXECUTOR> OpDb<T> ofDb(final Class<T> entityCls, final EXECUTOR executor);

    protected abstract Class<?> ofDb();

    protected abstract <T, EXECUTOR> QrOne<T> ofOne(final Class<T> entityCls, final EXECUTOR executor);

    protected abstract Class<?> ofOne();

    protected abstract <T, EXECUTOR> QrMany<T> ofMany(final Class<T> entityCls, final EXECUTOR executor);

    protected abstract Class<?> ofMany();

    protected abstract <T, EXECUTOR, CONDITION> OpVary<T, CONDITION> ofVary(final Class<T> entityCls, final EXECUTOR executor);

    protected abstract Class<?> ofVary();

    protected abstract <T, EXECUTOR, CONDITION> QrAnalyzer<CONDITION> ofAnalyzer(final Class<T> entityCls, final EXECUTOR executor);

    protected abstract Class<?> ofAnalyzer();
}

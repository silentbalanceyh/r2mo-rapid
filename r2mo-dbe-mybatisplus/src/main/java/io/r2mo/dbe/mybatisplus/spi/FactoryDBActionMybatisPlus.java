package io.r2mo.dbe.mybatisplus.spi;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.r2mo.base.dbe.operation.*;
import io.r2mo.spi.FactoryDBAction;
import io.r2mo.typed.cc.Cc;

/**
 * @author lang : 2025-08-28
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class FactoryDBActionMybatisPlus implements FactoryDBAction {
    private static final Cc<String, OpAggr> CCT_OP_AGGR = Cc.openThread();
    private static final Cc<String, OpDb> CCT_OP_DB = Cc.openThread();
    private static final Cc<String, QrAnalyzer> CCT_QR_ANALYZER = Cc.openThread();
    private static final Cc<String, QrOne> CCT_QR_ONE = Cc.openThread();
    private static final Cc<String, QrMany> CCT_QR_MANY = Cc.openThread();
    private static final Cc<String, OpVary> CCT_OP_VARY = Cc.openThread();

    private String keyCached(final Class<?> entityCls, final Object executor, final Class<?> implCls) {
        return entityCls.getName() + "@" + implCls.getName() + "@" + executor.hashCode();
    }

    @Override
    public <T, EXECUTOR> OpAggr opAggr(final Class<T> entityCls, final EXECUTOR executor) {
        final String cacheKey = this.keyCached(entityCls, executor, OpAggrImpl.class);
        return CCT_OP_AGGR.pick(() -> new OpAggrImpl<>(entityCls, (BaseMapper<T>) executor), cacheKey);
    }

    @Override
    public <T, EXECUTOR> OpDb<T> opDb(final Class<T> entityCls, final EXECUTOR executor) {
        final String cacheKey = this.keyCached(entityCls, executor, OpDbImpl.class);
        return CCT_OP_DB.pick(() -> new OpDbImpl<>(entityCls, (BaseMapper<T>) executor), cacheKey);
    }

    @Override
    public <T, EXECUTOR> QrOne<T> qrOne(final Class<T> entityCls, final EXECUTOR executor) {
        final String cacheKey = this.keyCached(entityCls, executor, QrOneImpl.class);
        return CCT_QR_ONE.pick(() -> new QrOneImpl<>(entityCls, (BaseMapper<T>) executor), cacheKey);
    }

    @Override
    public <T, EXECUTOR> QrMany<T> qrMany(final Class<T> entityCls, final EXECUTOR executor) {
        final String cacheKey = this.keyCached(entityCls, executor, QrManyImpl.class);
        return CCT_QR_MANY.pick(() -> new QrManyImpl<>(entityCls, (BaseMapper<T>) executor), cacheKey);
    }

    @Override
    public <T, EXECUTOR, CONDITION> OpVary<T, CONDITION> opVary(final Class<T> entityCls, final EXECUTOR executor) {
        final String cacheKey = this.keyCached(entityCls, executor, OpVaryImpl.class);
        return CCT_OP_VARY.pick(() -> new OpVaryImpl<>(entityCls, (BaseMapper<T>) executor), cacheKey);
    }

    @Override
    public <T, EXECUTOR, CONDITION> QrAnalyzer<CONDITION> qrAnalyzer(final Class<T> entityCls, final EXECUTOR executor) {
        final String cacheKey = this.keyCached(entityCls, executor, QrAnalyzerImpl.class);
        return CCT_QR_ANALYZER.pick(() -> new QrAnalyzerImpl<>(entityCls), cacheKey);
    }
}

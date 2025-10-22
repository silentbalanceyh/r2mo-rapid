package io.r2mo.vertx.jooq;

import io.r2mo.base.dbe.operation.QrAnalyzer;
import io.r2mo.dbe.jooq.spi.QrAnalyzerJooq;
import io.r2mo.typed.cc.Cc;
import io.r2mo.vertx.dbe.AsyncAggr;
import io.r2mo.vertx.dbe.AsyncDb;
import io.r2mo.vertx.dbe.AsyncMany;
import io.r2mo.vertx.dbe.AsyncOne;
import io.r2mo.vertx.dbe.AsyncVary;
import io.r2mo.vertx.dbe.FactoryDBAsync;

/**
 * @author lang : 2025-10-18
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class FactoryDBAsyncJooq implements FactoryDBAsync {
    private static final Cc<String, AsyncAggr> CCT_OP_AGGR = Cc.openThread();
    private static final Cc<String, AsyncDb> CCT_OP_DB = Cc.openThread();
    private static final Cc<String, QrAnalyzer> CCT_QR_ANALYZER = Cc.openThread();
    private static final Cc<String, AsyncOne> CCT_QR_ONE = Cc.openThread();
    private static final Cc<String, AsyncMany> CCT_QR_MANY = Cc.openThread();
    private static final Cc<String, AsyncVary> CCT_OP_VARY = Cc.openThread();

    // 此处两个 Analyzer 可能相同
    private String keyCached(final Class<?> entityCls, final Object executor, final Class<?> implCls) {
        return entityCls.getName() + "@" + implCls.getName() + "@" + executor.hashCode();
    }

    @Override
    public <T, METADATA> AsyncAggr opAggr(final Class<T> entityCls, final METADATA executor) {
        final String cacheKey = this.keyCached(entityCls, executor, AsyncOneJooq.class);
        return CCT_OP_AGGR.pick(() -> {
            final AsyncMeta meta = (AsyncMeta) executor;
            return new AsyncAggrJooq<>(entityCls, meta.executor());
        }, cacheKey);
    }

    @Override
    public <T, METADATA> AsyncDb<T> opDb(final Class<T> entityCls, final METADATA executor) {
        final String cacheKey = this.keyCached(entityCls, executor, AsyncOneJooq.class);
        return CCT_OP_DB.pick(() -> {
            final AsyncMeta meta = (AsyncMeta) executor;
            return new AsyncDbJooq<>(entityCls, meta.executor());
        }, cacheKey);
    }

    @Override
    public <T, METADATA, CONDITION> AsyncVary<T, CONDITION> opVary(final Class<T> entityCls, final METADATA executor) {
        final String cacheKey = this.keyCached(entityCls, executor, AsyncOneJooq.class);
        return CCT_OP_VARY.pick(() -> {
            final AsyncMeta meta = (AsyncMeta) executor;
            return new AsyncVaryJooq<>(entityCls, meta.executor());
        }, cacheKey);
    }

    @Override
    public <T, METADATA> AsyncOne<T> qrOne(final Class<T> entityCls, final METADATA executor) {
        final String cacheKey = this.keyCached(entityCls, executor, AsyncOneJooq.class);
        return CCT_QR_ONE.pick(() -> {
            final AsyncMeta meta = (AsyncMeta) executor;
            return new AsyncOneJooq<>(entityCls, meta.executor());
        }, cacheKey);
    }

    @Override
    public <T, METADATA> AsyncMany<T> qrMany(final Class<T> entityCls, final METADATA executor) {
        final String cacheKey = this.keyCached(entityCls, executor, AsyncManyJooq.class);
        return CCT_QR_MANY.pick(() -> {
            final AsyncMeta meta = (AsyncMeta) executor;
            return new AsyncManyJooq<>(entityCls, meta.executor());
        }, cacheKey);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T, METADATA, CONDITION> QrAnalyzer<CONDITION> qrAnalyzer(final Class<T> entityCls, final METADATA executor) {
        final String cacheKey = this.keyCached(entityCls, executor, QrAnalyzerJooq.class);
        return CCT_QR_ANALYZER.pick(() -> {
            final AsyncMeta meta = (AsyncMeta) executor;
            return new QrAnalyzerJooq(entityCls, meta.context());
        }, cacheKey);
    }
}

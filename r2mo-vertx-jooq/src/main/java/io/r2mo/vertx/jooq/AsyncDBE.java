package io.r2mo.vertx.jooq;

import io.r2mo.typed.cc.Cc;
import org.jooq.Condition;

/**
 * @author lang : 2025-10-21
 */
class AsyncDBE<T> extends io.r2mo.vertx.dbe.AsyncDBE<Condition, T, AsyncMeta> {
    private static final Cc<String, AsyncDBE<?>> CC_ASYNC_DBE = Cc.openThread();

    private AsyncDBE(final Class<T> entityCls, final AsyncMeta meta) {
        super(entityCls, meta);
    }

    @SuppressWarnings("unchecked")
    static <T> AsyncDBE<T> of(final Class<T> entityCls, final AsyncMeta meta) {
        return (AsyncDBE<T>) CC_ASYNC_DBE.pick(() -> new AsyncDBE<>(entityCls, meta), entityCls.getName());
    }
}

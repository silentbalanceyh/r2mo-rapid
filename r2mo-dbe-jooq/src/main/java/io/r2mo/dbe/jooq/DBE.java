package io.r2mo.dbe.jooq;

import io.r2mo.typed.cc.Cc;
import org.jooq.Condition;
import org.jooq.DSLContext;

/**
 * @author lang : 2025-10-18
 */
public class DBE<T> extends io.r2mo.dbe.common.DBE<Condition, T, DSLContext> {

    private static final Cc<String, DBE<?>> CCT_DBE = Cc.openThread();

    protected DBE(final Class<T> entityCls, final DSLContext executor) {
        super(entityCls, executor);
    }

    @SuppressWarnings("all")
    public static <T> DBE<T> of(final Class<T> entityCls, final DSLContext executor) {
        final String cacheKey = entityCls.getName() + "@" + executor.hashCode();
        return (DBE<T>) CCT_DBE.pick(() -> new DBE<>(entityCls, executor), cacheKey);
    }
}

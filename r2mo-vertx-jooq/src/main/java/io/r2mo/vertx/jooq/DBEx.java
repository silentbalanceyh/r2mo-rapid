package io.r2mo.vertx.jooq;

import io.r2mo.base.dbe.DBS;
import io.r2mo.base.program.R2Vector;
import io.r2mo.typed.cc.Cc;

/**
 * 异步模式的 DBE -> Async Database Engine, x - Extension（扩展）
 * <pre>
 *     此处没有使用 {@link io.r2mo.dbe.jooq.DBE}
 * </pre>
 *
 * @author lang : 2025-10-18
 */
@SuppressWarnings("all")
public class DBEx<T> extends DBExJson<T> {
    private static final Cc<String, DBEx> CC_DBEX = Cc.openThread();

    public DBEx vector(final R2Vector vector) {
        this.metadata().vector(vector);
        return this;
    }

    private DBEx(final Class<T> daoCls, final DBS dbs) {
        super(daoCls, dbs);
    }

    // -------------------- 静态创建方法 ----------------------
    public static DBEx of(final Class<?> daoCls, final DBS dbs) {
        final String cached = JooqContext.cached(daoCls, dbs);
        return CC_DBEX.pick(() -> new DBEx(daoCls, dbs), cached);
    }
}

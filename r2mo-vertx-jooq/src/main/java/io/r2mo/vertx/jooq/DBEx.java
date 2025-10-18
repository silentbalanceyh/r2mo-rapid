package io.r2mo.vertx.jooq;

import io.r2mo.base.dbe.DBS;
import io.r2mo.base.dbe.Database;
import io.r2mo.base.program.R2Vector;
import io.r2mo.dbe.jooq.DBE;
import io.r2mo.dbe.jooq.core.domain.JooqDatabase;
import io.r2mo.typed.cc.Cc;
import io.r2mo.typed.exception.web._501NotSupportException;
import io.vertx.core.Vertx;

import java.util.Objects;

/**
 * 异步模式的 DBE -> Async Database Engine, x - Extension（扩展）
 *
 * @author lang : 2025-10-18
 */
@SuppressWarnings("all")
public class DBEx {
    private static final Cc<String, DBEx> CC_DBEX = Cc.openThread();
    private final DBS dbs;
    private final Vertx vertxRef;
    private final JooqMetaAsync metadata;
    // Bridge
    private final DBE<?> dbe;

    public DBEx vector(final R2Vector vector) {
        metadata.vector(vector);
        return this;
    }

    private DBEx(final Class<?> daoCls, final DBS dbs) {
        // 提取 Database 引用，构造同步专用的 DSLContext
        final Database database = dbs.getDatabase();
        if (!(database instanceof final JooqDatabase jooqDatabase)) {
            throw new _501NotSupportException("[ R2MO ] JOOQ 模式仅支持 JooqDatabase 类型的数据库引用！");
        }

        this.dbs = dbs;
        // 内部直接访问 Context 中的引用
        this.vertxRef = JooqContext.vertxStatic(dbs);
        Objects.requireNonNull(vertxRef, "[ R2MO ] 关键步骤 DBS 无法初始化 Vertx 引用！");


        final JooqMetaAsync metaAsync = new JooqMetaAsync(daoCls);
        this.metadata = metaAsync;
        // 同步初始化
        this.dbe = DBE.of(metaAsync.entityClass(), jooqDatabase.getContext());
    }

    // -------------------- 静态创建方法 ----------------------
    public static DBEx of(final Class<?> daoCls, final DBS dbs) {
        final String cached = JooqContext.cached(daoCls, dbs);
        return CC_DBEX.pick(() -> new DBEx(daoCls, dbs), cached);
    }
}

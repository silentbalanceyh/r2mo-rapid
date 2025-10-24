package io.r2mo.vertx.jooq;

import io.r2mo.base.dbe.DBS;
import io.r2mo.base.dbe.Database;
import io.r2mo.base.dbe.common.DBRef;
import io.r2mo.dbe.jooq.DBE;
import io.r2mo.dbe.jooq.core.domain.JooqDatabase;
import io.r2mo.typed.exception.web._501NotSupportException;
import io.vertx.core.Vertx;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author lang : 2025-10-25
 */
class DBJxBase {
    private final DBS dbs;
    private final Vertx vertxRef;

    private final ConcurrentMap<Class<?>, DBE<?>> dbeMap = new ConcurrentHashMap<>();

    protected DBJxBase(final DBRef ref, final DBS dbs) {
        // 提取 Database 引用，构造同步专用的 DSLContext
        final Database database = dbs.getDatabase();
        if (!(database instanceof final JooqDatabase jooqDatabase)) {
            throw new _501NotSupportException("[ R2MO ] JOOQ 模式仅支持 JooqDatabase 类型的数据库引用！");
        }
        this.dbs = dbs;
        this.vertxRef = AsyncDBContext.vertxStatic(dbs);
        Objects.requireNonNull(this.vertxRef, "[ R2MO ] (ADB) 关键步骤 DBS 无法初始化 Vertx 引用！");
    }
}

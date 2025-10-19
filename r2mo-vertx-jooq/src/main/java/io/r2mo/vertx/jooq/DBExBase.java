package io.r2mo.vertx.jooq;

import io.r2mo.base.dbe.DBS;
import io.r2mo.base.dbe.Database;
import io.r2mo.dbe.jooq.DBE;
import io.r2mo.dbe.jooq.core.domain.JooqDatabase;
import io.r2mo.typed.exception.web._501NotSupportException;
import io.r2mo.vertx.dbe.AsyncDBE;
import io.vertx.core.Vertx;
import lombok.experimental.Accessors;
import org.jooq.Condition;

import java.util.Objects;

/**
 * @author lang : 2025-10-20
 */
@SuppressWarnings("all")
class DBExBase<T> {
    private final DBS dbs;
    private final Vertx vertxRef;


    // 映射和元数据
    @Accessors(fluent = true, chain = true)
    private final AsyncMeta metadata;

    // 桥接：同步和异步
    protected final DBE<T> dbe;
    protected final AsyncDBE<Condition, T, AsyncMeta> dbeAsync;

    protected DBExBase(final Class<T> daoCls, final DBS dbs) {
        // 提取 Database 引用，构造同步专用的 DSLContext
        final Database database = dbs.getDatabase();
        if (!(database instanceof final JooqDatabase jooqDatabase)) {
            throw new _501NotSupportException("[ R2MO ] JOOQ 模式仅支持 JooqDatabase 类型的数据库引用！");
        }

        this.dbs = dbs;
        // 内部直接访问 Context 中的引用
        this.vertxRef = JooqContext.vertxStatic(dbs);
        Objects.requireNonNull(this.vertxRef, "[ R2MO ] 关键步骤 DBS 无法初始化 Vertx 引用！");


        final AsyncMeta metaAsync = AsyncMeta.of(daoCls, jooqDatabase.getContext(), this.vertxRef);
        this.metadata = metaAsync;


        // 同步初始化
        this.dbe = DBE.of((Class<T>) metaAsync.metaEntity(), jooqDatabase.getContext());
        // 异步初始化
        this.dbeAsync = AsyncDBE.of((Class<T>) metaAsync.metaEntity(), metaAsync);
    }

    protected DBS refDBS() {
        return this.dbs;
    }

    protected Vertx refVertx() {
        return this.vertxRef;
    }

    protected AsyncMeta metadata() {
        return this.metadata;
    }
}

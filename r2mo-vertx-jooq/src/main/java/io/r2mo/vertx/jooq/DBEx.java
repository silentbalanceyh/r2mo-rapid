package io.r2mo.vertx.jooq;

import io.r2mo.base.dbe.DBS;
import io.r2mo.base.program.R2Vector;
import io.r2mo.typed.cc.Cc;
import io.vertx.core.Vertx;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

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
    private final Class<?> daoCls;
    private final Vertx vertxRef;
    /**
     * 正常而言，一旦带有 vector 在访问数据库过程中就不可能被拿掉，所以可以将 {@link R2Vector} 存储在 Meta 中使用，
     * 一般是用于迁移，比如配置 pojoFile -> 旧数据库往新数据库做迁移，使用此映射来实现所有操作，直接加载了此处的
     * {@link R2Vector} 之后就可以在 DB 的前置和后置工作做先处理部分事情来实现整体的映射流程
     */
    @Accessors(fluent = true, chain = true)
    @Getter
    @Setter
    private R2Vector vector;

    private DBEx(final Class<?> daoCls, final DBS dbs) {
        this.dbs = dbs;
        this.daoCls = daoCls;
        // 内部直接访问 Context 中的引用
        this.vertxRef = JooqContext.vertxStatic(dbs);
        Objects.requireNonNull(vertxRef, "[ R2MO ] 关键步骤 DBS 无法初始化 Vertx 引用！");
        // 单个线程内的基本操作，访问 DBE 时可使用
        // -> DBE 同步模式
        // -> DBAsync 异步模式
    }

    // -------------------- 静态创建方法 ----------------------
    public static DBEx of(final Class<?> daoCls, final DBS dbs) {
        final String cached = JooqContext.cached(daoCls, dbs);
        return CC_DBEX.pick(() -> new DBEx(daoCls, dbs), cached);
    }
}

package io.r2mo.vertx.jooq;

import io.github.jklingsporn.vertx.jooq.classic.VertxDAO;
import io.r2mo.base.dbe.DBS;
import io.r2mo.base.program.R2Vector;
import io.r2mo.typed.cc.Cc;
import io.vertx.core.Vertx;

import java.io.Serializable;

/**
 * 统一配置，包含了 Jooq 同步和异步的配置项
 *
 * @author lang : 2025-10-18
 */
public class JooqMeta implements Serializable {
    private static final Cc<String, JooqMeta> CC_META = Cc.open();
    // 元数据
    private final Class<?> daoCls;
    private Class<?> entityCls;
    // 数据源
    private final DBS dbs;
    // Vertx 相关的引用
    @SuppressWarnings("all")
    private VertxDAO daoRef;
    private final Vertx vertxRef;


    /**
     * 正常而言，一旦带有 vector 在访问数据库过程中就不可能被拿掉，所以可以将 {@link R2Vector} 存储在 Meta 中使用，
     * 一般是用于迁移，比如配置 pojoFile -> 旧数据库往新数据库做迁移，使用此映射来实现所有操作，直接加载了此处的
     * {@link R2Vector} 之后就可以在 DB 的前置和后置工作做先处理部分事情来实现整体的映射流程
     */
    private R2Vector vector;

    private JooqMeta(final Class<?> daoCls, final DBS dbs) {
        this.daoCls = daoCls;
        this.dbs = dbs;
        // 直接底层访问 Context 中的引用 -> 内部直接访问
        this.vertxRef = JooqContext.CC_VECTOR.get(String.valueOf(dbs.hashCode()));
    }

    public String cached() {
        return this.vertxRef.hashCode() + "/" + this.dbs.hashCode() + "@" + this.daoCls.getName();
    }

    /**
     * 创建 JooqMeta 实例
     *
     * @param daoCls {@link VertxDAO} 的子类型
     * @param dbs    {@link DBS} 数据源管理器
     *
     * @return 返回 JooqMeta 实例
     */
    public static JooqMeta of(final Class<?> daoCls, final DBS dbs) {
        final String cacheKey = daoCls.getName() + "@" + dbs.hashCode();
        return CC_META.pick(() -> new JooqMeta(daoCls, dbs), cacheKey);
    }
}

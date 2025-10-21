package io.r2mo.vertx.jooq;

import io.r2mo.base.dbe.DBS;
import io.r2mo.base.program.R2Vector;
import io.r2mo.typed.cc.Cc;

/**
 * 异步模式的 DBE -> Async Database Engine, x - Extension（扩展）
 * <pre>
 *     此处没有使用 {@link io.r2mo.dbe.jooq.DBE} 直接调用，整体继承结构如下
 *     - {@link DBExBase} -> 基础桥接同步/异步的核心支持
 *         - 变量 {@link DBExBase#dbe} -> {@link io.r2mo.dbe.jooq.DBE} 同步数据库引擎
 *         - 变量 {@link DBExBase#dbeAsync} -> {@link io.r2mo.vertx.dbe.AsyncDBE} 异步数据库引擎
 *       | - {@link DBExCommon}
 *           | - {@link DBExFuture}
 *               | - {@link DBExJson}
 *                   | - {@link DBEx} -> 本类，向外暴露最终的功能接口
 *     此处的代价：多层继承看起来创建了更多的对象，但是每一层的职责更加单一清晰，便于维护和扩展，而且 {@link DBEx}
 *     本身已经采用了组件缓存 {@link Cc}，不会频繁创建对象，所以整体性能影响可以忽略不计。
 * </pre>
 *
 * @author lang : 2025-10-18
 */
@SuppressWarnings("all")
public class DBEx<T> extends DBExJson<T> {
    private static final Cc<String, DBEx> CC_DBEX = Cc.openThread();

    private DBEx(final Class<T> daoCls, final DBS dbs, final R2Vector vector) {
        super(daoCls, dbs);
        /*
         * 留给底层去处理 R2Vector 映射关系
         */
        this.metadata().vector(vector);
    }

    // -------------------- 静态创建方法 ----------------------

    /**
     * 基础工厂方法：根据【实体/DAO 类】与【数据源】创建或获取 DBEx 实例。
     *
     * <p>内部会基于 {@link AsyncDBContext#cached(Class, DBS, R2Vector)} 计算缓存键，
     * 并通过 {@code CC_DBEX.pick(...)} 复用已存在的实例 ♻️，避免重复创建。</p>
     *
     * @param daoCls 实体/DAO 类（用于确定上下文与缓存键）
     * @param dbs    数据源（DBS）🗄️
     *
     * @return 对应上下文的 DBEx 实例（可能为缓存复用）
     */
    public static DBEx of(final Class<?> daoCls, final DBS dbs) {
        return of(daoCls, dbs, null);
    }

    /**
     * 进阶工厂方法：根据【实体/DAO 类】、【数据源】与【字段映射器】创建或获取 DBEx 实例。
     *
     * <p>当需要对字段/列进行自定义映射时，传入 {@link R2Vector} 🧭；否则可为 {@code null}。
     * 内部同样使用 {@link AsyncDBContext#cached(Class, DBS, R2Vector)} 作为缓存键并复用实例 ♻️。</p>
     *
     * @param daoCls 实体/DAO 类（用于确定上下文与缓存键）
     * @param dbs    数据源（DBS）🗄️
     * @param vector 字段映射器（R2Vector）🧭，可为 {@code null}
     *
     * @return 对应上下文（含映射器配置）的 DBEx 实例（可能为缓存复用）
     */
    public static DBEx of(final Class<?> daoCls, final DBS dbs, final R2Vector vector) {
        final String cached = AsyncDBContext.cached(daoCls, dbs, vector);
        return CC_DBEX.pick(() -> new DBEx(daoCls, dbs, vector), cached);
    }

    /**
     * 便捷工厂方法：使用【默认/待定数据源】与【字段映射器】创建或获取 DBEx 实例。
     *
     * <p>数据源通过 {@link DBS#waitFor()} 获得（如框架启动阶段注册的默认数据源）⚙️，
     * 其余逻辑与带 {@code dbs} 参数的方法一致。</p>
     *
     * @param daoCls 实体/DAO 类
     * @param vector 字段映射器（R2Vector）🧭
     *
     * @return 使用默认数据源与指定映射器的 DBEx 实例（可能为缓存复用 ♻️）
     */
    public static DBEx of(final Class<?> daoCls, final R2Vector vector) {
        final DBS waitFor = DBS.waitFor();
        return of(daoCls, waitFor, vector);
    }

    /**
     * 最简工厂方法：仅指定【实体/DAO 类】，其余采用默认。
     *
     * <p>数据源使用 {@link DBS#waitFor()} ⚙️，字段映射器为 {@code null}。
     * 适用于无需自定义映射的常规场景 ⚡。</p>
     *
     * @param daoCls 实体/DAO 类
     *
     * @return 使用默认数据源的 DBEx 实例（可能为缓存复用 ♻️）
     */
    public static DBEx of(final Class<?> daoCls) {
        final DBS waitFor = DBS.waitFor();
        return of(daoCls, waitFor, null);
    }
}

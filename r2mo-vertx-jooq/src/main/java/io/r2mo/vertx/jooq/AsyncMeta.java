package io.r2mo.vertx.jooq;

import io.github.jklingsporn.vertx.jooq.classic.VertxDAO;
import io.r2mo.SourceReflect;
import io.r2mo.base.program.R2Vector;
import io.r2mo.dbe.jooq.core.domain.JooqMeta;
import io.r2mo.typed.cc.Cc;
import io.r2mo.typed.exception.web._500ServerInternalException;
import io.vertx.core.Vertx;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.Table;

import java.util.Objects;

/**
 * @author lang : 2025-10-18
 */
@Slf4j
public class AsyncMeta {
    private static final Cc<Class<?>, AsyncMeta> CC_META = Cc.open();
    private final Class<?> daoCls;
    private final JooqMeta metadata;
    @SuppressWarnings("all")
    private VertxDAO dao;
    @Getter
    @Accessors(fluent = true, chain = true)
    private DSLContext context;

    public AsyncMeta vector(final R2Vector vector) {
        this.metadata.vector(vector);
        return this;
    }

    private AsyncMeta(final Class<?> daoCls) {
        this.daoCls = daoCls;
        if (!SourceReflect.isImplement(daoCls, VertxDAO.class)) {
            throw new _500ServerInternalException("[ R2MO ] 仅支持 VertxDAO 类型的 DAO 类：" + daoCls.getName());
        }
        // 提取表名
        final Table<?> table = SourceReflect.value(daoCls, "table");
        // 提取实体类名
        final Class<?> entityCls = SourceReflect.value(daoCls, "type");
        this.metadata = JooqMeta.of(entityCls, table);
    }

    void configure(final DSLContext context, final Vertx vertxRef) {
        final Configuration configuration = context.configuration();
        this.dao = SourceReflect.instance(this.daoCls, configuration, vertxRef);
        this.context = context;
    }

    @SuppressWarnings("all")
    public VertxDAO executor() {
        return this.dao;
    }

    public JooqMeta metaJooq() {
        return this.metadata;
    }

    public Class<?> metaDao() {
        return this.daoCls;
    }

    public Class<?> metaEntity() {
        return Objects.requireNonNull(this.metadata).entityCls();
    }

    public Table<?> metaTable() {
        return Objects.requireNonNull(this.metadata).table();
    }

    // ------------------------------------- 静态方法 -------------------------------------

    /**
     * 此方法负责初始化，而且在全局内存中追加 entityCls = AsyncMeta 的映射关系
     *
     * @param daoCls   VertxDao 实现类
     * @param context  Jooq 上下文
     * @param vertxRef Vertx 对象引用
     *
     * @return AsyncMeta 实例对象
     */
    public static AsyncMeta of(final Class<?> daoCls, final DSLContext context, final Vertx vertxRef) {
        return CC_META.pick(() -> {
            final AsyncMeta instance = new AsyncMeta(daoCls);
            instance.configure(context, vertxRef);
            log.info("[ R2MO ] ( Jooq ) Async 异步初始化完成 hashCode = {}", instance.hashCode());
            return instance;
        }, daoCls);
    }

    public static AsyncMeta getOr(final Class<?> daoCls) {
        return CC_META.getOrDefault(daoCls, null);
    }
}

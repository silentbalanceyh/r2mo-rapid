package io.r2mo.vertx.jooq;

import io.r2mo.SourceReflect;
import io.r2mo.base.program.R2Vector;
import io.r2mo.dbe.jooq.core.domain.JooqMeta;
import io.r2mo.typed.cc.Cc;
import io.r2mo.typed.exception.web._500ServerInternalException;
import io.r2mo.vertx.jooq.classic.VertxDAO;
import io.vertx.core.Vertx;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.Table;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author lang : 2025-10-18
 */
@Slf4j
public class AsyncMeta {
    private static final Cc<Class<?>, AsyncMeta> CC_META = Cc.open();
    /*
     * Fix Bug: DAO 类无法直接通过实体类反向获取
     * [ R2MO ] 无法从实体类中提取元数据，追加一个映射关系向量，可直接通过实体类反向获取元数据信息
     */
    private static final Cc<Class<?>, Class<?>> CC_META_REF = Cc.open();
    private final Class<?> daoCls;
    private final JooqMeta metadata;
    @Getter
    @Accessors(fluent = true, chain = true)
    private final DSLContext context;
    @SuppressWarnings("all")
    private VertxDAO dao;

    private AsyncMeta(final Class<?> daoCls, final DSLContext context, final Vertx vertxRef) {
        this.daoCls = daoCls;
        if (!SourceReflect.isImplement(daoCls, VertxDAO.class)) {
            throw new _500ServerInternalException("[ R2MO ] 仅支持 VertxDAO 类型的 DAO 类：" + daoCls.getName());
        }
        final Configuration configuration = context.configuration();
        @SuppressWarnings("all") final VertxDAO vertxDAO = SourceReflect.instance(this.daoCls, configuration, vertxRef);


        // 设置上下文对象
        this.context = context;


        // 提取表名
        final Table<?> table = SourceReflect.value(vertxDAO, "table");
        // 提取实体类名
        final Class<?> entityCls = SourceReflect.value(vertxDAO, "type");
        CC_META_REF.put(entityCls, daoCls);
        this.dao = vertxDAO;
        this.metadata = JooqMeta.of(entityCls, table);
    }

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
            final AsyncMeta instance = new AsyncMeta(daoCls, context, vertxRef);
            log.info("[ R2MO ] ( Jooq ) Async 异步初始化完成 hashCode = {}", instance.hashCode());
            return instance;
        }, daoCls);
    }

    public static AsyncMeta getOr(final Class<?> daoOrEntity) {
        if (CC_META.containsKey(daoOrEntity)) {
            return CC_META.get(daoOrEntity);
        }
        final Class<?> daoCls = CC_META_REF.getOrDefault(daoOrEntity, daoOrEntity);
        return CC_META.getOrDefault(daoCls, null);
    }

    public AsyncMeta vector(final R2Vector vector) {
        this.metadata.vector(vector);
        return this;
    }

    @SuppressWarnings("all")
    public VertxDAO executor() {
        return this.dao;
    }

    public ConcurrentMap<String, Class<?>> metaTypes() {
        if (Objects.isNull(this.metadata)) {
            return new ConcurrentHashMap<>();
        }
        return this.metadata.fieldType();
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

    // ------------------------------------- 静态方法 -------------------------------------

    public R2Vector metaVector() {
        return Objects.requireNonNull(this.metadata).vector();
    }

    public Table<?> metaTable() {
        return Objects.requireNonNull(this.metadata).table();
    }
}

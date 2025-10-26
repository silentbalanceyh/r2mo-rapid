package io.r2mo.vertx.jooq;

import cn.hutool.core.util.StrUtil;
import io.r2mo.base.dbe.DBS;
import io.r2mo.base.dbe.Join;
import io.r2mo.base.dbe.common.DBLoad;
import io.r2mo.base.dbe.common.DBNode;
import io.r2mo.base.dbe.common.DBRef;
import io.r2mo.dbe.jooq.DBJ;
import io.r2mo.dbe.jooq.spi.LoadREF;
import io.r2mo.spi.SPI;
import io.r2mo.typed.common.Kv;
import io.r2mo.typed.common.MultiKeyMap;
import io.vertx.core.Vertx;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * @author lang : 2025-10-25
 */
class DBJxBase {
    private final DBS dbs;
    private final Vertx vertxRef;
    private final DBJ<?> dbj;
    protected final DBRef ref;

    private final MultiKeyMap<DBEx<?>> dbeMap = new MultiKeyMap<>();

    protected DBJxBase(final DBRef ref, final DBS dbs) {
        this.dbs = dbs;
        this.vertxRef = AsyncDBContext.vertxStatic(dbs);
        Objects.requireNonNull(this.vertxRef, "[ R2MO ] (ADB) 关键步骤 DBS 无法初始化 Vertx 引用！");
        // first + second
        this.afterConstruct(ref, dbs);
        this.ref = ref;
        this.dbj = DBJ.of(ref, dbs);
    }

    private void afterConstruct(final DBRef ref, final DBS dbs) {


        // 🌾 填充 AsyncMeta
        final DBNode nodeLeft = ref.find();
        final Class<?> daoLeft = nodeLeft.dao();
        final DBEx<?> dbeLeft = DBEx.of(daoLeft, dbs, nodeLeft.vector());
        this.dbeMap.put(daoLeft.getName(), dbeLeft, this.keyOf(nodeLeft));
        // 多表模式
        ref.findByExclude(nodeLeft.entity()).forEach(node -> {


            // 🌾 填充 AsyncMeta
            final Class<?> daoRight = node.dao();
            final DBEx<?> dbeRight = DBEx.of(daoRight, dbs, node.vector());
            this.dbeMap.put(daoRight.getName(), dbeRight, this.keyOf(node));
        });
    }

    private String[] keyOf(final DBNode node) {
        final List<String> keys = new ArrayList<>();
        final Class<?> daoCls = node.dao();
        if (Objects.nonNull(daoCls)) {
            keys.add(daoCls.getName());
        }

        final Class<?> entityCls = node.entity();
        if (Objects.nonNull(entityCls)) {
            keys.add(entityCls.getName());
        }

        if (StrUtil.isNotEmpty(node.table())) {
            keys.add(node.table());
        }
        return keys.toArray(new String[0]);
    }

    private void beforeConstruct(final Join join, final DBS dbs) {


        // 🌾 填充 AsyncMeta
        final Class<?> daoLeft = join.from();
        final DBEx<?> dbeLeft = DBEx.of(daoLeft, dbs, join.vFrom());
        this.dbeMap.put(daoLeft.getName(), dbeLeft);


        // 🌾 填充 AsyncMeta
        final Class<?> daoRight = join.to();
        final DBEx<?> dbeRight = DBEx.of(daoRight, dbs, join.vTo());
        this.dbeMap.put(daoRight.getName(), dbeRight);
    }

    protected DBJxBase(final Join join, final DBS dbs) {
        this.dbs = dbs;
        this.vertxRef = AsyncDBContext.vertxStatic(dbs);
        Objects.requireNonNull(this.vertxRef, "[ R2MO ] (ADB) 关键步骤 DBS 无法初始化 Vertx 引用！");

        // 前置注册：第一加载阶段
        this.beforeConstruct(join, dbs);

        // 🍀 双重检查
        LoadREF.of().loadVerify(Set.of(join.from(), join.to()));

        // 加载流程
        final DBLoad loader = SPI.SPI_DB.loader();
        final DBNode leftNode = loader.configure(join.from(), join.vFrom(), dbs);
        final DBNode rightNode = loader.configure(join.to(), join.vTo(), dbs);
        this.ref = DBRef.of(leftNode, rightNode, Kv.create(join.fromField(), join.toField()));
        this.dbj = DBJ.of(this.ref, dbs);
    }

    protected DBEx<?> executor(final Class<?> daoOr) {
        return this.dbeMap.getOr(daoOr.getName());
    }

    protected Vertx refVertx() {
        return this.vertxRef;
    }

    @SuppressWarnings("unchecked")
    protected <T> DBJ<T> refDBJ() {
        return (DBJ<T>) this.dbj;
    }

    protected DBS refDBS() {
        return this.dbs;
    }
}

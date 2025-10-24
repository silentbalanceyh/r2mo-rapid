package io.r2mo.dbe.mybatisplus;

import com.github.yulichang.base.MPJBaseMapper;
import com.github.yulichang.query.MPJQueryWrapper;
import io.r2mo.base.dbe.DBMeta;
import io.r2mo.base.dbe.common.DBLoad;
import io.r2mo.base.dbe.common.DBNode;
import io.r2mo.base.dbe.common.DBRef;
import io.r2mo.base.dbe.operation.QrAnalyzer;
import io.r2mo.dbe.common.DBEJ;
import io.r2mo.dbe.mybatisplus.spi.OpJoinAnalyzer;
import io.r2mo.dbe.mybatisplus.spi.OpJoinImpl;
import io.r2mo.spi.SPI;
import io.r2mo.typed.cc.Cc;
import io.r2mo.typed.common.Kv;

/**
 * @author lang : 2025-10-23
 */
public class DBJ<T> extends DBEJ<MPJQueryWrapper<T>, T, MPJBaseMapper<T>> {
    private final QrAnalyzer<MPJQueryWrapper<T>> analyzer;

    private static final Cc<String, DBJ<?>> CCT_DBE = Cc.openThread();
    private final JoinProxy<T> joinProxy;

    /**
     * 此处的 DBRef 必须是完整的
     *
     * @param ref      DBRef
     * @param executor EXECUTOR
     */
    private DBJ(final DBRef ref, final JoinProxy<T> executor) {
        super(ref, executor.mapper());
        this.joinProxy = executor;
        this.analyzer = new OpJoinAnalyzer<>(ref);
        // 强制执行过程
        this.afterConstruct();
    }

    /*
     * FIX-DBE: 解决 MyBatis-Plus 以及 MyBatis 的一些特殊配置问题，主要是这个框架的 Mapper 必须和实体绑定，如果要执行
     *
     * JOIN 过程中的增删改操作，那么必须在此过程中想办法注入第二执行器才可以，或者注入第三执行器，所以只能用如此不太优雅的方
     * 法来实现注入流程，主要是构造过程中会变得繁琐，如果没有第二执行器，则无法执行实体操作！
     */
    @SuppressWarnings("all")
    protected void afterConstruct() {
        if (this.opJoin instanceof final OpJoinImpl joinAction) {
            joinAction.afterConstruct(this.joinProxy);
        }
    }

    @Override
    protected QrAnalyzer<MPJQueryWrapper<T>> analyzer() {
        return this.analyzer;
    }

    public DBJ<T> alias(final String table, final String field, final String alias) {
        this.ref.alias(table, field, alias);
        return this;
    }

    public DBJ<T> alias(final Class<?> entityCls, final String field, final String alias) {
        final DBNode found = DBMeta.of().findBy(entityCls);
        return this.alias(found.table(), field, alias);
    }

    /**
     * 常见构造，可直接使用
     * <pre>
     *     DBJ.of(Join.of(
     *          classFrom, from,
     *          classTo, to,
     *     )).xxxMethod(???)
     * </pre>
     *
     * @param meta       JOIN 元信息
     * @param baseMapper EXECUTOR
     *
     * @return 当前引用
     */
    public static <T> DBJ<T> of(final Join meta, final JoinProxy<T> baseMapper) {
        // 新版直接使用 DBLoad 来完成节点的构建（一次性构建完成）
        final DBLoad metaLoader = SPI.SPI_DB.loader();
        final DBNode leftNode = metaLoader.configure(meta.from());
        final DBNode rightNode = metaLoader.configure(meta.to());
        final Kv<String, String> kvJoin = Kv.create(meta.fromField(), meta.toField());
        return of(DBRef.of(leftNode, rightNode, kvJoin), baseMapper);
    }

    @SuppressWarnings("unchecked")
    public static <T> DBJ<T> of(final DBRef ref, final JoinProxy<T> mapper) {
        final String cacheKey = ref.hashCode() + "@" + mapper.hashCode();
        /*
         * 调用 DBJ 构造函数之前对 DBRef 进行倒排表的列填充
         */
        return (DBJ<T>) CCT_DBE.pick(() -> new DBJ<>(ref, mapper), cacheKey);
    }
}

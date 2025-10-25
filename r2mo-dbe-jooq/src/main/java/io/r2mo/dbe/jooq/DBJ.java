package io.r2mo.dbe.jooq;

import io.r2mo.base.dbe.DBMeta;
import io.r2mo.base.dbe.DBS;
import io.r2mo.base.dbe.common.DBNode;
import io.r2mo.base.dbe.common.DBRef;
import io.r2mo.base.dbe.operation.QrAnalyzer;
import io.r2mo.dbe.common.DBEJ;
import io.r2mo.dbe.jooq.core.domain.JooqDatabase;
import io.r2mo.dbe.jooq.core.domain.JooqMeta;
import io.r2mo.dbe.jooq.spi.QrAnalyzerJoin;
import io.r2mo.typed.cc.Cc;
import org.jooq.Condition;
import org.jooq.DSLContext;

/**
 * @author lang : 2025-10-24
 */
public class DBJ<T> extends DBEJ<Condition, T, DSLContext> {
    private static final Cc<String, DBJ<?>> CCT_DBE = Cc.openThread();

    private final QrAnalyzer<Condition> analyzer;

    /**
     * 此处的 DBRef 必须是完整的
     *
     * @param ref     DBRef
     * @param context EXECUTOR
     */
    private DBJ(final DBRef ref, final DSLContext context) {
        super(ref, context);
        this.analyzer = new QrAnalyzerJoin(ref);
    }

    @Override
    protected QrAnalyzer<Condition> analyzer() {
        return this.analyzer;
    }

    /**
     * Jooq 有比较特殊的点，是因为它没有注解，所以 {@link DBNode} 中的信息无法直接通过 {@link DBMeta} 获取到，所以在
     * 执行 Join 的过程中最好是直接在外层将 {@link DBRef} 直接构造并且传递过来，有了外层传递的 {@link DBRef} 之后，就
     * 可以在内层和 {@link JooqMeta} 配合执行相对应的操作了，所以 {@link DBMeta} 只在异步调用中会使用到，且异步调用中
     * 不单是针对 {@link Class} 级别的元数据，还会直接从部分实例中提取元数据信息。
     * <pre>
     *   历史原因：
     *   ‼️ 由于 {@link DBNode} 架构出来之前 {@link DBE} 的开发已经告一段落，所以此处不再将 {@link DBMeta} 的加载
     *      架构去替换 {@link JooqMeta}，而是直接和它协作来实现元数据的分析提取。
     * </pre>
     *
     * @param ref DBRef
     * @param dbs DBS
     * @param <T> 泛型
     *
     * @return DBJ
     */
    @SuppressWarnings("unchecked")
    public static <T> DBJ<T> of(final DBRef ref, final DBS dbs) {
        final String cacheKey = ref.hashCode() + "@" + dbs.hashCode();
        final JooqDatabase database = (JooqDatabase) dbs.getDatabase();
        return (DBJ<T>) CCT_DBE.pick(() -> new DBJ<>(ref, database.getContext()), cacheKey);
    }
}

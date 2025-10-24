package io.r2mo.dbe.jooq;

import io.r2mo.base.dbe.DBS;
import io.r2mo.base.dbe.Join;
import io.r2mo.base.dbe.common.DBLoad;
import io.r2mo.base.dbe.common.DBNode;
import io.r2mo.base.dbe.common.DBRef;
import io.r2mo.base.dbe.operation.QrAnalyzer;
import io.r2mo.dbe.common.DBEJ;
import io.r2mo.dbe.jooq.core.domain.JooqDatabase;
import io.r2mo.spi.SPI;
import io.r2mo.typed.cc.Cc;
import io.r2mo.typed.common.Kv;
import org.jooq.Condition;
import org.jooq.DSLContext;

/**
 * @author lang : 2025-10-24
 */
public class DBJ<T> extends DBEJ<Condition, T, DSLContext> {
    private static final Cc<String, DBJ<?>> CCT_DBE = Cc.openThread();

    /**
     * 此处的 DBRef 必须是完整的
     *
     * @param ref     DBRef
     * @param context EXECUTOR
     */
    private DBJ(final DBRef ref, final DSLContext context) {
        super(ref, context);
    }

    @Override
    protected QrAnalyzer<Condition> analyzer() {
        return null;
    }

    public static <T> DBJ<T> of(final Join meta, final DBS dbs) {
        // 新版直接使用 DBLoad 来完成节点构建（一次性构建完成）
        final DBLoad loader = SPI.SPI_DB.loader();
        final DBNode leftNode = loader.configure(meta.from(), meta.vFrom(), dbs);
        final DBNode rightNode = loader.configure(meta.to(), meta.vTo(), dbs);
        final Kv<String, String> kvJoin = Kv.create(meta.fromField(), meta.toField());
        return of(DBRef.of(leftNode, rightNode, kvJoin), dbs);
    }

    @SuppressWarnings("unchecked")
    public static <T> DBJ<T> of(final DBRef ref, final DBS dbs) {
        final String cacheKey = ref.hashCode() + "@" + dbs.hashCode();
        final JooqDatabase database = (JooqDatabase) dbs.getDatabase();
        return (DBJ<T>) CCT_DBE.pick(() -> new DBJ<>(ref, database.getContext()), cacheKey);
    }
}

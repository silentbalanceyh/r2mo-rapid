package io.r2mo.vertx.jooq;

import io.r2mo.base.dbe.DBS;
import io.r2mo.base.dbe.Database;
import io.r2mo.base.dbe.Join;
import io.r2mo.base.dbe.common.DBRef;
import io.r2mo.dbe.jooq.core.domain.JooqDatabase;
import io.r2mo.dbe.jooq.spi.LoadREF;
import io.r2mo.typed.cc.Cc;
import io.r2mo.typed.exception.web._501NotSupportException;
import org.jooq.Table;

import java.util.Map;

/**
 * @author lang : 2025-10-22
 */
public class DBJx extends DBJxBase {
    private static final Cc<String, DBJx> CCT_DBE = Cc.openThread();

    private DBJx(final DBRef ref, final DBS dbs) {
        super(ref, dbs);
    }

    private DBJx(final Join join, final DBS dbs) {
        super(join, dbs);
    }

    private static void ensureDBS(final DBS dbs) {
        // 提取 Database 引用，构造同步专用的 DSLContext
        final Database database = dbs.getDatabase();
        if (!(database instanceof JooqDatabase)) {
            throw new _501NotSupportException("[ R2MO ] JOOQ 模式仅支持 JooqDatabase 类型的数据库引用！");
        }
    }

    public static DBJx of(final DBRef ref, final DBS dbs) {
        ensureDBS(dbs);
        final String cacheKey = ref.hashCode() + "@" + dbs.hashCode();
        return CCT_DBE.pick(() -> new DBJx(ref, dbs), cacheKey);
    }

    public static DBJx of(final Join join, final DBS dbs) {
        ensureDBS(dbs);
        final String cacheKey = join.hashCode() + "@" + dbs.hashCode();
        return CCT_DBE.pick(() -> new DBJx(join, dbs), cacheKey);
    }

    public DBJx alias(final Class<?> daoCls, final String name, final String alias) {
        final Table<?> table = LoadREF.of().loadTable(daoCls);
        this.ref.alias(table.getName(), name, alias);
        return this;
    }

    public DBJx alias(final Class<?> daoCls, Map<String, String> waitFor) {
        final Table<?> table = LoadREF.of().loadTable(daoCls);
        waitFor.forEach((name, alias) -> this.ref.alias(table.getName(), name, alias));
        return this;
    }

    public DBJx alias(final String tableName, final String name, final String alias) {
        this.ref.alias(tableName, name, alias);
        return this;
    }

    public DBJx alias(final String tableName, final Map<String, String> waitFor) {
        waitFor.forEach((name, alias) -> this.ref.alias(tableName, name, alias));
        return this;
    }
}

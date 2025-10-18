package io.r2mo.vertx.jooq;

import io.r2mo.base.dbe.DBS;
import io.r2mo.base.dbe.Database;
import io.r2mo.dbe.jooq.core.domain.JooqDatabase;
import io.r2mo.typed.annotation.SPID;
import io.r2mo.typed.cc.Cc;
import io.r2mo.typed.exception.web._501NotSupportException;
import io.r2mo.vertx.dbe.DBContext;
import io.vertx.core.Vertx;
import org.jooq.DSLContext;

/**
 * @author lang : 2025-10-18
 */
@SPID(DBContext.DEFAULT_CONTEXT_SPID)
public class JooqContext implements DBContext {
    static final Cc<String, Vertx> CC_VECTOR = Cc.open();
    private static final Cc<String, DSLContext> CC_JOOQ = Cc.open();

    @Override
    public <DSL> DSL configure(final DBS dbs, final Vertx vertx) {
        final Database database = dbs.getDatabase();
        if (database instanceof final JooqDatabase jooqDatabase) {
            final DSLContext context = jooqDatabase.getContext();
            final String key = String.valueOf(dbs.hashCode());
            CC_VECTOR.put(key, vertx);
            CC_JOOQ.put(key, context);
        }
        throw new _501NotSupportException("[ R2MO ] 此操作仅支持 JooqDatabase 类型！");
    }

    @Override
    @SuppressWarnings("unchecked")
    public <DSL> DSL context(final Database database) {
        if (database instanceof final JooqDatabase jooqDatabase) {
            return (DSL) jooqDatabase.getContext();
        }
        throw new _501NotSupportException("[ ZERO ] 此操作仅支持 JooqDatabase 类型！");
    }

    @Override
    public Vertx vertx(final DBS dbs) {
        return CC_VECTOR.get(String.valueOf(dbs.hashCode()));
    }
}

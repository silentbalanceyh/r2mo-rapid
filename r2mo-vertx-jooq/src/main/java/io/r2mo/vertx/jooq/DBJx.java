package io.r2mo.vertx.jooq;

import io.r2mo.base.dbe.DBS;
import io.r2mo.base.dbe.common.DBRef;
import io.r2mo.typed.cc.Cc;

/**
 * @author lang : 2025-10-22
 */
public class DBJx extends DBJxBase {
    private static final Cc<String, DBJx> CCT_DBE = Cc.openThread();

    private DBJx(final DBRef ref, final DBS dbs) {
        super(ref, dbs);
    }

    public static DBJx of(final DBRef ref, final DBS dbs) {
        final String cacheKey = ref.hashCode() + "@" + dbs.hashCode();
        return CCT_DBE.pick(() -> new DBJx(ref, dbs), cacheKey);
    }
}

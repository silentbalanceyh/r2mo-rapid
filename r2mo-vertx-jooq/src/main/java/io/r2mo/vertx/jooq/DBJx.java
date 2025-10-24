package io.r2mo.vertx.jooq;

import io.r2mo.base.dbe.DBS;
import io.r2mo.base.dbe.Join;
import io.r2mo.dbe.jooq.DBE;
import io.r2mo.typed.cc.Cc;

/**
 * @author lang : 2025-10-22
 */
public class DBJx extends DBJxBase {
    private static final Cc<String, DBJx> CCT_DBE = Cc.openThread();
    private static final Cc<Class<?>, DBE<?>> CC_ADB = Cc.open();

    private DBJx(final Join join, final DBS dbs) {
        super(join, dbs);
    }
}

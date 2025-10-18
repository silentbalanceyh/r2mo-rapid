package io.r2mo.vertx.dbe;

import io.r2mo.base.dbe.DBMany;
import io.r2mo.base.dbe.DBS;
import io.r2mo.base.dbe.Database;
import io.vertx.core.Vertx;

/**
 * @author lang : 2025-10-18
 */
public interface DBContext {

    String DEFAULT_CONTEXT_SPID = "DEFAULT_CONTEXT_SPID";

    default <DSL> DSL configure(final Vertx vertx) {
        final DBS dbs = DBMany.of().get();
        return this.configure(dbs, vertx);
    }

    <DSL> DSL configure(final DBS dbs, final Vertx vertx);

    <DSL> DSL context(Database database);

    Vertx vertx(DBS dbs);
}

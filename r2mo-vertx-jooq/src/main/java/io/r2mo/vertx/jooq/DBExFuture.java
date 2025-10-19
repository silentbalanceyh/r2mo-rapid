package io.r2mo.vertx.jooq;

import io.r2mo.base.dbe.DBS;
import io.vertx.core.Future;

import java.util.List;

/**
 * @author lang : 2025-10-20
 */
class DBExFuture<T> extends DBExCommon<T> {
    protected DBExFuture(final Class<T> daoCls, final DBS dbs) {
        super(daoCls, dbs);
    }

    public Future<List<T>> findAllAsync() {
        return this.dbeAsync.findAllAsync();
    }
}

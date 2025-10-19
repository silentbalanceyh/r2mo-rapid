package io.r2mo.vertx.jooq;

import io.r2mo.base.dbe.DBS;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;

/**
 * @author lang : 2025-10-20
 */
class DBExJson<T> extends DBExFuture<T> {
    protected DBExJson(final Class<T> daoCls, final DBS dbs) {
        super(daoCls, dbs);
        this.mapped = DBVector.of(this.metadata());
    }

    private final DBVector<T> mapped;

    @SuppressWarnings("all")
    protected DBVector<T> mapped() {
        return (DBVector<T>) this.mapped;
    }

    public JsonArray findAllJ() {
        return this.mapped().outMany(this.dbe.findAll());
    }

    public Future<JsonArray> findAllJAsync() {
        return this.mapped().outMany(this.findAllAsync());
    }
}

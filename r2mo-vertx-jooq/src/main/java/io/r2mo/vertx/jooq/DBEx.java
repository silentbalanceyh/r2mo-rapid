package io.r2mo.vertx.jooq;

import io.r2mo.base.dbe.DBS;
import io.vertx.core.Vertx;

/**
 * 异步模式的 DBE -> Async Database Engine, x - Extension（扩展）
 *
 * @author lang : 2025-10-18
 */
@SuppressWarnings("all")
public class DBEx {
    private final DBS dbs;
    private final Vertx vertxRef;

    private DBEx(final Vertx vertxRef, final DBS dbs) {
        this.vertxRef = vertxRef;
        this.dbs = dbs;
    }
}

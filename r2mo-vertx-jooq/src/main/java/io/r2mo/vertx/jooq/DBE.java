package io.r2mo.vertx.jooq;

import io.github.jklingsporn.vertx.jooq.classic.VertxDAO;

/**
 * @author lang : 2025-10-18
 */
@SuppressWarnings("all")
public class DBE<T> {
    private DBE(final Class<T> entityCls, final VertxDAO executor) {
    }
}

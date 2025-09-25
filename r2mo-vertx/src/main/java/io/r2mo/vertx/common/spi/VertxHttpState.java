package io.r2mo.vertx.common.spi;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.r2mo.typed.webflow.WebState;
import io.r2mo.vertx.common.enums.HttpStatus;

/**
 * @author lang : 2025-09-25
 */
class VertxHttpState implements WebState {
    private final HttpStatus status;

    VertxHttpState(final HttpStatus status) {
        this.status = status;
    }


    @Override
    public String name() {
        return this.status.message();
    }

    @Override
    public int state() {
        return this.status.code();
    }

    @Override
    @SuppressWarnings("unchecked")
    public HttpStatus value() {
        return this.status;
    }
}

package io.r2mo.vertx.common.spi;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.r2mo.typed.webflow.WebState;

/**
 * @author lang : 2025-09-25
 */
class VertxHttpState implements WebState {
    private final HttpResponseStatus status;

    VertxHttpState(final HttpResponseStatus status) {
        this.status = status;
    }


    @Override
    public String name() {
        return this.status.reasonPhrase();
    }

    @Override
    public int state() {
        return this.status.code();
    }

    @Override
    @SuppressWarnings("unchecked")
    public HttpResponseStatus value() {
        return this.status;
    }
}

package io.r2mo.xync.email;

import io.r2mo.base.exchange.BaseContext;

/**
 * @author lang : 2025-12-05
 */
public class EmailContext extends BaseContext {

    public EmailContext setProtocol(final String protocol) {
        this.set(KEY_PROTOCOL, protocol);
        return this;
    }

    public EmailContext setSsl(final boolean ssl) {
        this.set(KEY_SSL, ssl);
        return this;
    }

    public EmailContext setTimeout(final int timeout) {
        this.set(KEY_TIMEOUT, timeout);
        return this;
    }

    public EmailContext setHost(final String host) {
        this.set(KEY_HOST, host);
        return this;
    }

    public EmailContext setPort(final int port) {
        this.set(KEY_PORT, port);
        return this;
    }
}

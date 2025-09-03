package io.r2mo.typed.service;

import io.r2mo.typed.webflow.WebState;

import java.io.Serializable;

/**
 * @author lang : 2025-09-03
 */
class ActCommuneImpl implements ActCommune, Serializable {

    private final WebState state;

    private Object data;

    private String sender;

    private String acceptor;

    ActCommuneImpl(final WebState state) {
        this.state = state;
    }

    public ActCommune setSender(final String sender) {
        this.sender = sender;
        return this;
    }

    public ActCommune setAcceptor(final String acceptor) {
        this.acceptor = acceptor;
        return this;
    }

    public <T> ActCommune bind(final T data) {
        this.data = data;
        return this;
    }

    @Override
    public String ofSender() {
        return this.sender;
    }

    @Override
    public String ofAcceptor() {
        return this.acceptor;
    }

    @Override
    public WebState state() {
        return this.state;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T data() {
        return (T) this.data;
    }
}

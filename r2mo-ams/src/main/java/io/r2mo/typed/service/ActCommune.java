package io.r2mo.typed.service;

import io.r2mo.typed.webflow.WebState;

/**
 * @author lang : 2025-09-03
 */
public interface ActCommune {

    static ActCommune of(WebState webState) {
        return new ActCommuneImpl(webState);
    }

    String ofSender();

    String ofAcceptor();

    WebState state();

    <T> T data();
}

package io.r2mo.spring.security.exception;

import io.r2mo.spring.common.exception.SpringException;

/**
 * @author lang : 2025-11-13
 */
public class _80240Exception400UsernameRequired extends SpringException {
    public _80240Exception400UsernameRequired(final String fieldOfUsername) {
        super(ERR._80240, fieldOfUsername);
    }
}

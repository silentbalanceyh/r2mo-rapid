package io.r2mo.spring.security.exception;

import io.r2mo.spring.common.exception.SpringException;

/**
 * @author lang : 2025-11-13
 */
public class _80241Exception400PasswordRequired extends SpringException {
    public _80241Exception400PasswordRequired(final String fieldOfPassword) {
        super(ERR._80241, fieldOfPassword);
    }
}

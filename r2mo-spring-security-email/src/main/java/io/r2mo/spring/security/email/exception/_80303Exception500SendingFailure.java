package io.r2mo.spring.security.email.exception;

import io.r2mo.spring.common.exception.SpringException;

/**
 * @author lang : 2025-12-08
 */
public class _80303Exception500SendingFailure extends SpringException {
    public _80303Exception500SendingFailure(final String account) {
        super(ERR._80303, account);
    }
}

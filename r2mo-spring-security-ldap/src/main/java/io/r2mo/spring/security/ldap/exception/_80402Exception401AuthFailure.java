package io.r2mo.spring.security.ldap.exception;

import io.r2mo.spring.common.exception.SpringException;

/**
 * @author lang : 2025-12-09
 */
public class _80402Exception401AuthFailure extends SpringException {
    public _80402Exception401AuthFailure(final String account) {
        super(ERR._80402, account);
    }
}

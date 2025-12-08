package io.r2mo.spring.security.sms.exception;

import io.r2mo.spring.common.exception.SpringException;

/**
 * @author lang : 2025-12-08
 */
public class _80382Exception400MobileFormat extends SpringException {
    public _80382Exception400MobileFormat(final String mobile) {
        super(ERR._80382, mobile);
    }
}

package io.r2mo.spring.sms.exception;

import io.r2mo.spring.common.exception.SpringException;

/**
 * @author lang : 2025-12-08
 */
public class _80383Exception500SendingFailure extends SpringException {
    public _80383Exception500SendingFailure(final String mobile) {
        super(ERR._80383, mobile);
    }
}

package io.r2mo.spring.security.sms.exception;

import io.r2mo.spring.common.exception.SpringException;

/**
 * @author lang : 2025-12-08
 */
public class _80383Exception500MobileSending extends SpringException {
    public _80383Exception500MobileSending(final String mobile) {
        super(ERR._80383, mobile);
    }
}

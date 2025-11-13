package io.r2mo.spring.security.exception;

import io.r2mo.spring.common.exception.SpringException;

/**
 * @author lang : 2025-11-13
 */
public class _80242Exception400CaptchaRequired extends SpringException {
    public _80242Exception400CaptchaRequired(final String fieldOfCaptcha) {
        super(ERR._80242, fieldOfCaptcha);
    }
}

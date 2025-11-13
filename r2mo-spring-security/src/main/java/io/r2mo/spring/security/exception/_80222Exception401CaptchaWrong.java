package io.r2mo.spring.security.exception;

import io.r2mo.spring.common.exception.SpringException;

/**
 * @author lang : 2025-11-13
 */
public class _80222Exception401CaptchaWrong extends SpringException {
    public _80222Exception401CaptchaWrong(final String captcha) {
        super(ERR._80222, captcha);
    }
}

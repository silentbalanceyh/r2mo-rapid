package io.r2mo.spring.security.weco.exception;

import io.r2mo.spring.common.exception.SpringException;

/**
 * @author lang : 2025-12-11
 */
public class _80554Exception401WeComBlocked extends SpringException {
    public _80554Exception401WeComBlocked(final String url) {
        super(ERR._80554, url);
    }
}

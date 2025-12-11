package io.r2mo.spring.security.exception;

import io.r2mo.spring.common.exception.SpringAuthenticationException;
import io.r2mo.spring.common.exception.SpringException;

/**
 * @author lang : 2025-12-08
 */
public class _80250Exception401Unauthorized extends SpringException {
    public _80250Exception401Unauthorized(final String identifier, final String details) {
        super(ERR._80250, identifier, details);
    }

    public static class Unauthorized extends SpringAuthenticationException {

        public Unauthorized(final String msg, final String identifier) {
            super(msg, identifier, null);
        }

        @Override
        public SpringException toFailure() {
            return new _80250Exception401Unauthorized(this.identifier, this.getMessage());
        }
    }
}

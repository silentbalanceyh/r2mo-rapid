package io.r2mo.spring.security.exception;

import io.r2mo.spring.common.exception.SpringAuthenticationException;
import io.r2mo.spring.common.exception.SpringException;
import io.r2mo.typed.enums.TypeLogin;

/**
 * @author lang : 2025-12-08
 */
public class _80244Exception401LoginTypeWrong extends SpringException {
    public _80244Exception401LoginTypeWrong() {
        super(ERR._80244);
    }

    public static class Unauthorized extends SpringAuthenticationException {

        public Unauthorized(final String msg) {
            super(msg, null, TypeLogin.PASSWORD);
        }

        @Override
        public SpringException toFailure() {
            return new _80244Exception401LoginTypeWrong();
        }
    }
}

package io.r2mo.spring.security.exception;

import io.r2mo.jaas.enums.TypeLogin;
import io.r2mo.spring.common.exception.SpringException;

/**
 * @author lang : 2025-12-08
 */
public class _80244Exception401LoginTypeWrong extends SpringException {
    public _80244Exception401LoginTypeWrong() {
        super(ERR._80244);
    }

    public static class Unauthorized extends BridgeAuthenticationException {

        public Unauthorized(final String msg) {
            super(msg, null, TypeLogin.PASSWORD);
        }

        @Override
        public SpringException toFailure() {
            return new _80244Exception401LoginTypeWrong();
        }
    }
}

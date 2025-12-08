package io.r2mo.spring.security.exception;

import io.r2mo.jaas.enums.TypeLogin;
import io.r2mo.spring.common.exception.SpringException;
import io.r2mo.typed.exception.WebException;

/**
 * @author lang : 2025-12-08
 */
public class _80204Exception401PasswordNotMatch extends SpringException {
    public _80204Exception401PasswordNotMatch(final String identifier) {
        super(ERR._80204, identifier);
    }

    public static class Unauthorized extends BridgeAuthenticationException {

        public Unauthorized(final String msg, final String identifier) {
            super(msg, identifier, TypeLogin.PASSWORD);
        }

        @Override
        public WebException toFailure() {
            return new _80204Exception401PasswordNotMatch(this.identifier);
        }
    }
}

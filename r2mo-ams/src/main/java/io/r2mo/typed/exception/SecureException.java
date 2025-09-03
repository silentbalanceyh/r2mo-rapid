package io.r2mo.typed.exception;

import io.r2mo.typed.enums.SecurityScope;
import io.r2mo.typed.webflow.WebState;

/**
 * @author lang : 2025-09-03
 */
public abstract class SecureException extends WebException {

    protected SecurityScope scope = SecurityScope.ALL;

    protected SecureException(final WebState status, final String messageKey, final Object... messageArgs) {
        super(status, messageKey, messageArgs);
    }

    protected SecureException(final WebState status, final String messageContent) {
        super(status, messageContent);
    }

    protected SecureException(final WebState status, final Throwable ex) {
        super(status, ex);
    }

    public SecurityScope scope() {
        return this.scope;
    }
}

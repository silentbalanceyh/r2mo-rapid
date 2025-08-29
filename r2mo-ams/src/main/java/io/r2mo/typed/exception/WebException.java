package io.r2mo.typed.exception;

import io.r2mo.typed.process.WebState;
import lombok.Getter;

/**
 * @author lang : 2025-08-28
 */
@Getter
public abstract class WebException extends AbstractException {
    private final WebState status;

    protected WebException(final WebState status, final String messageKey, final Object... messageArgs) {
        super(messageKey, messageArgs);
        this.status = status;
    }

    protected WebException(final WebState status, final String messageContent) {
        super(messageContent);
        this.status = status;
    }

    protected WebException(final WebState status, final Throwable ex) {
        super(ex);
        this.status = status;
    }
}

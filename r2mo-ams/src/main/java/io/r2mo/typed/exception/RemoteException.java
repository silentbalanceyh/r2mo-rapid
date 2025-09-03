package io.r2mo.typed.exception;

import io.r2mo.typed.service.ActCommune;
import io.r2mo.typed.webflow.WebState;

import java.util.Objects;

/**
 * @author lang : 2025-09-03
 */
public abstract class RemoteException extends WebException {

    protected ActCommune message;

    protected RemoteException(final WebState status, final String messageKey, final Object... messageArgs) {
        super(status, messageKey, messageArgs);
        this.message = ActCommune.of(status);
    }

    protected RemoteException(final WebState status, final String messageContent) {
        super(status, messageContent);
        this.message = ActCommune.of(status);
    }

    protected RemoteException(final WebState status, final Throwable ex) {
        super(status, ex);
        this.message = ActCommune.of(status);
    }

    public String service() {
        if (Objects.isNull(this.message)) {
            return null;
        }
        // Acceptor 就是服务通信过程中的接受者，即服务名称
        return this.message.ofAcceptor();
    }
}

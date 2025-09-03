package io.r2mo.typed.webflow;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.r2mo.typed.exception.AbstractException;
import io.r2mo.typed.exception.WebException;
import io.r2mo.typed.exception.web._500ServerInternalException;
import lombok.Data;

import java.io.Serializable;

/**
 * @author lang : 2025-08-28
 */
@Data
class RFailed implements Serializable {
    private WebState status;

    private int code;

    private String message;

    @JsonIgnore
    private AbstractException error;

    RFailed(final Throwable ex) {
        if (ex instanceof final WebException exWeb) {
            this.fromWebException(exWeb);
        } else {
            final _500ServerInternalException ex500 = new _500ServerInternalException(ex.getMessage());
            this.fromWebException(ex500);
        }
    }

    private void fromWebException(final WebException exWeb) {
        this.status = exWeb.getStatus();
        this.code = exWeb.getCode();
        this.message = exWeb.getMessage();
        this.error = exWeb;
    }
}

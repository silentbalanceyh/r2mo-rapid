package io.r2mo.typed.exception.web;

import io.r2mo.spi.SPI;
import io.r2mo.typed.exception.WebException;

/**
 * @author lang : 2025-08-28
 */
public class _405MethodBadException extends WebException {
    public _405MethodBadException(final String messageContent) {
        super(SPI.V_STATUS.V405(), messageContent);
    }

    @Override
    public int getCode() {
        return -10405;
    }

}

package io.r2mo.typed.exception.web;

import io.r2mo.spi.SPI;
import io.r2mo.typed.exception.WebException;

/**
 * @author lang : 2025-08-28
 */
public class _500ServerInternalException extends WebException {
    public _500ServerInternalException(final String messageContent) {
        super(SPI.V_STATUS.V500(), messageContent);
    }

    @Override
    public int getCode() {
        return -10500;
    }
}

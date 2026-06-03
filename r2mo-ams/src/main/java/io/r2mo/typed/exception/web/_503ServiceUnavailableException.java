package io.r2mo.typed.exception.web;

import io.r2mo.spi.SPI;
import io.r2mo.typed.exception.WebException;

public class _503ServiceUnavailableException extends WebException {
    public _503ServiceUnavailableException(final String messageContent) {
        super(SPI.V_STATUS.valueOf(503), messageContent);
    }

    @Override
    public int getCode() {
        return -10503;
    }
}

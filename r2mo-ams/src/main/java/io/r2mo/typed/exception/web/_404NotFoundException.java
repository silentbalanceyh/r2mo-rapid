package io.r2mo.typed.exception.web;

import io.r2mo.spi.SPI;
import io.r2mo.typed.exception.WebException;

/**
 * @author lang : 2025-09-03
 */
public class _404NotFoundException extends WebException {
    public _404NotFoundException(final String messageContent) {
        super(SPI.V_STATUS.V404(), messageContent);
    }

    @Override
    public int getCode() {
        return -10404;
    }
}

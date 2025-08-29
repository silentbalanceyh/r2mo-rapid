package io.r2mo.typed.exception.impl;

import io.r2mo.spi.SPIConnect;
import io.r2mo.typed.exception.WebException;

/**
 * @author lang : 2025-08-28
 */
public class _403ForbiddenException extends WebException {
    public _403ForbiddenException(final String messageContent) {
        super(SPIConnect.SPI_WEB.ofFail403(), messageContent);
    }

    @Override
    public int getCode() {
        return -10403;
    }
}

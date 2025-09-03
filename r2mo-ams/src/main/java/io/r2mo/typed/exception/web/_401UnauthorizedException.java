package io.r2mo.typed.exception.web;

import io.r2mo.spi.SPIConnect;
import io.r2mo.typed.exception.WebException;

/**
 * @author lang : 2025-08-28
 */
public class _401UnauthorizedException extends WebException {
    public _401UnauthorizedException(final String messageContent) {
        super(SPIConnect.STATUS.V401(), messageContent);
    }

    @Override
    public int getCode() {
        return -10401;
    }
}

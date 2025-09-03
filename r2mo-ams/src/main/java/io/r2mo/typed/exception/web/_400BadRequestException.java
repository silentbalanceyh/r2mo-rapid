package io.r2mo.typed.exception.web;

import io.r2mo.spi.SPI;
import io.r2mo.typed.exception.WebException;

/**
 * @author lang : 2025-08-28
 */
public class _400BadRequestException extends WebException {
    public _400BadRequestException(final String messageContent) {
        super(SPI.V_STATUS.V400(), messageContent);
    }

    @Override
    public int getCode() {
        return -10400;
    }
}

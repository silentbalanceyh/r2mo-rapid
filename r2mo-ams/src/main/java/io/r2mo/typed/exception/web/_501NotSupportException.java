package io.r2mo.typed.exception.web;

import io.r2mo.spi.SPI;
import io.r2mo.typed.exception.WebException;

/**
 * @author lang : 2025-08-28
 */
public class _501NotSupportException extends WebException {
    public _501NotSupportException(final String messageContent) {
        super(SPI.V_STATUS.V501(), messageContent);
    }

    @Override
    public int getCode() {
        return -10501;
    }
}

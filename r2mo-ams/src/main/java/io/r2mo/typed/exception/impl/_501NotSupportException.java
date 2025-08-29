package io.r2mo.typed.exception.impl;

import io.r2mo.spi.SPIConnect;
import io.r2mo.typed.exception.WebException;

/**
 * @author lang : 2025-08-28
 */
public class _501NotSupportException extends WebException {
    public _501NotSupportException(final String messageContent) {
        super(SPIConnect.SPI_WEB.ofFail501(), messageContent);
    }

    @Override
    public int getCode() {
        return -10501;
    }
}

package io.r2mo.dbe.common;

import io.r2mo.spi.SPIConnect;

/**
 * @author lang : 2025-08-28
 */
class DBEConfiguration implements SPIConnect {

    private int pBatchSize = 1024;

    public int pBatchSize() {
        return this.pBatchSize;
    }

    public void pBatchSize(final int pBatchSize) {
        this.pBatchSize = pBatchSize;
    }
}

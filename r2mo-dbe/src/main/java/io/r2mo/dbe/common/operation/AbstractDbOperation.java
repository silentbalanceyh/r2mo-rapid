package io.r2mo.dbe.common.operation;

import io.r2mo.base.dbe.operation.QrAnalyzer;
import io.r2mo.spi.SPI;

/**
 * @author lang : 2025-08-28
 */
public class AbstractDbOperation<QR, T, EXECUTOR> {

    private final Class<T> entityCls;
    private final EXECUTOR executor;
    private final QrAnalyzer<QR> qrAnalyzer;

    protected AbstractDbOperation(final Class<T> entityCls, final EXECUTOR executor) {
        this.entityCls = entityCls;
        this.executor = executor;
        this.qrAnalyzer = SPI.SPI_DB.qrAnalyzer(entityCls, executor);
    }

    @SuppressWarnings("all")
    protected <A extends EXECUTOR> A executor() {
        return (A) this.executor;
    }

    protected Class<T> entityCls() {
        return this.entityCls;
    }

    protected QrAnalyzer<QR> analyzer() {
        return this.qrAnalyzer;
    }
}

package io.r2mo.vertx.dbe;

import io.r2mo.base.dbe.operation.QrAnalyzer;
import io.r2mo.spi.SPI;
import io.vertx.core.Future;

import java.util.List;

/**
 * @author lang : 2025-10-20
 */
class AsyncDBET<QR, T, METADATA> {
    private static final FactoryDBAsync factory = SPI.findOne(FactoryDBAsync.class);
    // 操作专用函数
    private final AsyncAggr opAggr;
    private final AsyncDb<T> opDb;
    private final AsyncOne<T> qrOne;
    private final AsyncMany<T> qrMany;

    private final AsyncVary<T, QR> opVary;
    private final QrAnalyzer<QR> qrAnalyzer;

    AsyncDBET(final Class<T> entityCls, final METADATA meta) {
        this.opAggr = factory.opAggr(entityCls, meta);
        this.opDb = factory.opDb(entityCls, meta);
        this.qrOne = factory.qrOne(entityCls, meta);
        this.qrMany = factory.qrMany(entityCls, meta);
        this.opVary = factory.opVary(entityCls, meta);
        this.qrAnalyzer = factory.qrAnalyzer(entityCls, meta);
    }

    public Future<List<T>> findAllAsync() {
        return this.opVary.findAllAsync();
    }
}

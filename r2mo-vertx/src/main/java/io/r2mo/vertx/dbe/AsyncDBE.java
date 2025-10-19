package io.r2mo.vertx.dbe;

import io.r2mo.base.dbe.operation.QrAnalyzer;
import io.r2mo.spi.SPI;
import io.r2mo.typed.cc.Cc;

/**
 * @author lang : 2025-10-19
 */
public class AsyncDBE<QR, T, METADATA> {

    private static final Cc<Class<?>, AsyncDBE<?, ?, ?>> CC_ASYNC = Cc.open();

    private final Class<T> entityCls;
    private final METADATA metadata;
    // 操作专用函数
    private final AsyncAggr opAggr;
    private final AsyncDb<T> opDb;
    private final AsyncOne<T> qrOne;
    private final AsyncMany<T> qrMany;

    private final AsyncVary<T, QR> opVary;
    private final QrAnalyzer<QR> qrAnalyzer;

    private static final FactoryDBAsync factory = SPI.findOne(FactoryDBAsync.class);

    private AsyncDBE(final Class<T> entityCls, final METADATA meta) {
        this.entityCls = entityCls;
        this.metadata = meta;
        this.opAggr = factory.opAggr(entityCls, meta);
        this.opDb = factory.opDb(entityCls, meta);
        this.qrOne = factory.qrOne(entityCls, meta);
        this.qrMany = factory.qrMany(entityCls, meta);
        this.opVary = factory.opVary(entityCls, meta);
        this.qrAnalyzer = factory.qrAnalyzer(entityCls, meta);
    }

    @SuppressWarnings("unchecked")
    public static <QR, T, METADATA, R extends AsyncDBE<QR, T, METADATA>> AsyncDBE<QR, T, METADATA> of(final Class<T> entityCls, final METADATA meta) {
        return (R) CC_ASYNC.pick(() -> new AsyncDBE<>(entityCls, meta), entityCls);
    }
}

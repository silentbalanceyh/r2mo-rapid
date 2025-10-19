package io.r2mo.vertx.dbe;

import io.r2mo.spi.SPI;
import io.r2mo.typed.cc.Cc;
import io.vertx.core.Future;

import java.util.List;

/**
 * @author lang : 2025-10-19
 */
public class AsyncDBE<QR, T, METADATA> {

    private static final Cc<Class<?>, AsyncDBE<?, ?, ?>> CC_ASYNC = Cc.open();

    private final Class<T> entityCls;
    private final METADATA metadata;
    // 操作专用函数
    private final AsyncDBET<QR, T, METADATA> dbeT;

    private static final FactoryDBAsync factory = SPI.findOne(FactoryDBAsync.class);

    private AsyncDBE(final Class<T> entityCls, final METADATA meta) {
        this.entityCls = entityCls;
        this.metadata = meta;
        this.dbeT = new AsyncDBET<>(entityCls, meta);
    }

    @SuppressWarnings("unchecked")
    public static <QR, T, METADATA, R extends AsyncDBE<QR, T, METADATA>> AsyncDBE<QR, T, METADATA> of(final Class<T> entityCls, final METADATA meta) {
        return (R) CC_ASYNC.pick(() -> new AsyncDBE<>(entityCls, meta), entityCls);
    }

    public Future<List<T>> findAllAsync() {
        return this.dbeT.findAllAsync();
    }
}

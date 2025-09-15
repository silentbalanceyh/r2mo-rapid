package io.r2mo.io.local.spi;

import io.r2mo.base.io.HStore;
import io.r2mo.base.io.HTransfer;
import io.r2mo.io.local.operation.HStoreLocal;
import io.r2mo.io.local.service.HTransferLocal;
import io.r2mo.spi.FactoryIo;
import io.r2mo.typed.cc.Cc;

/**
 * @author lang : 2025-09-02
 */
public class FactoryIoLocal implements FactoryIo {
    private static final Cc<String, HStore> CCT_STORE = Cc.openThread();
    private static final Cc<String, HTransfer> CCT_TRANSFER = Cc.openThread();

    @Override
    public HStore ioAction() {
        return CCT_STORE.pick(HStoreLocal::new, HStoreLocal.class.getName());
    }

    @Override
    public HTransfer ioTransfer(final String lookupName) {
        return CCT_TRANSFER.pick(HTransferLocal::new, HTransferLocal.class.getName());
    }
}

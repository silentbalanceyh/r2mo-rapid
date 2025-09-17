package io.r2mo.io.component.node;

import io.r2mo.base.io.modeling.StoreDirectory;
import io.r2mo.base.io.transfer.TransferRequest;
import io.r2mo.io.modeling.TransferResponse;

/**
 * @author lang : 2025-09-17
 */
class StoreInitDirectory implements StoreInit<StoreDirectory> {
    @Override
    public StoreDirectory input(final TransferRequest request) {
        return null;
    }

    @Override
    public TransferResponse output(final StoreDirectory node) {
        return null;
    }
}

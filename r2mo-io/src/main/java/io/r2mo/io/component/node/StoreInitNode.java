package io.r2mo.io.component.node;

import io.r2mo.base.io.modeling.StoreNode;
import io.r2mo.base.io.transfer.TransferRequest;
import io.r2mo.io.modeling.TransferResponse;

/**
 * @author lang : 2025-09-17
 */
class StoreInitNode implements StoreInit<StoreNode> {
    @Override
    public StoreNode input(final TransferRequest request) {
        return null;
    }

    @Override
    public TransferResponse output(final StoreNode node) {
        return null;
    }
}

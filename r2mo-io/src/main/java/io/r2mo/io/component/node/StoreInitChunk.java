package io.r2mo.io.component.node;

import io.r2mo.base.io.modeling.StoreChunk;
import io.r2mo.base.io.transfer.TransferRequest;
import io.r2mo.io.modeling.TransferResponse;

import java.util.List;

/**
 * @author lang : 2025-09-17
 */
class StoreInitChunk implements StoreInit<List<StoreChunk>> {
    @Override
    public List<StoreChunk> input(final TransferRequest request) {
        return null;
    }

    @Override
    public TransferResponse output(final List<StoreChunk> node) {
        return null;
    }
}

package io.r2mo.io.local.service;

import io.r2mo.base.io.modeling.FileRange;
import io.r2mo.base.io.modeling.StoreChunk;
import io.r2mo.base.io.transfer.TransferRequest;
import io.r2mo.base.io.transfer.TransferResult;
import io.r2mo.base.io.transfer.token.TransferTokenService;
import io.r2mo.io.component.node.StoreInit;
import io.r2mo.io.modeling.TransferResponse;
import io.r2mo.io.service.TransferLargeService;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.UUID;

/**
 * @author lang : 2025-09-16
 */
class LocalLargeService extends AbstractTransferService implements TransferLargeService {

    private final StoreInit<List<StoreChunk>> initializer;

    LocalLargeService(final TransferTokenService token) {
        super(token);
        this.initializer = StoreInit.ofChunk();
    }

    @Override
    public TransferResult runUpload(final String token, final InputStream fileData, final StoreChunk chunk) {
        return null;
    }

    @Override
    public TransferResult runDownload(final String token, final OutputStream fileData, final StoreChunk chunk) {
        return null;
    }

    @Override
    public TransferResult runDownload(final String token, final OutputStream fileData, final FileRange range) {
        return null;
    }

    @Override
    public List<StoreChunk> dataUploaded() {
        return List.of();
    }

    @Override
    public List<StoreChunk> dataWaiting() {
        return List.of();
    }

    @Override
    public TransferResponse initialize(final TransferRequest request) {
        return null;
    }

    @Override
    public List<StoreChunk> data(final String token) {
        return List.of();
    }

    @Override
    public List<StoreChunk> data(final UUID id) {
        return List.of();
    }

    @Override
    public TransferResult cancel(final String token) {
        return null;
    }

    @Override
    public TransferResult complete(final String token) {
        return null;
    }
}

package io.r2mo.io.local.service;

import io.r2mo.base.io.modeling.StoreDirectory;
import io.r2mo.base.io.modeling.StoreNode;
import io.r2mo.base.io.transfer.TransferRequest;
import io.r2mo.base.io.transfer.TransferResult;
import io.r2mo.base.io.transfer.token.TransferTokenService;
import io.r2mo.io.component.node.StoreInit;
import io.r2mo.io.modeling.TransferResponse;
import io.r2mo.io.service.TransferDirectoryService;
import io.r2mo.typed.common.Binary;

import java.io.InputStream;
import java.util.List;
import java.util.UUID;

/**
 * @author lang : 2025-09-16
 */
class LocalDirectoryService extends AbstractTransferService implements TransferDirectoryService {

    private final StoreInit<StoreDirectory> initializer;

    LocalDirectoryService(final TransferTokenService token) {
        super(token);
        this.initializer = StoreInit.ofDirectory();
    }

    @Override
    public TransferResult runUpload(final String token, final InputStream fileData) {
        return null;
    }

    @Override
    public Binary runDownload(final String token) {
        return null;
    }

    @Override
    public TransferResponse initialize(final TransferRequest request) {
        return null;
    }

    @Override
    public List<StoreNode> data(final String token) {
        return List.of();
    }

    @Override
    public List<StoreNode> data(final UUID id) {
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

package io.r2mo.io.local.service;

import io.r2mo.base.io.enums.TransferResult;
import io.r2mo.base.io.modeling.StoreNode;
import io.r2mo.io.modeling.TransferRequest;
import io.r2mo.io.modeling.TransferResponse;
import io.r2mo.io.service.TransferDirectoryService;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.UUID;

/**
 * @author lang : 2025-09-16
 */
class LocalDirectoryService implements TransferDirectoryService {
    @Override
    public TransferResult runUpload(final String token, final InputStream fileData, final String filePath) {
        return null;
    }

    @Override
    public TransferResult runDownload(final String token, final OutputStream fileData) {
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

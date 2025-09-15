package io.r2mo.io.local.service;

import io.r2mo.base.io.enums.TransferResult;
import io.r2mo.io.enums.TransferStatus;
import io.r2mo.io.modeling.TransferProgress;
import io.r2mo.io.service.MonitorProgressService;

/**
 * @author lang : 2025-09-16
 */
class LocalProgressService implements MonitorProgressService {
    @Override
    public TransferResult runUpdate(final String token, final Long byteTransferred, final TransferStatus status) {
        return null;
    }

    @Override
    public TransferProgress getProcess(final String token) {
        return null;
    }

    @Override
    public TransferResult runPause(final String token) {
        return null;
    }

    @Override
    public TransferResult runResume(final String token) {
        return null;
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

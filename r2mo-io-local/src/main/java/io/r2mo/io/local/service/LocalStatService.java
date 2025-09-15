package io.r2mo.io.local.service;

import io.r2mo.base.io.enums.TransferResult;
import io.r2mo.io.enums.TransferType;
import io.r2mo.io.modeling.TransferStatistics;
import io.r2mo.io.service.MonitorStatService;

import java.util.UUID;

/**
 * @author lang : 2025-09-16
 */
class LocalStatService implements MonitorStatService {
    @Override
    public TransferResult runRecord(final UUID id, final TransferType type) {
        return null;
    }

    @Override
    public TransferStatistics getStatistics(final UUID id) {
        return null;
    }

    @Override
    public TransferResult syncToDatabase() {
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

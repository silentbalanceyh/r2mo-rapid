package io.r2mo.io.service;

import io.r2mo.base.io.transfer.HTransferAction;
import io.r2mo.base.io.transfer.TransferResult;
import io.r2mo.base.io.transfer.TransferType;
import io.r2mo.io.modeling.TransferStatistics;

import java.util.UUID;

/**
 * @author lang : 2025-09-16
 */
public interface MonitorStatService extends HTransferAction {

    TransferResult runRecord(UUID id, TransferType type);

    TransferStatistics getStatistics(UUID id);

    TransferResult syncToDatabase();
}

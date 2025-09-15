package io.r2mo.io.service;

import io.r2mo.base.io.HTransferAction;
import io.r2mo.base.io.enums.TransferResult;
import io.r2mo.io.enums.TransferStatus;
import io.r2mo.io.modeling.TransferProgress;

/**
 * 传输进度服务
 *
 * @author lang : 2025-09-16
 */
public interface MonitorProgressService extends HTransferAction {

    TransferResult runUpdate(String token, Long byteTransferred, TransferStatus status);

    TransferProgress getProcess(String token);

    TransferResult runPause(String token);

    TransferResult runResume(String token);
}

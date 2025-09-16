package io.r2mo.io.local.service;

import io.r2mo.base.io.HTransfer;
import io.r2mo.base.io.transfer.HTransferAction;
import io.r2mo.base.io.transfer.HTransferService;
import io.r2mo.base.io.transfer.TransferTokenPool;
import io.r2mo.io.service.*;
import io.r2mo.typed.annotation.SPID;
import io.r2mo.typed.cc.Cc;

/**
 * @author lang : 2025-09-16
 */
@SuppressWarnings("unchecked")
@SPID(HTransfer.DEFAULT_ID)
public class HTransferLocal implements HTransfer {

    private static final Cc<String, HTransferAction> CCT_ACTION = Cc.openThread();
    private static final Cc<String, HTransferService<?, ?, ?>> CCT_SERVICE = Cc.openThread();

    @Override
    public TransferFileService serviceOfFile() {
        return (TransferFileService) CCT_SERVICE.pick(LocalFileService::new, LocalFileService.class.getName());
    }

    @Override
    public TransferLargeService serviceOfLarge() {
        return (TransferLargeService) CCT_SERVICE.pick(LocalLargeService::new, LocalLargeService.class.getName());
    }

    @Override
    public TransferDirectoryService serviceOfDirectory() {
        return (TransferDirectoryService) CCT_SERVICE.pick(LocalDirectoryService::new, LocalDirectoryService.class.getName());
    }

    @Override
    public TransferTokenService serviceToken(final TransferTokenPool store) {
        String cacheKey = store == null ? "default" : String.valueOf(store.hashCode());
        cacheKey = cacheKey + "@" + LocalTokenService.class.getName();
        return (TransferTokenService) CCT_SERVICE.pick(() -> new LocalTokenService(store), cacheKey);
    }

    @Override
    public MonitorProgressService actionProgress() {
        return (MonitorProgressService) CCT_ACTION.pick(LocalProgressService::new, LocalProgressService.class.getName());
    }

    @Override
    public MonitorStatService actionStatistics() {
        return (MonitorStatService) CCT_ACTION.pick(LocalStatService::new, LocalStatService.class.getName());
    }
}

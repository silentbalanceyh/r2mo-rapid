package io.r2mo.io.local.service;

import io.r2mo.base.io.HTransfer;
import io.r2mo.base.io.transfer.HTransferAction;
import io.r2mo.base.io.transfer.HTransferService;
import io.r2mo.base.io.transfer.token.TransferTokenPool;
import io.r2mo.base.io.transfer.token.TransferTokenService;
import io.r2mo.io.service.MonitorProgressService;
import io.r2mo.io.service.MonitorStatService;
import io.r2mo.io.service.TransferDirectoryService;
import io.r2mo.io.service.TransferFileService;
import io.r2mo.io.service.TransferLargeService;
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
    public TransferFileService serviceOfFile(final TransferTokenService store) {
        String cacheKey = store == null ? "default" : String.valueOf(store.hashCode());
        cacheKey = cacheKey + "@" + LocalFileService.class.getName();
        return (TransferFileService) CCT_SERVICE.pick(() -> new LocalFileService(store), cacheKey);
    }

    @Override
    public TransferLargeService serviceOfLarge(final TransferTokenService store) {
        String cacheKey = store == null ? "default" : String.valueOf(store.hashCode());
        cacheKey = cacheKey + "@" + LocalLargeService.class.getName();
        return (TransferLargeService) CCT_SERVICE.pick(() -> new LocalLargeService(store), cacheKey);

    }

    @Override
    public TransferDirectoryService serviceOfDirectory(final TransferTokenService store) {
        String cacheKey = store == null ? "default" : String.valueOf(store.hashCode());
        cacheKey = cacheKey + "@" + LocalDirectoryService.class.getName();
        return (TransferDirectoryService) CCT_SERVICE.pick(() -> new LocalDirectoryService(store), cacheKey);
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

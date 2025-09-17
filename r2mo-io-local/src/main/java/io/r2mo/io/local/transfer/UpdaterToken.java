package io.r2mo.io.local.transfer;

import io.r2mo.base.io.transfer.token.TransferTokenService;
import io.r2mo.typed.cc.Cc;

/**
 * @author lang : 2025-09-18
 */
public class UpdaterToken {
    private static final Cc<String, UpdaterToken> CCT_PROGRESS = Cc.openThread();

    private final TransferTokenService service;

    private UpdaterToken(final TransferTokenService service) {
        this.service = service;
    }

    public static UpdaterToken of(final TransferTokenService service) {
        return CCT_PROGRESS.pick(() -> new UpdaterToken(service), String.valueOf(service.hashCode()));
    }
}

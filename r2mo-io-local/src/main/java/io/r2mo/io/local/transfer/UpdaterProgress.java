package io.r2mo.io.local.transfer;

import io.r2mo.typed.cc.Cc;

/**
 * @author lang : 2025-09-18
 */
public class UpdaterProgress {
    private static final Cc<String, UpdaterProgress> CCT_PROGRESS = Cc.openThread();

    private UpdaterProgress() {
    }

    public static UpdaterProgress of() {
        return CCT_PROGRESS.pick(UpdaterProgress::new);
    }
}

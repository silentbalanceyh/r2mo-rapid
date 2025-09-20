package io.r2mo.io.local.transfer;

import io.r2mo.base.io.HStore;
import io.r2mo.base.io.HUri;
import io.r2mo.base.io.modeling.StoreFile;
import io.r2mo.spi.SPI;
import io.r2mo.typed.cc.Cc;
import io.r2mo.typed.common.Binary;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lang : 2025-09-18
 */
@Slf4j
public class TransDownload {
    private static final Cc<String, TransDownload> CCT_DOWNLOAD = Cc.openThread();

    private TransDownload() {
    }

    public static TransDownload of() {
        return CCT_DOWNLOAD.pick(TransDownload::new);
    }

    public Binary read(final StoreFile file) {
        final HStore store = SPI.V_STORE;
        final String path = HUri.UT.resolve(store.pHome(), file.getStorePath());
        log.info("[ R2MO ] - 准备下载文件，存储路径：{}", path);
        return store.inBinary(path);
    }
}

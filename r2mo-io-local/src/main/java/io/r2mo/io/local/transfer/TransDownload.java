package io.r2mo.io.local.transfer;

import io.r2mo.base.io.HStore;
import io.r2mo.base.io.HUri;
import io.r2mo.base.io.modeling.FileRange;
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

    private static Result getPath(String relatePath) {
        HStore store = SPI.V_STORE;
        String path = HUri.UT.resolve(store.pHome(), relatePath);
        log.info("[ R2MO ] - 准备下载分块，存储路径：{}", path);
        return new Result(store, path);
    }

    public Binary read(StoreFile file) {
        return this.read(file.getStorePath());
    }

    public Binary read(String relatePath, final FileRange range) {
        Result result = getPath(relatePath);
        log.info("[ R2MO ] -读取文件：{} ，范围：{} ", relatePath, range);
        return result.store().inBinary(result.path(), range, null);
    }

    public Binary read(String relatePath) {
        Result result = getPath(relatePath);
        return result.store().inBinary(result.path);
    }

    private record Result(HStore store, String path) {
    }

}

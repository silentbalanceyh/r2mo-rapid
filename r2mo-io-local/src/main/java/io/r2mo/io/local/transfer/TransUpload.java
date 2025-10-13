package io.r2mo.io.local.transfer;

import io.r2mo.base.io.HStore;
import io.r2mo.base.io.HUri;
import io.r2mo.base.io.modeling.StoreChunk;
import io.r2mo.base.io.modeling.StoreFile;
import io.r2mo.spi.SPI;
import io.r2mo.typed.cc.Cc;

import java.io.InputStream;

/**
 * @author lang : 2025-09-18
 */
public class TransUpload {
    private static final Cc<String, TransUpload> CCT_UPLOAD = Cc.openThread();

    private TransUpload() {
    }

    public static TransUpload of() {
        return CCT_UPLOAD.pick(TransUpload::new);
    }

    public boolean write(final StoreFile file, final InputStream in) {
        return this.write(file.getStorePath(), in);
    }

    public boolean write(final String relatePath, final InputStream in) {
        final HStore store = SPI.V_STORE;
        final String path = HUri.UT.resolve(store.pHome(), relatePath);
        return store.write(path, in);
    }

    public boolean rm(final StoreChunk chunk) {
        final HStore store = SPI.V_STORE;
        final String path = HUri.UT.resolve(store.pHome(), chunk.getStorePath());
        return store.rm(path);
    }

}

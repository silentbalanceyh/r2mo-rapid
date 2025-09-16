package io.r2mo.io.spi;

import cn.hutool.core.util.StrUtil;
import io.r2mo.base.io.HStore;
import io.r2mo.base.io.HTransfer;
import io.r2mo.spi.FactoryIo;
import io.r2mo.spi.SPI;
import io.r2mo.typed.cc.Cc;

/**
 * @author lang : 2025-09-02
 */
public class FactoryIoCommon implements FactoryIo {
    private static final Cc<String, HStore> CCT_STORE = Cc.openThread();
    private static final Cc<String, HTransfer> CCT_TRANSFER = Cc.openThread();

    @Override
    public HStore ioAction(final String name) {
        if (StrUtil.isEmpty(name)) {
            return null;
        }
        return CCT_STORE.pick(() -> SPI.findOne(HStore.class, name), name);
    }

    @Override
    public HTransfer ioTransfer(final String name) {
        if (StrUtil.isEmpty(name)) {
            return null;
        }
        return CCT_TRANSFER.pick(() -> SPI.findOne(HTransfer.class, name), name);
    }
}

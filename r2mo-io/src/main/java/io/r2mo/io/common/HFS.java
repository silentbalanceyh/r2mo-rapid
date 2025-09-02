package io.r2mo.io.common;

import io.r2mo.base.io.HStore;
import io.r2mo.spi.SPIConnect;
import io.r2mo.typed.cc.Cc;

/**
 * 统一访问接口，用来完成和 IO 直接对接的部分，而 Zero 中实现的是异步模式的继承，来完成整体对接部分，替换原始的 HFS
 * 的 IO 操作
 *
 * @author lang : 2025-09-01
 */
public class HFS {
    private static final Cc<String, HFS> CCT_HFS = Cc.openThread();
    private final HStore store;

    protected HFS() {
        this.store = SPIConnect.SPI_IO.ioAction();
    }

    public static HFS of() {
        return CCT_HFS.pick(HFS::new);
    }
}

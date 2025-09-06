package io.r2mo.io.local.spi;

import io.r2mo.base.io.HPath;
import io.r2mo.base.io.HStore;
import io.r2mo.io.local.HStoreLocal;
import io.r2mo.spi.FactoryIo;
import io.r2mo.spi.SPI;
import io.r2mo.typed.cc.Cc;

import java.util.Objects;

/**
 * @author lang : 2025-09-02
 */
public class FactoryIoLocal implements FactoryIo {
    private static final Cc<String, HStore> CCT_STORE = Cc.openThread();
    private static final Cc<String, HPath> CCT_PATH = Cc.openThread();

    @Override
    public HStore ioAction() {
        return CCT_STORE.pick(HStoreLocal::new, HStoreLocal.class.getName());
    }

    @Override
    public HPath ioPath() {
        return CCT_PATH.pick(() -> SPI.findOne(HPath.class, HPath.PATH_DEFAULT_NAME), HPath.PATH_DEFAULT_NAME);
    }

    @Override
    public HPath ioPath(final String name) {
        if (HPath.PATH_DEFAULT_NAME.equals(name)) {
            throw new UnsupportedOperationException("[ R2MO ] 不可以用 ioPath(String) 获取默认组件，请切换！");
        }
        if (Objects.isNull(name)) {
            return null;
        }
        return CCT_PATH.pick(() -> SPI.findOne(HPath.class, name), name);
    }
}

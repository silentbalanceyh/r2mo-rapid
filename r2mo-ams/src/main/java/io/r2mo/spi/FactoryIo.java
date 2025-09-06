package io.r2mo.spi;

import io.r2mo.base.io.HPath;
import io.r2mo.base.io.HStore;

/**
 * @author lang : 2025-08-28
 */
public interface FactoryIo {

    HStore ioAction();

    HPath ioPath();

    HPath ioPath(String name);
}

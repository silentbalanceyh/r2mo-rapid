package io.r2mo.spi;

import io.r2mo.base.io.HStore;
import io.r2mo.base.io.HTransfer;

/**
 * 理论上，传输行为和底层的 {@link HStore} 是可以分离的，但为了保证完整的一致性，在目前版本中将二者合并到一起形成绑定关系，
 * 简单说，如果您选择的实现是本地实现，那么传输行为也只能是本地的，如果是云存储实现，那么传输行为也只能是云存储的，于是此时的
 * {@link FactoryIo} 负责存储介质的选择和传输行为的选择。二者职责如下
 * <pre>
 *     1. {@link HStore} 负责底层的IO操作
 *     2. {@link HTransfer} 负责传输行为如上传、下载、大文件
 * </pre>
 *
 * @author lang : 2025-08-28
 */
public interface FactoryIo {

    HStore ioAction(String name);

    default HStore ioAction() {
        return ioAction(null);
    }

    HTransfer ioTransfer(String name);

    default HTransfer ioTransfer() {
        return ioTransfer(null);
    }
}

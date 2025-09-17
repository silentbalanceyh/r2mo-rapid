package io.r2mo.io.local.service;

import io.r2mo.base.io.transfer.TransferTokenPool;
import io.r2mo.spi.SPI;
import io.r2mo.typed.json.JUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * @author lang : 2025-09-17
 */
@Slf4j
public abstract class AbstractTransferService {
    protected static final JUtil UT = SPI.V_UTIL;
    protected final TransferTokenPool cache;

    protected AbstractTransferService(final TransferTokenPool cache) {
        if (Objects.isNull(cache)) {
            log.info("[ R2MO ] 使用默认本地令牌池");
            this.cache = new LocalTokenPool();
        } else {
            log.info("[ R2MO ] 使用自定义令牌池: {}", cache.getClass().getName());
            this.cache = cache;
        }
    }
}

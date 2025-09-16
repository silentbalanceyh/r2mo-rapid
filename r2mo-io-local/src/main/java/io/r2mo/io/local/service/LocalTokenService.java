package io.r2mo.io.local.service;

import io.r2mo.base.io.transfer.TransferToken;
import io.r2mo.base.io.transfer.TransferTokenPool;
import io.r2mo.io.modeling.TransferRequest;
import io.r2mo.io.service.TransferTokenService;
import io.r2mo.spi.SPI;
import io.r2mo.typed.json.JObject;
import io.r2mo.typed.json.JUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * @author lang : 2025-09-16
 */
@Slf4j
class LocalTokenService implements TransferTokenService {
    private static final JUtil UT = SPI.V_UTIL;
    private final TransferTokenPool cache;

    public LocalTokenService(final TransferTokenPool cache) {
        if (Objects.isNull(cache)) {
            log.info("[ R2MO ] 使用默认本地令牌池");
            this.cache = new LocalTokenPool();
        } else {
            log.info("[ R2MO ] 使用自定义令牌池: {}", cache.getClass().getName());
            this.cache = cache;
        }
    }

    @Override
    public TransferToken runValidate(final String token) {
        return null;
    }

    @Override
    public TransferToken getToken(final String token) {
        return null;
    }

    @Override
    public boolean runRevoke(final String token) {
        return false;
    }

    @Override
    public boolean runExtend(final String token, final long expireSeconds) {
        return false;
    }

    @Override
    public TransferToken initialize(final TransferRequest request) {
        return null;
    }

    @Override
    public List<JObject> data(final String token) {
        return List.of();
    }

    @Override
    public List<JObject> data(final UUID id) {
        return List.of();
    }
}

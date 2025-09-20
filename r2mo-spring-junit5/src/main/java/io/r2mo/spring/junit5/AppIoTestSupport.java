package io.r2mo.spring.junit5;

import io.r2mo.io.common.HFS;
import io.r2mo.spi.SPI;
import io.r2mo.typed.json.JArray;
import io.r2mo.typed.json.JObject;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author lang : 2025-09-07
 */
@Slf4j
public abstract class AppIoTestSupport {

    protected HFS fs() {
        return HFS.of();
    }

    protected <T> T inOne(final String filename, final Class<T> clazz) {
        final JObject mappedEntity = this.fs().inJson(filename);
        log.info("[ R2MOMO ] ( One ) 读取测试数据：{} -> {}", filename, mappedEntity.encodePretty());
        return SPI.V_UTIL.deserializeJson(mappedEntity, clazz);
    }

    protected <T> List<T> inMany(final String filename, final Class<T> clazz) {
        final JArray mappedEntity = this.fs().inJson(filename);
        log.info("[ R2MOMO ] ( Many ) 读取测试数据：{} -> {}", filename, mappedEntity.encodePretty());
        return SPI.V_UTIL.deserializeJson(mappedEntity, clazz);
    }
}

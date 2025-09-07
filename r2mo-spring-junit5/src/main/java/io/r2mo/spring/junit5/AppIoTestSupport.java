package io.r2mo.spring.junit5;

import io.r2mo.io.common.HFS;
import io.r2mo.spi.SPI;
import io.r2mo.typed.json.JArray;
import io.r2mo.typed.json.JObject;

import java.util.List;

/**
 * @author lang : 2025-09-07
 */
public abstract class AppIoTestSupport {

    protected HFS fs() {
        return HFS.of();
    }

    protected <T> T inOne(final String filename, final Class<T> clazz) {
        final JObject mappedEntity = this.fs().inJson(filename);
        return SPI.V_UTIL.deserializeJson(mappedEntity, clazz);
    }

    protected <T> List<T> inMany(final String filename, final Class<T> clazz) {
        final JArray mappedEntity = this.fs().inJson(filename);
        return SPI.V_UTIL.deserializeJson(mappedEntity, clazz);
    }
}

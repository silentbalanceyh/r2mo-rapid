package io.r2mo.spring.junit5;

import io.r2mo.io.common.HFS;
import io.r2mo.spi.SPI;
import io.r2mo.typed.json.JArray;
import io.r2mo.typed.json.JObject;
import io.r2mo.typed.json.JUtil;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.util.List;

/**
 * @author lang : 2025-09-07
 */
@Slf4j
public abstract class AppIoTestSupport {

    protected static JUtil UT = SPI.V_UTIL;

    protected HFS fs() {
        return HFS.of();
    }

    protected <T> T inOne(final String filename, final Class<T> clazz) {
        final JObject mappedEntity = this.fs().inJson(filename);
        log.info("[ R2MOMO ] ( One ) 读取测试数据：{} -> {}", filename, mappedEntity.encodePretty());
        return UT.deserializeJson(mappedEntity, clazz);
    }

    protected <T> List<T> inMany(final String filename, final Class<T> clazz) {
        final JArray mappedEntity = this.fs().inJson(filename);
        log.info("[ R2MOMO ] ( Many ) 读取测试数据：{} -> {}", filename, mappedEntity.encodePretty());
        return UT.deserializeJson(mappedEntity, clazz);
    }

    protected JObject inJObject(final String filename) {
        final JObject json = this.fs().inJson(filename);
        log.info("[ R2MOMO ] ( JObject ) 读取测试数据：{} -> {}", filename, json.encodePretty());
        return json;
    }

    protected JArray inJArray(final String filename) {
        final JArray json = this.fs().inJson(filename);
        log.info("[ R2MOMO ] ( JArray ) 读取测试数据：{} -> {}", filename, json.encodePretty());
        return json;
    }

    protected <T> T inOne(final String filename, final Class<T> clazz, final ClassLoader loader) {
        final URL url = loader.getResource(filename);
        final JObject mappedEntity = this.fs().inJson(url);
        log.info("[ R2MOMO ] ( One ) CP / 读取测试数据：{} -> {}", filename, mappedEntity.encodePretty());
        return UT.deserializeJson(mappedEntity, clazz);
    }

    protected <T> T inCPOne(final String filename, final Class<T> clazz) {
        return this.inOne(filename, clazz, Thread.currentThread().getContextClassLoader());
    }

    protected <T> List<T> inMany(final String filename, final Class<T> clazz, final ClassLoader loader) {
        final URL url = loader.getResource(filename);
        final JArray mappedEntity = this.fs().inJson(url);
        log.info("[ R2MOMO ] ( Many ) CP / 读取测试数据：{} -> {}", filename, mappedEntity.encodePretty());
        return UT.deserializeJson(mappedEntity, clazz);
    }

    protected <T> List<T> inCPMany(final String filename, final Class<T> clazz) {
        return this.inMany(filename, clazz, Thread.currentThread().getContextClassLoader());
    }

    protected JObject inJObject(final String filename, final ClassLoader loader) {
        final URL url = loader.getResource(filename);
        final JObject json = this.fs().inJson(url);
        log.info("[ R2MOMO ] ( JObject ) CP / 读取测试数据：{} -> {}", filename, json.encodePretty());
        return json;
    }

    protected JObject inCPJObject(final String filename) {
        return this.inJObject(filename, Thread.currentThread().getContextClassLoader());
    }

    protected JArray inJArray(final String filename, final ClassLoader loader) {
        final URL url = loader.getResource(filename);
        final JArray json = this.fs().inJson(url);
        log.info("[ R2MOMO ] ( JArray ) CP / 读取测试数据：{} -> {}", filename, json.encodePretty());
        return json;
    }

    protected JArray inCPJArray(final String filename) {
        return this.inJArray(filename, Thread.currentThread().getContextClassLoader());
    }
}

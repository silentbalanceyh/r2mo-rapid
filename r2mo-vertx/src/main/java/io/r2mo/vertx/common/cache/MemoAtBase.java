package io.r2mo.vertx.common.cache;

import io.r2mo.typed.cc.CacheAtBase;
import io.vertx.core.Vertx;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * 缓存实现基类
 * <p>
 * 核心职责：
 * 1. 管理缓存实例的单例池 (CC_MEMO)
 * 2. 管理配置项的单例池 (CC_OPTION) - 确保相同指纹的配置只有一份
 * 3. 提供统一的属性访问
 *
 * @author lang : 2026-01-01
 */
@Slf4j
public abstract class MemoAtBase<K, V> implements MemoAt<K, V> {

    private final Vertx vertxRef;
    private final MemoOptions<K, V> options;

    /**
     * 参考 {@link CacheAtBase} 中的注释，数据结构和设计结构是一致的
     *
     * @param vertxRef 容器引用器
     * @param options  缓存配置项
     */
    protected MemoAtBase(final Vertx vertxRef, final MemoOptions<K, V> options) {
        if (Objects.isNull(options)) {
            throw new IllegalArgumentException("[ R2MO ] 缓存构造时候配置 options 不可为空！");
        }
        this.options = options;
        this.vertxRef = vertxRef;
    }

    @Override
    public String name() {
        return this.options.name();
    }

    protected Vertx vertx() {
        return this.vertxRef;
    }

    protected Class<K> classKey() {
        return this.options.classK();
    }

    protected Class<V> classValue() {
        return this.options.classV();
    }

    protected MemoOptions<K, V> options() {
        return this.options;
    }
}
package io.r2mo.typed.cc;

import lombok.extern.slf4j.Slf4j;

import java.time.Duration;

/**
 * @author lang : 2025-11-12
 */
@Slf4j
public abstract class CacheAtBase<K, V> implements CacheAt<K, V> {
    private final String name;
    private boolean initialized = false;
    // 子类可访问的基础配置属性
    protected Duration duration;
    protected long size = -1;

    protected CacheAtBase(final String name) {
        this.name = name;
    }

    @Override
    public boolean isOk() {
        return this.initialized;
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public void configure(final Duration duration, final long size) {
        this.duration = duration;
        this.size = size;
        if (0 >= size) {
            throw new IllegalArgumentException("[ R2MO ] 缓存尺寸必须大于 0 ！");
        }
        this.initialized = this.build();
        log.info("[ R2MO ] --> 缓存：{}({}) / 超时：{}", this.name, size, duration.toString());
    }

    protected abstract boolean build();
}

package io.r2mo.vertx.common.cache;

import io.r2mo.SourceReflect;
import io.r2mo.typed.cc.CacheAtBase;
import io.r2mo.typed.cc.Cc;
import io.vertx.core.Vertx;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.Objects;

/**
 * @author lang : 2026-01-01
 */
@Slf4j
public abstract class MemoAtBase<K, V> implements MemoAt<K, V> {
    protected static final Cc<String, MemoAt<?, ?>> CC_MEMO = Cc.openThread();
    private final String name;
    private final String manager;
    private final MemoOptions<K, V> options;

    /**
     * 参考 {@link CacheAtBase} 中的注释，数据结构和设计结构是一致的
     *
     * @param name    缓存名称
     * @param options 缓存配置项
     */
    protected MemoAtBase(final String name, final MemoOptions<K, V> options) {
        if (Objects.isNull(options)) {
            throw new IllegalArgumentException("[ ZERO ] 缓存构造时候配置 options 不可为空！");
        }
        if (!options.isOk()) {
            throw new IllegalStateException("[ ZERO ] 缓存配置 MemoOption<K,V> 不符合参数规范，请检查输入！");
        }
        this.name = name;
        this.options = options;

        final Duration duration = options.duration();
        this.manager = options.keyCache(this.getClass());
        // ISO 8601 标准时间格式
        log.info("[ R2MO ] --> 异步缓存：{}({}) / 超时：{}", this.name, options.size(), duration);
    }

    @SuppressWarnings("unchecked")
    protected static <K, V> MemoAt<K, V> of(final String name, final MemoOptions<K, V> options,
                                            final Class<?> caller) {
        final String keyCache = options.keyCache(caller) + "@/" + name;
        return (MemoAt<K, V>) CC_MEMO.pick(() -> SourceReflect.instance(caller, name, options), keyCache);
    }

    @Override
    public String name() {
        return this.name;
    }

    /**
     * 返回当前 manager 名称，可能会存在
     * <pre>
     *     name 01 -> Manager
     *     name 02 ->
     *     name 03 ->
     * </pre>
     * 上述上个名称可能会返回同一个 Manager 实例，这种只在子类模式中使用，不在外层使用，主要是减少 Manager 的实例数量，Manager 本身绑定的是配置信息
     *
     * @return Manager 管理器名称
     */
    protected String manager() {
        return this.manager;
    }

    protected Vertx vertx() {
        return this.options.vertx();
    }

    protected Class<K> classKey() {
        return this.options.classK();
    }

    protected Class<V> classValue() {
        return this.options.classV();
    }
}

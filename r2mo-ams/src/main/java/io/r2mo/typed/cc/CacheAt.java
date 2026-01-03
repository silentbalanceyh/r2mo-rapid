package io.r2mo.typed.cc;

import io.r2mo.typed.common.Kv;

import java.time.Duration;

/**
 * 设置可超时的简易缓存接口
 *
 * @author lang : 2025-11-12
 */
public interface CacheAt<K, V> {

    String name();

    /**
     * 缓存内部配置了 initialized 标记位，此标记位会在调用此方法后被设置为 true，表示缓存已初始化完成，此处之所以如此处理，原因在于
     * 部分缓存实现中这两个维度的数据是构造时必须的，而部分数据是读写数据的时候设置的 TTL 机制，为了兼容两种机制，提供二阶段的初始化
     * 流程
     * <pre>
     *     1. 第一阶段 --> 构造缓存实例，此时 initialized = false
     *        对于构造时候不需要此两个维度的数据的缓存实现，可以在此阶段构造完成后直接将 initialized 设置为 true，这种通常是
     *        在读写数据的时候提供 TTL 机制的缓存实现
     *     2. 第二阶段 --> 调用此方法进行配置，此时 initialized = true
     *
     *     3. 不仅如此，有些缓存实现中 size 并非必须，也可将 size 设置成 -1，表示不限制尺寸
     * </pre>
     * 当前接口的这些操作主要用于兼容不同缓存实现的初始化需求，且也为了让同异步都可以统一实现而设计，这种模式下的设计可以增强缓存的
     * 扩展性，两种缓存机制虽然不同，但最终可实现职责统一和功能统一。
     *
     * @param duration 缓存存活时间
     * @param size     尺寸
     */
    void configure(Duration duration, long size);

    void put(K key, V value);

    default void put(final Kv<K, V> kv) {
        this.put(kv.key(), kv.value());
    }

    boolean remove(K key);

    V find(K key);

    void clear();

    boolean isOk();
}

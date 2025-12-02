package io.r2mo.typed.cc;

import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.Objects;

/**
 * @author lang : 2025-11-12
 */
@Slf4j
public abstract class CacheAtBase<K, V> implements CacheAt<K, V> {
    private final Class<K> clazzK;
    private final Class<V> clazzV;
    private final String name;

    private String manager;
    private boolean initialized = false;
    // 子类可访问的基础配置属性
    protected Duration duration;
    protected long size = -1;

    protected CacheAtBase(final String name, final Class<K> clazzK, final Class<V> clazzV) {
        this.name = name;
        this.clazzK = clazzK;
        this.clazzV = clazzV;
    }

    @Override
    public boolean isOk() {
        return this.initialized;
    }

    @Override
    public String name() {
        return this.name;
    }

    /**
     * 返回当前的 Manager 名称，可能会存在
     * <pre>
     *     name 01 -> Manager
     *     name 02 ->
     *     name 03 ->
     * </pre>
     * 上述三个名称返回了同一个 Manager 实例，这种模式只在子类中使用，不在外层接口使用，其目的是
     * 减少 Manager 实例的创建数量，从而节省资源开销。
     *
     * @return Manager 名称
     */
    protected String manager() {
        return this.manager;
    }

    /**
     * 配置缓存参数，配置过程中遵循几个原则
     * <pre>
     *     1. name 属性表示的是缓存的名称，对应
     *        Manager -> name-01
     *                -> name-02
     *                -> ...
     *        简单说一个 Manager 中可能会根据名称创建多个缓存实例
     *     2. managerName 主要包含四个维度
     *        - 实现类：this.getClass().getName()
     *        - key / value 类型：this.clazzK.getName() / this.clazzV.getName()
     *        - size：缓存尺寸
     *        - duration：超时时间
     *     3. 根据上述四个维度的不同组合，最终形成唯一的 managerName，从而保证缓存实例的唯一性
     * </pre>
     *
     * @param duration 超时时间
     * @param size     缓存尺寸
     */
    @Override
    public void configure(final Duration duration, final long size) {
        this.duration = duration;
        this.size = size;
        if (0 >= size) {
            throw new IllegalArgumentException("[ R2MO ] 缓存尺寸必须大于 0 ！");
        }
        this.manager = this.getClass().getName() + "@"
            + this.clazzK.getName() + "=" + this.clazzV.getName()
            + "S=" + size
            + "D=" + duration.hashCode();
        this.initialized = this.build();
        log.info("[ R2MO ] --> 缓存：{}({}) / 超时：{}", this.name, size, duration.toString());
    }

    // 初始化专用方法
    protected abstract boolean build();

    protected Class<K> classKey() {
        return this.clazzK;
    }

    protected Class<V> classValue() {
        return this.clazzV;
    }

    // -------------------- 防御式处理 --------------------

    /**
     * 此处采用二级模式，先检查初始化状态，再调用子类的具体实现方法，具体方法由子类实现
     *
     * @param key   缓存键
     * @param value 缓存值
     */
    @Override
    public void put(final K key, final V value) {
        if (!this.initialized) {
            log.warn("[ R2MO ] 缓存 {} 未初始化，无法使用 put 操作！", this.name);
        }
        if (Objects.isNull(key) || Objects.isNull(value)) {
            log.warn("[ R2MO ] 缓存 {} 不允许存储 null 值！", this.name);
            return;
        }
        this.doPut(key, value);
    }

    protected abstract void doPut(final K key, final V value);

    @Override
    public boolean remove(final K key) {
        if (!this.initialized) {
            log.warn("[ R2MO ] 缓存 {} 未初始化，无法使用 remove 操作！", this.name);
            return false;
        }
        if (Objects.isNull(key)) {
            log.warn("[ R2MO ] 缓存 {} 不允许删除 null 键！", this.name);
            return false;
        }
        return this.doRemove(key);
    }

    protected abstract boolean doRemove(final K key);

    @Override
    public V find(final K key) {
        if (!this.initialized) {
            log.warn("[ R2MO ] 缓存 {} 未初始化，无法使用 find 操作！", this.name);
            return null;
        }
        if (Objects.isNull(key)) {
            log.warn("[ R2MO ] 缓存 {} 不允许查找 null 键！", this.name);
            return null;
        }
        return this.doFind(key);
    }

    protected abstract V doFind(final K key);

    @Override
    public void clear() {
        if (!this.initialized) {
            log.warn("[ R2MO ] 缓存 {} 未初始化，无法使用 clear 操作！", this.name);
            return;
        }
        this.doClear();
    }

    protected abstract void doClear();
}

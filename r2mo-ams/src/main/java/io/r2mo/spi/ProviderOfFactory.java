package io.r2mo.spi;

import cn.hutool.core.util.StrUtil;
import io.r2mo.typed.annotation.SPID;
import io.r2mo.typed.cc.Cc;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author lang : 2025-08-28
 */
@Slf4j
class ProviderOfFactory {

    private static final Cc<String, FactoryObject> CCT_OBJECT_FACTORY = Cc.openThread();

    private static final Cc<String, FactoryDBAction> CCT_DB_ACTION_FACTORY = Cc.openThread();

    private static final Cc<String, FactoryIo> CCT_IO_FACTORY = Cc.openThread();

    private static final Cc<String, FactoryWeb> CCT_WEB_FACTORY = Cc.openThread();

    private static final ConcurrentMap<Class<?>, Class<?>> META_CLASS = new ConcurrentHashMap<>();

    public static FactoryIo forIo() {
        return CCT_IO_FACTORY.pick(() -> findOneInternal(FactoryIo.class));
    }

    public static FactoryObject forObject() {
        return CCT_OBJECT_FACTORY.pick(() -> findOneInternal(FactoryObject.class));
    }

    public static FactoryDBAction forDBAction() {
        return CCT_DB_ACTION_FACTORY.pick(() -> findOneInternal(FactoryDBAction.class));
    }

    public static FactoryWeb forWeb() {
        return CCT_WEB_FACTORY.pick(() -> findOneInternal(FactoryWeb.class));
    }

    static ConcurrentMap<Class<?>, Class<?>> meta() {
        return META_CLASS;
    }

    static <T> T findOne(final Class<T> clazz, final String name) {
        final List<T> instances = findMany(clazz);
        if (instances.isEmpty()) {
            log.warn("[ R2MO ] SPI 实现类未找到: {}", clazz.getName());
            return null;
        }
        if (StrUtil.isBlank(name)) {
            // 如果没有指定名称，并且有多个实现类，则返回空
            if (1 < instances.size()) {
                log.warn("[ R2MO ] SPI 实现类不唯一: {}, {}", clazz.getName(), instances.size());
                return null;
            }
            return instances.get(0);
        } else {
            final T found = instances.stream().filter(item -> {
                final Class<?> implClass = item.getClass();
                final SPID annoSPI = implClass.getDeclaredAnnotation(SPID.class);
                if (Objects.isNull(annoSPI)) {
                    return false;
                }
                final String implName = annoSPI.value();
                return StrUtil.equals(name, implName);
            }).findAny().orElse(null);
            log.info("[ R2MO ] SPI 实现类按名称查找: interface = {} / SPID = {} / {}",
                clazz.getName(), name, Objects.isNull(found) ? null : found.getClass().getName());
            return found;
        }
    }

    static <T> List<T> findMany(final Class<T> clazz) {
        Objects.requireNonNull(clazz);
        final ServiceLoader<T> loader = ServiceLoader.load(clazz);
        final List<T> instances = new ArrayList<>();
        for (final T instance : loader) {
            instances.add(instance);
        }
        return instances;
    }

    static <T> List<T> findMany(final Class<T> clazz, final ClassLoader loader) {
        final List<T> list = new ArrayList<>();
        ServiceLoader<T> factories;
        if (loader != null) {
            /*
             * 1. 外部传入了 ClassLoader，这种情况直接使用外部的 ClassLoader 加载
             */
            factories = ServiceLoader.load(clazz, loader);
        } else {
            /*
             * 2. 外部没有传入 ClassLoader，则直接使用 TCCL 加载
             */
            final ClassLoader TCCL = Thread.currentThread().getContextClassLoader();
            factories = ServiceLoader.load(clazz, TCCL);
        }
        if (factories.iterator().hasNext()) {
            /* 「OK」上边二选一已拿到信息，则直接遍历提取 */
            factories.iterator().forEachRemaining(list::add);
            return list;
        }


        /*
         * 3. 默认使用 TCCL，但在 OSGi 环境中可能不够，因此尝试使用加载此类的类加载器，所以为了兼容 osgi 环境，需要使用
         *    - clazz 的类加载器
         *    - USPI.class 的类加载器
         */
        final ClassLoader clazzLoader = clazz.getClassLoader();
        factories = ServiceLoader.load(clazz, clazzLoader);
        if (factories.iterator().hasNext()) {
            /* 「OK」上边已拿到信息，则直接遍历提取 */
            factories.iterator().forEachRemaining(list::add);
            return list;
        }


        /*
         * 4. 直接使用 SPI.class 的类加载器（特殊情况）
         */
        final ClassLoader spiLoader = SPI.class.getClassLoader();
        factories = ServiceLoader.load(clazz, spiLoader);
        if (factories.iterator().hasNext()) {
            factories.iterator().forEachRemaining(list::add);
            return list;
        }

        return Collections.emptyList();
    }

    private static <T> T findOneInternal(final Class<T> clazz) {
        Objects.requireNonNull(clazz);
        final ServiceLoader<T> loader = ServiceLoader.load(clazz);
        for (final T instance : loader) {
            if (Objects.nonNull(instance)) {
                META_CLASS.put(clazz, instance.getClass());
            }
            return instance;
        }
        log.error("[ R2MO ] SPI 实现类未找到: {}", clazz.getName());
        return null;
    }
}

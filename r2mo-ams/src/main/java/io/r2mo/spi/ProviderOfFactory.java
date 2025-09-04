package io.r2mo.spi;

import cn.hutool.core.util.StrUtil;
import io.r2mo.typed.annotation.OneSPI;
import io.r2mo.typed.cc.Cc;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author lang : 2025-08-28
 */
@Slf4j
public class ProviderOfFactory {

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
        } else if (instances.size() > 1) {
            log.warn("[ R2MO ] SPI 实现类不唯一: {}, {}", clazz.getName(), instances.size());
            return null;
        }
        if (StrUtil.isBlank(name)) {
            return instances.get(0);
        } else {
            return instances.stream().filter(item -> {
                final Class<?> implClass = item.getClass();
                final OneSPI annoSPI = implClass.getDeclaredAnnotation(OneSPI.class);
                if (Objects.isNull(annoSPI)) {
                    return false;
                }
                final String implName = annoSPI.name();
                return StrUtil.equals(name, implName);
            }).findAny().orElse(null);
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

    private static <T> T findOneInternal(final Class<T> clazz) {
        Objects.requireNonNull(clazz);
        final ServiceLoader<T> loader = ServiceLoader.load(clazz);
        for (final T instance : loader) {
            if (Objects.nonNull(instance)) {
                META_CLASS.put(clazz, instance.getClass());
            }
            return instance;
        }
        return null;
    }
}

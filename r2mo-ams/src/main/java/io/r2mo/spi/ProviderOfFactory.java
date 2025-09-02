package io.r2mo.spi;

import io.r2mo.typed.annotation.Oneness;
import io.r2mo.typed.cc.Cc;
import lombok.extern.slf4j.Slf4j;

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
        return CCT_IO_FACTORY.pick(() -> findOne(FactoryIo.class));
    }

    public static FactoryObject forObject() {
        return CCT_OBJECT_FACTORY.pick(() -> findOne(FactoryObject.class));
    }

    public static FactoryDBAction forDBAction() {
        return CCT_DB_ACTION_FACTORY.pick(() -> findOne(FactoryDBAction.class));
    }

    public static FactoryWeb forWeb() {
        return CCT_WEB_FACTORY.pick(() -> findOne(FactoryWeb.class));
    }

    static ConcurrentMap<Class<?>, Class<?>> meta() {
        return META_CLASS;
    }

    private static <T> T findBy(final Class<T> clazz, final String name) {
        Objects.requireNonNull(clazz);
        Objects.requireNonNull(name);
        final ServiceLoader<T> loader = ServiceLoader.load(clazz);
        for (final T instance : loader) {
            final Class<?> implCls = instance.getClass();
            final Oneness oneness = implCls.getDeclaredAnnotation(Oneness.class);
            if (Objects.isNull(oneness)) {
                continue;
            }
            if (name.equals(oneness.value())) {
                return instance;
            }
        }
        return null;
    }

    private static <T> T findOne(final Class<T> clazz) {
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

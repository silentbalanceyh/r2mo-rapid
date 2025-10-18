package io.r2mo;

import cn.hutool.core.util.StrUtil;
import io.r2mo.function.Fn;
import io.r2mo.typed.cc.Cc;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Stream;

/**
 * 🔍 源码反射工具类（反射专用库）
 * <pre>
 * 提供以下能力：
 * 1. 单例获取与实例化工具（singleton / instance）
 * 2. 类接口实现关系检查（isImplement）
 * 3. 对象字段的安全读写（value 读 / value 写）
 * 4. 泛型类型参数获取（classT0 / classT1）
 * 5. 字段查找（包含父类层级 fieldN / field）
 * </pre>
 * 特点：
 * - 捕获异常并封装成日志 + 合理默认返回，保证工具调用时不会轻易抛出未处理异常
 * - 提供简化 API，减少反射操作的模板代码
 *
 * @author lang
 * @since 2025-09-06
 */
@Slf4j
public final class SourceReflect {
    private static final Cc<String, Class<?>> CLASS_CACHE = Cc.open();
    /**
     * 类到单例对象的缓存（轻量级容器）
     */
    private static final Cc<Class<?>, Object> SINGLETONS = Cc.open();

    private SourceReflect() {
    }

    // -----------------------------------------------------------------------
    // 🟢 单例 / 实例化相关
    // -----------------------------------------------------------------------

    /**
     * 获取某个类的单例对象（缓存模式）
     *
     * @param clazzImpl 类类型
     * @param <T>       泛型类型
     *
     * @return 该类的单例对象，如果不存在则通过 {@link #instance(Class, Object[])} 构造并缓存
     */
    @SuppressWarnings("unchecked")
    public static <T> T singleton(final Class<?> clazzImpl, final Object... params) {
        return (T) SINGLETONS.pick(() -> instance(clazzImpl, params), clazzImpl);
    }

    public static <T> T singleton(final String className, final Object... params) {
        final Class<?> clazz = clazz(className);
        if (Objects.isNull(clazz)) {
            throw new IllegalStateException("[ R2MO ] 类不存在：" + className);
        }
        return singleton(clazz, params);
    }

    /**
     * 创建类的新实例（支持带参构造函数、多构造重载）
     * <pre>
     * 特性：
     * ✅ 支持无参构造和有参构造
     * ✅ 自动匹配接口、抽象类和父类类型
     * ✅ 支持 null 参数（仅限引用类型，不能匹配原始类型）
     *
     * @param clazzImpl 类类型
     * @param args      构造函数参数
     * @param <T>       泛型类型
     * </pre>
     *
     * @return 类实例
     * @throws IllegalStateException 当没有匹配的构造函数时抛出运行时异常
     */
    @SuppressWarnings("unchecked")
    public static <T> T instance(final Class<?> clazzImpl, final Object... args) {
        try {
            if (args == null || args.length == 0) {
                // 无参构造
                return (T) clazzImpl.getDeclaredConstructor().newInstance();
            }

            // 获取所有构造函数（含 private）
            final Constructor<?>[] ctors = clazzImpl.getDeclaredConstructors();
            for (final Constructor<?> ctor : ctors) {
                final Class<?>[] ctorTypes = ctor.getParameterTypes();
                if (ctorTypes.length != args.length) {
                    continue; // 参数数量不匹配
                }

                boolean match = true;
                for (int i = 0; i < ctorTypes.length; i++) {
                    final Object arg = args[i];
                    final Class<?> paramType = ctorTypes[i];

                    if (arg == null) {
                        // null 可匹配任意非原始类型
                        if (paramType.isPrimitive()) {
                            match = false;
                            break;
                        }
                        continue;
                    }

                    final Class<?> argType = arg.getClass();
                    // 关键逻辑：允许接口、抽象类、父类匹配
                    if (!paramType.isAssignableFrom(argType)) {
                        match = false;
                        break;
                    }
                }

                if (match) {
                    ctor.setAccessible(true);
                    @SuppressWarnings("unchecked") final T instance = (T) ctor.newInstance(args);
                    return instance;
                }
            }

            throw new IllegalStateException(
                "[ R2MO ] 没有找到匹配的构造函数：" + clazzImpl.getName()
            );
        } catch (final Exception ex) {
            log.error(ex.getMessage(), ex);
            throw new IllegalStateException("[ R2MO ] 实例化对象失败！", ex);
        }
    }

    public static <T> T instance(final String className, final Object... params) {
        final Class<?> clazz = clazz(className);
        if (Objects.isNull(clazz)) {
            throw new IllegalStateException("[ R2MO ] 类不存在：" + className);
        }
        return instance(clazz, params);
    }
    // -----------------------------------------------------------------------
    // 🟢 接口实现检查
    // -----------------------------------------------------------------------

    /**
     * 判断某个类是否实现了指定接口（递归检查父类）
     *
     * @param implCls      实现类
     * @param interfaceCls 接口类
     *
     * @return true 表示实现了该接口
     */
    public static boolean isImplement(final Class<?> implCls, final Class<?> interfaceCls) {
        final Class<?>[] interfaces = implCls.getInterfaces();
        boolean match = Arrays.asList(interfaces).contains(interfaceCls);
        if (!match && Objects.nonNull(implCls.getSuperclass())) {
            // 递归检查父类
            match = isImplement(implCls.getSuperclass(), interfaceCls);
        }
        return match;
    }

    // -----------------------------------------------------------------------
    // 🟢 字段读取
    // -----------------------------------------------------------------------

    /**
     * 获取对象指定字段的值（自动推断类类型）
     *
     * @param entity 实体对象
     * @param field  字段名称
     * @param <V>    返回值类型
     * @param <T>    实体类型
     *
     * @return 字段值；如果对象/字段不存在或访问失败则返回 null
     */
    @SuppressWarnings("unchecked")
    public static <V, T> V value(final T entity, final String field) {
        if (entity == null) {
            return null;
        }
        return value(entity, field, (Class<T>) entity.getClass());
    }

    /**
     * 获取对象指定字段的值（强制使用传入类类型）
     *
     * @param entity    实体对象
     * @param field     字段名称
     * @param entityCls 实体类类型
     * @param <V>       返回值类型
     * @param <T>       实体类型
     *
     * @return 字段值；如果对象/字段不存在或访问失败则返回 null
     */
    @SuppressWarnings("unchecked")
    public static <V, T> V value(final T entity, final String field, final Class<T> entityCls) {
        if (entity == null || field == null || field.isEmpty() || entityCls == null) {
            return null;
        }
        try {
            final Field f = fieldN(entityCls, field);
            if (f == null) {
                return null;
            }
            f.setAccessible(true);
            final Object val = f.get(entity);
            return (V) val;
        } catch (final Exception ex) {
            log.error(ex.getMessage(), ex);
            return null;
        }
    }

    // -----------------------------------------------------------------------
    // 🟢 字段写入
    // -----------------------------------------------------------------------

    /**
     * 设置对象指定字段的值（通过字段名）
     *
     * @param instance 对象实例
     * @param name     字段名
     * @param value    要设置的值
     * @param <T>      对象类型
     * @param <V>      字段值类型
     */
    public static <T, V> void value(final T instance, final String name, final V value) {
        if (Objects.nonNull(instance) && Objects.nonNull(name)) {
            try {
                final Field field = instance.getClass().getDeclaredField(name);
                value(instance, field, value);
            } catch (final NoSuchFieldException ex) {
                log.warn("[ R2MO ] 类 {} 设置异常，详情: {}", instance.getClass(), ex.getMessage());
            }
        }
    }

    /**
     * 设置对象指定字段的值（通过 Field 对象）
     *
     * @param instance 对象实例
     * @param field    反射字段
     * @param value    要设置的值
     * @param <T>      对象类型
     * @param <V>      字段值类型
     */
    public static <T, V> void value(final T instance, final Field field, final V value) {
        field.setAccessible(true);
        Fn.jvmAt(() -> field.set(instance, value));
    }

    /**
     * 获取类中的 static 常量或变量（接口常量、静态公有/私有常量）
     *
     * @param clazz 类
     * @param name  字段名
     * @param <T>   返回值类型
     *
     * @return 字段值
     */
    @SuppressWarnings("all")
    public static <T> T value(final Class<?> clazz, final String name) {
        return Fn.jvmOr(() -> {
            final Field field = clazz.getDeclaredField(name);
            field.setAccessible(true);
            return (T) field.get(null);
        });
    }

    // -----------------------------------------------------------------------
    // 🟢 字段查找
    // -----------------------------------------------------------------------

    /**
     * 在类层级结构中查找字段（包含父类）
     *
     * @param clazz     目标类
     * @param fieldName 字段名称
     *
     * @return Field 对象；如果不存在返回 null
     */
    public static Field fieldN(Class<?> clazz, final String fieldName) {
        while (clazz != null && clazz != Object.class) {
            final Field field = field(clazz, fieldName);
            if (field != null) {
                return field;
            }
            clazz = clazz.getSuperclass();
        }
        return null;
    }

    /**
     * 在类中查找指定字段（仅当前类，不含父类）
     *
     * @param clazz     目标类
     * @param fieldName 字段名称
     *
     * @return Field 对象；如果不存在返回 null
     */
    public static Field field(final Class<?> clazz, final String fieldName) {
        Objects.requireNonNull(clazz);
        if (Objects.isNull(fieldName)) {
            return null;
        }
        return Stream.of(fields(clazz))
            .filter(f -> fieldName.equals(f.getName()))
            .findAny().orElse(null);
    }

    /**
     * 🔍 获取类及其所有父类中的非静态、非抽象字段
     * <p>
     * 🔄 此方法递归遍历类继承树，收集当前类及所有父类的非静态、非抽象字段。
     *
     * @param clazz 📚 目标类
     *
     * @return 📝 包含当前类及所有父类的非静态、非抽象字段数组
     * @throws NullPointerException 🚨 当 clazz 为 null 时抛出
     * @since 💡 1.1.0
     */
    public static Field[] fieldsN(final Class<?> clazz) {
        Objects.requireNonNull(clazz);

        final List<Field> allFields = new ArrayList<>();
        Class<?> currentClass = clazz;

        // 🔄 循环遍历类继承链，直到 Object 类
        while (currentClass != null && !currentClass.equals(Object.class)) {
            // 📥 使用现有的 fields 方法获取当前类的字段
            final Field[] currentFields = fields(currentClass);
            allFields.addAll(Arrays.asList(currentFields));
            currentClass = currentClass.getSuperclass();
        }

        return allFields.toArray(new Field[0]);
    }

    /**
     * 获取类中所有非静态、非抽象字段
     *
     * @param clazz 目标类
     *
     * @return 字段数组
     */
    public static Field[] fields(final Class<?> clazz) {
        Objects.requireNonNull(clazz);
        final Field[] fields = clazz.getDeclaredFields();
        return Arrays.stream(fields)
            .filter(item -> !Modifier.isStatic(item.getModifiers()))
            .filter(item -> !Modifier.isAbstract(item.getModifiers()))
            .toArray(Field[]::new);
    }

    // -----------------------------------------------------------------------
    // 🟢 泛型类型提取
    // -----------------------------------------------------------------------

    /**
     * 获取类的泛型定义中的第一个类型（通常是 UT）
     *
     * @param target 目标类
     * @param <T>    类型
     *
     * @return 泛型 Class 对象；获取失败返回 null
     */
    public static <T> Class<T> classT0(final Class<?> target) {
        return classT(target, 0);
    }

    /**
     * 获取类的泛型定义中的第二个类型（通常是 E）
     *
     * @param target 目标类
     * @param <T>    类型
     *
     * @return 泛型 Class 对象；获取失败返回 null
     */
    public static <T> Class<T> classT1(final Class<?> target) {
        return classT(target, 1);
    }

    /**
     * 按索引获取类泛型参数的类型
     *
     * @param target 目标类
     * @param index  泛型参数索引
     * @param <T>    类型
     *
     * @return 泛型 Class 对象；获取失败返回 null
     */
    @SuppressWarnings("unchecked")
    private static <T> Class<T> classT(final Class<?> target, final int index) {
        Objects.requireNonNull(target);
        final Type genericType = target.getGenericSuperclass();
        if (genericType instanceof final ParameterizedType pt) {
            final Type[] types = pt.getActualTypeArguments();
            if (index < types.length) {
                return (Class<T>) types[index];
            } else {
                throw new IllegalStateException("[ R2MO ] 泛型定义长度不对！");
            }
        } else {
            // 旧代码保留：部分子类未声明泛型时返回 null
            return null;
        }
    }

    public static Class<?> clazz(final String name) {
        return clazz(name, null, null);
    }

    public static Class<?> clazz(final String name, final Class<?> instanceCls) {
        return clazz(name, instanceCls, null);
    }

    public static Class<?> clazz(final String className, final Class<?> defaultClass, final ClassLoader loader) {
        if (StrUtil.isBlank(className)) {
            return defaultClass;
        }
        /*
         * 这里我们不能直接调用 `clazz(name)`，因为 getJvm 在当前方法中会抛出异常。我们应该捕获 `ClassNotFoundException` 并直接返回 null。
         */
        final ConcurrentMap<String, Class<?>> cCache = CLASS_CACHE.get();
        Class<?> clazz = cCache.get(className);
        if (Objects.isNull(clazz)) {
            /* 优先考虑使用非空类加器加载 */
            try {
                if (Objects.nonNull(loader)) {
                    clazz = loader.loadClass(className);
                }
            } catch (final Throwable ex) {
                log.error("[ ZERO ] (Module) 类加载异常，详情: {}", ex.getMessage());
            }
        }


        // 如果 clazz 为空，则考虑从当前线程中再加载
        if (Objects.isNull(clazz)) {
            try {
                clazz = Thread.currentThread().getContextClassLoader().loadClass(className);
            } catch (final Throwable ex) {
                log.error("[ ZERO ] (Program) 类加载异常，详情: {}", ex.getMessage());
            }
        }


        // 从当前类加载器中加载
        if (Objects.isNull(clazz)) {
            try {
                clazz = ClassLoader.getSystemClassLoader().loadClass(className);
            } catch (final Throwable ex) {
                log.error("[ ZERO ] (System) 类加载异常，详情: {}", ex.getMessage());
            }
        }


        // 从平台类加载器中加载
        if (Objects.isNull(clazz)) {
            try {
                clazz = ClassLoader.getPlatformClassLoader().loadClass(className);
            } catch (final Throwable ex) {
                log.error("[ ZERO ] (Platform) 类加载异常，详情: {}", ex.getMessage());
            }
        }


        // 全程扫描完成，依旧没有
        if (Objects.isNull(clazz)) {
            clazz = defaultClass;
        } else {
            cCache.put(className, clazz);
        }
        return clazz;
    }
}

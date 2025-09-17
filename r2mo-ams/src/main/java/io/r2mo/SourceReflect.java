package io.r2mo;

import io.r2mo.typed.cc.Cc;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * 源码处理（反射专用库）
 *
 * @author lang : 2025-09-06
 */
@Slf4j
public final class SourceReflect {

    private static final Cc<Class<?>, Object> SINGLETONS = Cc.open();

    private SourceReflect() {
    }

    /**
     * 获取单例对象
     *
     * @param clazzImpl 类类型
     * @param <T>       泛型类型
     *
     * @return 单例对象
     */
    @SuppressWarnings("unchecked")
    public static <T> T singleton(final Class<T> clazzImpl) {
        return (T) SINGLETONS.pick(() -> instance(clazzImpl), clazzImpl);
    }

    /**
     * 获取单例对象
     *
     * @param clazzImpl 类类型
     * @param <T>       泛型类型
     *
     * @return 单例对象
     */
    public static <T> T instance(final Class<T> clazzImpl) {
        try {
            return clazzImpl.getDeclaredConstructor().newInstance();
        } catch (final Exception ex) {
            log.error(ex.getMessage(), ex);
            throw new IllegalStateException("[ R2MO ] 实例化对象失败！", ex);
        }
    }

    /**
     * 获取实体对象中指定字段的值
     *
     * @param entity    实体对象
     * @param field     字段名称
     * @param entityCls 实体类类型
     * @param <V>       返回值类型
     * @param <T>       实体类型
     *
     * @return 字段值，如果字段不存在或无法访问则返回 null
     */
    public static <V, T> V value(final T entity, final String field, final Class<T> entityCls) {
        if (entity == null || field == null || field.isEmpty() || entityCls == null) {
            return null;
        }
        try {
            // 查找字段（包括父类）
            final Field f = fieldN(entityCls, field);
            if (f == null) {
                return null;
            }

            // 设置字段可访问
            f.setAccessible(true);

            // 获取字段值并转换为 K 类型
            final Object value = f.get(entity);

            // 转换成所需类型
            @SuppressWarnings("unchecked") final V key = (V) value;
            return key;
        } catch (final Exception ex) {
            // 出现异常返回 null
            log.error(ex.getMessage(), ex);
            return null;
        }
    }

    /**
     * 根据字段名称获取字段定义，包含父类
     *
     * @param clazz     目标类
     * @param fieldName 字段名称
     *
     * @return Field 字段定义
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
     * 根据字段名称获取字段定义
     *
     * @param clazz     目标类
     * @param fieldName 字段名称
     *
     * @return Field 字段定义
     */
    public static Field field(final Class<?> clazz, final String fieldName) {
        Objects.requireNonNull(clazz);
        if (Objects.isNull(fieldName)) {
            return null;
        }
        final Field[] fields = clazz.getDeclaredFields();
        return Stream.of(fields)
            .filter(field -> fieldName.equals(field.getName()))
            .findAny().orElse(null);
    }

    /**
     * 获取泛型定义中的第二个类型，通常是 E
     *
     * @param target 目标类
     * @param <T>    类型
     *
     * @return Class<UT> 返回泛型的类型信息
     */
    public static <T> Class<T> classT1(final Class<?> target) {
        return classT(target, 1);
    }

    /**
     * 获取泛型定义中的第一个类型，通常是 UT
     *
     * @param target 目标类
     * @param <T>    类型
     *
     * @return Class<UT> 返回泛型的类型信息
     */
    public static <T> Class<T> classT0(final Class<?> target) {
        return classT(target, 0);
    }

    @SuppressWarnings("unchecked")
    private static <T> Class<T> classT(final Class<?> target, final int index) {
        Objects.requireNonNull(target);
        final Type genericType = target.getGenericSuperclass();
        if (genericType instanceof final ParameterizedType parameterizedType) {
            final Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
            if (0 < actualTypeArguments.length) {
                return (Class<T>) actualTypeArguments[index];
            } else {
                throw new IllegalStateException("[ R2MO ] 泛型定义长度不对！");
            }
        } else {
            // 旧代码, 孙子辈不可能再出现泛型，扩展专用
            // throw new IllegalStateException("[ R2MO ] 泛型类型获取失败！");
            return null;
        }
    }
}

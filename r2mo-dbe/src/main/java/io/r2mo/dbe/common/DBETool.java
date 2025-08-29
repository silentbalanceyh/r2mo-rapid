package io.r2mo.dbe.common;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author lang : 2025-08-28
 */
public class DBETool {

    static <K, T> Map<K, List<T>> groupBy(final List<T> entities, final String field, final Class<T> entityCls) {
        final Function<T, K> keyMapper = entity -> getValue(entity, field, entityCls);
        return entities.parallelStream().collect(Collectors.groupingBy(keyMapper));
    }

    public static <K, T> K getValue(final T entity, final String field, final Class<T> entityCls) {
        if (entity == null || field == null || field.isEmpty() || entityCls == null) {
            return null;
        }
        try {
            // 查找字段（包括父类）
            final Field f = getField(entityCls, field);
            if (f == null) {
                return null;
            }

            // 设置字段可访问
            f.setAccessible(true);

            // 获取字段值并转换为 K 类型
            final Object value = f.get(entity);

            // 转换成所需类型
            @SuppressWarnings("unchecked") final K key = (K) value;
            return key;
        } catch (final Exception ex) {
            // 出现异常返回 null
            return null;
        }
    }

    public static Field getField(Class<?> entityCls, final String fieldName) {
        // 从当前类开始向上查找
        while (entityCls != null && entityCls != Object.class) {
            try {
                // 尝试当前类中查找
                return entityCls.getDeclaredField(fieldName);
            } catch (final NoSuchFieldException e) {
                // 如果当前类中没有找到，则继续向上查找父类
                entityCls = entityCls.getSuperclass();
            }
        }
        return null;
    }
}

package io.r2mo.dbe.common;

import io.r2mo.SourceReflect;
import io.r2mo.typed.annotation.Identifiers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author lang : 2025-08-28
 */
public class DBETool {

    static <K, T> Map<K, List<T>> groupBy(final List<T> entities, final String field, final Class<T> entityCls) {
        final Function<T, K> keyMapper = entity -> SourceReflect.value(entity, field, entityCls);
        return entities.parallelStream().collect(Collectors.groupingBy(keyMapper));
    }

    @SuppressWarnings("unchecked")
    public static <T> Map<String, Object> getIdentifier(final Object entity) {
        if (Objects.isNull(entity)) {
            return null;
        }
        final Class<T> entityCls = (Class<T>) entity.getClass();
        final Identifiers identifiers = entityCls.getDeclaredAnnotation(Identifiers.class);
        if (Objects.isNull(identifiers)) {
            return null;
        }
        final Map<String, Object> condition = new HashMap<>();
        final String[] fields = identifiers.value();
        final T instance = (T) entity;
        // WHERE field1 = ? AND field2 = ? ...
        for (final String field : fields) {
            condition.put(field, SourceReflect.value(instance, field, entityCls));
        }
        // WHERE appId = ? AND tenantId = ? AND enabled = true
        if (identifiers.ifApp()) {
            condition.put("appId", SourceReflect.value(instance, "appId", entityCls));
        }
        if (identifiers.ifTenant()) {
            condition.put("tenantId", SourceReflect.value(instance, "tenantId", entityCls));
        }
        if (identifiers.ifEnabled()) {
            condition.put("enabled", true);
        }
        return condition;
    }
}

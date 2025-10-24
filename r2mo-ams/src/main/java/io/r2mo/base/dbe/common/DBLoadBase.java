package io.r2mo.base.dbe.common;

import io.r2mo.SourceReflect;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Objects;

/**
 * @author lang : 2025-10-24
 */
public abstract class DBLoadBase implements DBLoad {

    protected void setupFields(final DBNode node, final Class<?> clazz) {
        Objects.requireNonNull(clazz, "[ DBLoad ] 实体类 Class<?> 不可为 null！");
        final Field[] fields = SourceReflect.fieldsN(clazz);
        Arrays.stream(fields)
            .filter(field -> !Modifier.isStatic(field.getModifiers()))
            .forEach(field -> node.types(field.getName(), field.getType()));
    }
}

package io.r2mo.dbe.mybatisplus.core.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;

/**
 * 调试工具：输出对象内部字段信息（带 hashCode）
 *
 * @author lang
 */
class BaseDebug {
    private static final List<String> SPECIAL_FIELDS = Arrays.asList("id", "code", "appId", "tenantId");

    /**
     * 返回对象调试信息（带 hashCode）
     */
    public static String dgInfo(final Object obj) {
        if (obj == null) {
            return "null";
        }

        final StringBuilder sb = new StringBuilder();
        sb.append("Object@").append(obj.hashCode()).append("\n");

        final Class<?> clazz = obj.getClass();
        appendClassInfo(sb, clazz, obj);

        // 打印特殊信息
        sb.append("- 特殊信息\n");
        appendSpecialFields(sb, obj);

        return sb.toString();
    }

    private static void appendClassInfo(final StringBuilder sb, final Class<?> clazz, final Object obj) {
        // 递归父类
        if (clazz.getSuperclass() != null && !clazz.getSuperclass().equals(Object.class)) {
            appendClassInfo(sb, clazz.getSuperclass(), obj);
        }

        final Field[] fields = clazz.getDeclaredFields();
        boolean hasPrintableFields = false;

        // 判断是否有可打印的字段
        for (final Field field : fields) {
            if (shouldPrintField(field)) {
                try {
                    field.setAccessible(true);
                    if (field.get(obj) != null) {
                        hasPrintableFields = true;
                        break;
                    }
                } catch (final IllegalAccessException ignored) {
                }
            }
        }

        if (hasPrintableFields) {
            sb.append("- ").append(clazz.getSimpleName()).append(" / ").append(clazz.getName()).append("\n");
            for (final Field field : fields) {
                if (shouldPrintField(field)) {
                    try {
                        field.setAccessible(true);
                        final Object value = field.get(obj);
                        sb.append("   ").append(field.getName()).append(" = ").append(value).append("\n");
                    } catch (final IllegalAccessException e) {
                        sb.append("   ").append(field.getName()).append(" = <access denied>\n");
                    }
                }
            }
        }
    }

    private static boolean shouldPrintField(final Field field) {
        return !Modifier.isStatic(field.getModifiers()) &&
            !field.isAnnotationPresent(JsonIgnore.class);
    }

    private static void appendSpecialFields(final StringBuilder sb, final Object obj) {
        boolean first = true;
        sb.append("   ");
        for (final String fieldName : SPECIAL_FIELDS) {
            final Object value = getFieldValue(obj, fieldName);
            if (value != null) {
                if (!first) {
                    sb.append(", ");
                }
                sb.append(fieldName).append("=").append(value);
                first = false;
            }
        }
        sb.append("\n");
    }

    private static Object getFieldValue(final Object obj, final String fieldName) {
        Class<?> clazz = obj.getClass();
        while (clazz != null && !clazz.equals(Object.class)) {
            try {
                final Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                return field.get(obj);
            } catch (final NoSuchFieldException | IllegalAccessException e) {
                clazz = clazz.getSuperclass();
            }
        }
        return null;
    }
}

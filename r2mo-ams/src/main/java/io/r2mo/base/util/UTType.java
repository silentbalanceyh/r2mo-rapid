package io.r2mo.base.util;

import cn.hutool.core.util.StrUtil;
import io.r2mo.typed.json.JArray;

import java.lang.reflect.Array;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author lang : 2025-10-19
 */
class UTType {
    /**
     * 🔄 将任意 Java 对象转换为 Collection
     * <p>
     * 🔍 此方法能够识别并转换以下类型的对象：
     * <ul>
     *   <li>数组（包括基本类型数组）</li>
     *   <li>Collection 接口实现类（List, Set 等）</li>
     *   <li>Map 类型（返回 Map 的 values 集合）</li>
     *   <li>Iterator 接口实现</li>
     *   <li>Iterable 接口实现</li>
     *   <li>单个对象（包装为包含该对象的集合）</li>
     * </ul>
     *
     * @param obj 📦 要转换的 Java 对象
     *
     * @return 📝 转换后的 Collection 实例，如果输入为 null 则返回空集合
     * @since 💡 1.0.0
     */
    @SuppressWarnings("unchecked")
    static Collection<?> toCollection(final Object obj) {
        if (obj == null) {
            return Collections.emptyList();
        }

        // 🔄 检查是否已经是 Collection
        if (obj instanceof Collection) {
            return (Collection<?>) obj;
        }

        // 🔄 检查是否是数组
        if (obj.getClass().isArray()) {
            if (obj instanceof Object[]) {
                // 📝 对象数组
                return Arrays.stream(flattenValues(obj)).toList();
            } else {
                // 🔢 基本类型数组需要特殊处理
                return Arrays.stream((Object[]) Array.newInstance(
                        obj.getClass().getComponentType(),
                        Array.getLength(obj)))
                    .map(i -> Array.get(obj, (Integer) i))
                    .collect(Collectors.toList());
            }
        }

        // 🔄 检查是否是 Map
        if (obj instanceof Map) {
            return ((Map<Object, Object>) obj).values();
        }

        // 🔄 检查是否是 Iterator
        if (obj instanceof Iterator) {
            final List<Object> list = new ArrayList<>();
            final Iterator<Object> iterator = (Iterator<Object>) obj;
            while (iterator.hasNext()) {
                list.add(iterator.next());
            }
            return list;
        }

        // 🔄 检查是否是 Iterable
        if (obj instanceof Iterable) {
            final List<Object> list = new ArrayList<>();
            for (final Object item : (Iterable<Object>) obj) {
                list.add(item);
            }
            return list;
        }

        if (obj instanceof final JArray array) {
            return array.toList();
        }

        // 🔄 单个对象，包装为集合
        return Collections.singletonList(obj);
    }

    static boolean isMatch(final String value, final String regex) {
        if (StrUtil.isEmpty(value) || StrUtil.isEmpty(regex)) {
            return false;
        }
        final Pattern pattern = Pattern.compile(regex);
        final Matcher matcher = pattern.matcher(value);
        return matcher.matches();
    }

    /**
     * 🔍 检查对象是否为集合类型
     * <p>
     * 🔄 此方法能够识别以下类型的集合对象：
     * <ul>
     *   <li>Collection 接口实现类（List, Set 等）</li>
     *   <li>数组（包括基本类型数组和对象数组）</li>
     *   <li>Map 类型</li>
     *   <li>Iterator 接口实现</li>
     *   <li>Iterable 接口实现</li>
     *   <li>JArray 类型</li>
     * </ul>
     *
     * @param obj 📦 要检查的 Java 对象
     *
     * @return ✅ 如果对象是集合类型返回 true，否则返回 false
     * @since 💡 1.0.0
     */
    static boolean isCollection(final Object obj) {
        if (obj == null) {
            return false;
        }

        final Class<?> clazz = obj.getClass();

        // 🔄 检查是否是 Collection 接口实现
        if (obj instanceof Collection) {
            return true;
        }

        // 🔄 检查是否是数组
        if (clazz.isArray()) {
            return true;
        }

        // 🔄 检查是否是 Map
        if (obj instanceof Map) {
            return true;
        }

        // 🔄 检查是否是 Iterator
        if (obj instanceof Iterator) {
            return true;
        }

        // 🔄 检查是否是 Iterable（但排除 String，因为 String 也实现了 Iterable<Character>）
        if (obj instanceof Iterable) {
            return true;
        }

        // 🔄 检查是否是 JArray
        return obj instanceof JArray;
    }

    static boolean isBoolean(final String literal, final boolean widely) {
        if (Objects.isNull(literal)) {
            return false;
        } else {
            final String lower = literal.toLowerCase().trim();
            if (widely) {
                /*
                 * 匹配对
                 * yes / no
                 * true / false
                 * y / n
                 * 1 / 0
                 */
                return "true".equals(lower)
                    || "false".equals(lower)
                    || "yes".equals(lower)
                    || "no".equals(lower)
                    || "y".equals(lower)
                    || "n".equals(lower)
                    || "1".equals(lower)
                    || "0".equals(lower);
            } else {
                return "true".equals(lower)
                    || "false".equals(lower);
            }
        }
    }

    static Object[] flattenValues(final Object... values) {
        if (values == null || values.length == 0) {
            return new Object[0];
        }

        final List<Object> resultList = new ArrayList<>();
        final Deque<Object> stack = new ArrayDeque<>();

        // 将所有输入压入栈
        for (int i = values.length - 1; i >= 0; i--) {
            stack.push(values[i]);
        }

        // 迭代展开
        while (!stack.isEmpty()) {
            final Object current = stack.pop();

            final Class<?> clazz = current.getClass();

            if (clazz.isArray()) {
                final int len = Array.getLength(current);
                // 倒序压入栈，保持顺序
                for (int i = len - 1; i >= 0; i--) {
                    stack.push(Array.get(current, i));
                }
            } else if (current instanceof final List<?> list) {
                // 倒序压入栈，保持顺序
                for (int i = list.size() - 1; i >= 0; i--) {
                    stack.push(list.get(i));
                }
            } else {
                resultList.add(current);
            }
        }

        return resultList.toArray();
    }
}

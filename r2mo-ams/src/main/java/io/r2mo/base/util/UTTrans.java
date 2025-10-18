package io.r2mo.base.util;

import io.r2mo.typed.json.JArray;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author lang : 2025-10-19
 */
class UTTrans {
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
                return Arrays.asList((Object[]) obj);
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
}

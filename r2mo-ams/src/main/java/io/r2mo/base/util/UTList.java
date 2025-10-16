package io.r2mo.base.util;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import io.r2mo.SourceReflect;
import io.r2mo.typed.common.Compared;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

/**
 * 列表差异工具类
 *
 * @author lang
 * @since 2025-09-24
 */
class UTList {

    static <T> List<T> elementCombine(final List<T> oldList,
                                      final List<T> newList,
                                      final String field) {
        // 统一返回“副本列表”，无副作用
        if (oldList == null || oldList.isEmpty()) {
            return Collections.emptyList();
        }

        // new -> Map(key, entity)
        final Map<Object, T> newMap = (newList == null || newList.isEmpty())
            ? Collections.emptyMap()
            : elementMap(newList, field);

        // 拷贝策略：忽略 null（不覆盖为 null），忽略拷贝错误
        final CopyOptions opts = new CopyOptions()
            .ignoreNullValue()
            .ignoreError();

        final List<T> result = new ArrayList<>(oldList.size());
        for (final T oldItem : oldList) {
            if (oldItem == null) {
                result.add(null);
                continue;
            }
            // 先基于旧元素创建一个“副本”
            @SuppressWarnings("unchecked") final T merged = BeanUtil.copyProperties(oldItem, (Class<T>) oldItem.getClass());

            // 如果新列表中存在同 key，则用新元素的“非空字段”覆盖到副本
            final Object key = SourceReflect.value(oldItem, field);
            if (key != null && !newMap.isEmpty()) {
                final T newItem = newMap.get(key);
                if (newItem != null) {
                    BeanUtil.copyProperties(newItem, merged, opts);
                }
            }
            result.add(merged);
        }
        return result;
    }

    /**
     * 比较两个列表，找出新增、更新和删除的元素。
     *
     * @param oldList 旧列表
     * @param newList 新列表
     * @param field   用于比较的字段名
     * @param <T>     实体类型
     *
     * @return 包含新增、更新和删除元素的 Compared
     */
    static <T> Compared<T> elementDiff(final List<T> oldList,
                                       final List<T> newList,
                                       final String field) {

        final Compared<T> compared = new Compared<>();

        // 如果旧列表为空，则所有新记录都是新增
        if (oldList == null || oldList.isEmpty()) {
            if (newList != null) {
                compared.queueC().addAll(newList);
            }
            return compared;
        }

        // 如果新列表为空，则所有旧记录都是删除
        if (newList == null || newList.isEmpty()) {
            compared.queueD().addAll(oldList);
            return compared;
        }

        // 构建映射
        final Map<Object, T> oldMap = elementMap(oldList, field);
        final Map<Object, T> newMap = elementMap(newList, field);

        // 新列表 -> 新增 & 更新
        for (final Map.Entry<Object, T> entry : newMap.entrySet()) {
            final Object key = entry.getKey();
            final T newEntity = entry.getValue();
            if (!oldMap.containsKey(key)) {
                compared.queueC().add(newEntity);
            } else {
                compared.queueU().add(newEntity);
                oldMap.remove(key); // 被匹配过的，从 oldMap 移除
            }
        }

        // oldMap 剩余的 -> 删除
        compared.queueD().addAll(oldMap.values());

        return compared;
    }

    /** 根据指定字段构建 Map */
    private static <K, V> Map<K, V> elementMap(final List<V> list, final String field) {
        final Map<K, V> map = new HashMap<>();
        for (final V item : list) {
            final K value = SourceReflect.value(item, field);
            if (value != null) {
                map.put(value, item);
            }
        }
        return map;
    }

    /**
     * 交集（基于字段值）
     */
    static <T> List<T> elementIntersection(final List<T> list1, final List<T> list2, final String field) {
        if (list1 == null || list2 == null) {
            return Collections.emptyList();
        }
        final Map<Object, T> map1 = elementMap(list1, field);
        final Map<Object, T> map2 = elementMap(list2, field);

        final List<T> result = new ArrayList<>();
        for (final Object key : map1.keySet()) {
            if (map2.containsKey(key)) {
                result.add(map1.get(key));
            }
        }
        return result;
    }

    /**
     * 并集（基于字段值，去重）
     */
    static <T> List<T> elementUnion(final List<T> list1, final List<T> list2, final String field) {
        final Map<Object, T> map = new LinkedHashMap<>();
        if (list1 != null) {
            map.putAll(elementMap(list1, field));
        }
        if (list2 != null) {
            map.putAll(elementMap(list2, field));
        }
        return new ArrayList<>(map.values());
    }

    /**
     * 差集：source - target（基于字段值）
     */
    static <T> List<T> elementSubtract(final List<T> source, final List<T> target, final String field) {
        if (source == null) {
            return Collections.emptyList();
        }
        final Map<Object, T> sourceMap = elementMap(source, field);
        if (target != null) {
            final Map<Object, T> targetMap = elementMap(target, field);
            targetMap.keySet().forEach(sourceMap::remove);
        }
        return new ArrayList<>(sourceMap.values());
    }

    /**
     * 分组：基于字段值
     */
    static <T> Map<Object, List<T>> elementGroupBy(final List<T> list, final String field) {
        final Map<Object, List<T>> groupMap = new LinkedHashMap<>();
        if (list == null) {
            return groupMap;
        }
        for (final T item : list) {
            final Object key = SourceReflect.value(item, field);
            groupMap.computeIfAbsent(key, k -> new ArrayList<>()).add(item);
        }
        return groupMap;
    }

    /**
     * 查找第一个匹配项：基于字段值
     */
    static <T> T elementFirst(final List<T> list, final String field, final Object value) {
        if (list == null) {
            return null;
        }
        for (final T item : list) {
            if (Objects.equals(SourceReflect.value(item, field), value)) {
                return item;
            }
        }
        return null;
    }

    /**
     * 查找所有匹配项：基于字段值
     */
    static <T> List<T> elementMany(final List<T> list, final String field, final Object value) {
        final List<T> result = new ArrayList<>();
        if (list == null) {
            return result;
        }
        for (final T item : list) {
            if (Objects.equals(SourceReflect.value(item, field), value)) {
                result.add(item);
            }
        }
        return result;
    }

    /**
     * 排序：基于字段值
     */
    static <T> List<T> elementSortBy(final List<T> list, final String field, final boolean asc) {
        if (list == null) {
            return Collections.emptyList();
        }
        list.sort((a, b) -> {
            final Comparable<Object> va = SourceReflect.value(a, field);
            final Comparable<Object> vb = SourceReflect.value(b, field);
            if (va == null && vb == null) {
                return 0;
            }
            if (va == null) {
                return asc ? -1 : 1;
            }
            if (vb == null) {
                return asc ? 1 : -1;
            }
            return asc ? va.compareTo(vb) : vb.compareTo(va);
        });
        return list;
    }

    static <K, V, E> ConcurrentMap<K, V> elementMap(final List<E> list, final Function<E, K> keyFn, final Function<E, V> valueFn) {
        final ConcurrentMap<K, V> grouped = new ConcurrentHashMap<>();
        if (Objects.nonNull(list)) {
            list.stream().filter(Objects::nonNull).forEach(each -> {
                final K key = keyFn.apply(each);
                final V value = valueFn.apply(each);
                if (Objects.nonNull(key) && Objects.nonNull(value)) {
                    grouped.put(key, value);
                }
            });
        }
        return grouped;
    }
}

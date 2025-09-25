package io.r2mo.base.util;

import io.r2mo.typed.common.Compared;
import io.r2mo.typed.json.JObject;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Ams 工具类 Tool
 *
 * @author lang : 2025-09-20
 */
public class R2MO {

    public static <T> T valueT(final T value, final Supplier<T> constructorFn) {
        return UTJson.valueT(value, constructorFn);
    }

    public static <T> T valueT(final JObject jsonJ, final String field, final Supplier<T> constructorFn) {
        return UTJson.valueT(jsonJ, field, constructorFn);
    }

    public static byte[] serialize(final Object object) {
        return UTJvm.serialize(object);
    }

    public static <T> T deserialize(final byte[] bytes) {
        return UTJvm.deserialize(bytes);
    }

    // =============== 数组类计算方法
    public static <T> Compared<T> elementDiff(
        final List<T> oldList, final List<T> newList, final String field) {
        return UTList.elementDiff(oldList, newList, field);
    }

    public static <T> List<T> elementIntersection(
        final List<T> list1, final List<T> list2, final String field) {
        return UTList.elementIntersection(list1, list2, field);
    }

    public static <T> List<T> elementUnion(
        final List<T> list1, final List<T> list2, final String field) {
        return UTList.elementUnion(list1, list2, field);
    }

    public static <T> List<T> elementSubtract(
        final List<T> source, final List<T> target, final String field) {
        return UTList.elementSubtract(source, target, field);
    }

    public static <T> Map<Object, List<T>> elementGroupBy(
        final List<T> list, final String field) {
        return UTList.elementGroupBy(list, field);
    }

    public static <T> T elementFirst(
        final List<T> list, final String field, final Object value) {
        return UTList.elementFirst(list, field, value);
    }

    public static <T> List<T> elementMany(
        final List<T> list, final String field, final Object value) {
        return UTList.elementMany(list, field, value);
    }

    public static <T> List<T> elementSortBy(
        final List<T> list, final String field, final boolean asc) {
        return UTList.elementSortBy(list, field, asc);
    }
}

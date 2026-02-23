package io.r2mo.base.util;

import io.r2mo.typed.common.Compared;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * @author lang : 2025-10-19
 */
class _UtilArray {
    protected _UtilArray() {
    }

    // =============== 数组类计算方法

    /**
     * 基于字段值对比新旧列表，计算新增(C)、更新(U)、删除(D)集。🧮
     *
     * <p>判定规则：
     * <ul>
     *   <li>以 {@code field} 指定的键字段为基准建立映射</li>
     *   <li>新中有、旧中无 → 新增(C)</li>
     *   <li>新中有、旧中也有 → 更新(U)</li>
     *   <li>旧中有、新中无 → 删除(D)</li>
     * </ul>
     *
     * @param oldList 旧列表（可为 null）
     * @param newList 新列表（可为 null）
     * @param field   参与比较的字段名（作为键）
     * @param <T>     元素类型
     * @return Compared 结果，含 C/U/D 三个队列
     */
    public static <T> Compared<T> elementDiff(
        final List<T> oldList, final List<T> newList, final String field) {
        return UTList.elementDiff(oldList, newList, field);
    }

    /**
     * 按字段键合并两个列表，返回一个<strong>无副作用</strong>的新列表。🔀
     *
     * <p>默认策略（与你前文要求一致）：
     * <ul>
     *   <li>仅当两边存在相同键时，用新元素的<strong>非空字段</strong>覆盖旧元素字段</li>
     *   <li>不新增、不删除，顺序以旧列表为准</li>
     *   <li>返回的是合并后的副本列表（不修改传入的对象）</li>
     * </ul>
     *
     * @param list1 旧列表
     * @param list2 新列表
     * @param field 键字段名
     * @param <T>   元素类型
     * @return 合并后的新列表
     */
    public static <T> List<T> elementCombine(
        final List<T> list1, final List<T> list2, final String field) {
        return UTList.elementCombine(list1, list2, field);
    }

    /**
     * 交集：返回在两个列表中<strong>都出现</strong>的元素（按字段键判断）。🤝
     *
     * @param list1 列表1
     * @param list2 列表2
     * @param field 键字段名
     * @param <T>   元素类型
     * @return 交集列表（保持 list1 的出现顺序）
     */
    public static <T> List<T> elementIntersection(
        final List<T> list1, final List<T> list2, final String field) {
        return UTList.elementIntersection(list1, list2, field);
    }

    /**
     * 并集：两个列表合并并按键去重（后放入者覆盖先前同键元素）。➕
     *
     * @param list1 列表1
     * @param list2 列表2
     * @param field 键字段名
     * @param <T>   元素类型
     * @return 去重后的并集列表（稳定顺序：list1 后接 list2 的去重结果）
     */
    public static <T> List<T> elementUnion(
        final List<T> list1, final List<T> list2, final String field) {
        return UTList.elementUnion(list1, list2, field);
    }

    /**
     * 差集：返回 {@code source - target}（按字段键判断）。➖
     *
     * @param source 源列表
     * @param target 目标列表
     * @param field  键字段名
     * @param <T>    元素类型
     * @return 仅存在于 source 而不在 target 的元素列表
     */
    public static <T> List<T> elementSubtract(
        final List<T> source, final List<T> target, final String field) {
        return UTList.elementSubtract(source, target, field);
    }

    /**
     * 按字段值分组。📦
     *
     * @param list  待分组列表
     * @param field 作为分组键的字段名
     * @param <T>   元素类型
     * @return Map：键为字段值，值为该键下的元素列表（保持原顺序）
     */
    public static <T> Map<Object, List<T>> elementGroupBy(
        final List<T> list, final String field) {
        return UTList.elementGroupBy(list, field);
    }

    public static <K, T> Map<K, List<T>> elementGroupBy(
            final String field,final List<T> entities, final Class<T> entityCls) {
        return UTList.elementGroupBy(entities, field, entityCls);
    }

    /**
     * 查找第一个匹配项（按字段值等于指定值）。🎯
     *
     * @param list  列表
     * @param field 字段名
     * @param value 目标值（可为 null）
     * @param <T>   元素类型
     * @return 第一个匹配的元素；未找到返回 null
     */
    public static <T> T elementFirst(
        final List<T> list, final String field, final Object value) {
        return UTList.elementFirst(list, field, value);
    }

    public static <T> T elementFirst(final List<T> list) {
        return UTList.elementFirst(list);
    }

    /**
     * 查找所有匹配项（按字段值等于指定值）。🔎
     *
     * @param list  列表
     * @param field 字段名
     * @param value 目标值（可为 null）
     * @param <T>   元素类型
     * @return 所有匹配元素列表（可能为空列表，保持原顺序）
     */
    public static <T> List<T> elementMany(
        final List<T> list, final String field, final Object value) {
        return UTList.elementMany(list, field, value);
    }

    /**
     * 按某字段进行排序（支持升/降序）。📈
     *
     * <p>要求该字段值实现 {@link Comparable}；null 值将排列在序列两端（具体取决于升序/降序）。</p>
     *
     * @param list  待排序列表（原地排序并返回同一引用）
     * @param field 字段名（其值应实现 Comparable）
     * @param asc   true 升序；false 降序
     * @param <T>   元素类型
     * @return 排序后的同一列表引用
     */
    public static <T> List<T> elementSortBy(
        final List<T> list, final String field, final boolean asc) {
        return UTList.elementSortBy(list, field, asc);
    }

    /**
     * 将列表构造成并发映射（自定义 key 与 value 提取函数）。🗺️⚡
     *
     * <p>适合在并发场景下作为缓存/索引使用；如遇重复键，后加入的元素会覆盖先前键值。</p>
     *
     * @param list    源列表
     * @param keyFn   键选择器（不可返回 null）
     * @param valueFn 值选择器
     * @param <K>     键类型
     * @param <V>     值类型
     * @param <E>     列表元素类型
     * @return ConcurrentMap 映射结果
     */
    public static <K, V, E> ConcurrentMap<K, V> elementMap(
        final List<E> list, final Function<E, K> keyFn, final Function<E, V> valueFn) {
        return UTList.elementMap(list, keyFn, valueFn);
    }

    /**
     * 将列表构造成并发映射（键选择器 + 值为元素自身）。🧩⚡
     *
     * <p>等价于 {@code elementMap(list, keyFn, Function.identity())}。</p>
     *
     * @param list  源列表
     * @param keyFn 键选择器（不可返回 null）
     * @param <K>   键类型
     * @param <V>   值类型（与列表元素类型一致）
     * @return ConcurrentMap 映射结果
     */
    public static <K, V> ConcurrentMap<K, V> elementMap(
        final List<V> list, final Function<V, K> keyFn) {
        return UTList.elementMap(list, keyFn, item -> item);
    }

    /**
     * 拉平操作，针对第一集合 List<E> 和第二集合 List<S> 执行 zipFn 的拉平函数处理
     * 拉平后最终返回一个新的 List<T> 集合
     *
     * <pre><code>
     *     [ F, F, F, F, F ] + [ S, S, S, S, S ] -> [ T, T, T, T, T ]
     * </code></pre>
     * <p>
     * 尺寸以第一个集合的尺寸为基础，类似左连接的方式进行拉平
     *
     * @param first  第一集合
     * @param second 第二集合
     * @param zipFn  拉平函数
     * @param <F>    第一集合元素类型
     * @param <S>    第二集合元素类型
     * @param <T>    拉平后的元素类型
     * @return 拉平后的集合
     */
    public static <F, S, T> List<T> elementZip(final List<F> first, final List<S> second, final BiFunction<F, S, T> zipFn) {
        return UTZip.zip(first, second, zipFn);
    }

    /**
     * 拉平操作，针对第一集合 List<E> 和第二集合 List<S> 执行拉平处理，拉平之后形成一个
     * 哈希表，key 为第一集合元素，value 为第二集合元素
     *
     * @param keys   key 集合
     * @param values value 集合
     * @param <F>    key 类型
     * @param <T>    value 类型
     * @return 拉平后的哈希表
     */
    public static <F, T> ConcurrentMap<F, T> elementZip(final List<F> keys, final List<T> values) {
        return UTZip.zip(keys, values);
    }

    /**
     * （重载）拉平操作，针对一个列表中的元素执行双属性的拉平
     * 最终拉平之后生成一个哈希表，key 为第一个属性，value 为第二个属性
     *
     * @param collection 待拉平的集合
     * @param keyFn      key 生成函数
     * @param valueFn    value 生成函数
     * @param <K>        key 类型
     * @param <V>        value 类型
     * @param <E>        待拉平集合元素类型
     * @return 拉平后的哈希表
     */
    public static <K, V, E> ConcurrentMap<K, V> elementZip(final E[] collection,
                                                           final Function<E, K> keyFn, final Function<E, V> valueFn) {
        return UTZip.zip(Arrays.asList(collection), keyFn, valueFn);
    }

    /**
     * 双哈希表的拉平操作，针对两个哈希表执行拉平叠加
     *
     * <pre><code>
     *     Map1: key = value
     *     Map2: value = element
     *     最终计算结果
     *     Map3: key = element
     * </code></pre>
     *
     * @param source 源哈希表
     * @param target 目标哈希表
     * @param <K>    源哈希表 key 类型
     * @param <T>    源哈希表 value 类型
     * @param <V>    目标哈希表 value 类型
     * @return 拉平后的哈希表
     */
    public static <K, T, V> ConcurrentMap<K, V> elementZip(final ConcurrentMap<K, T> source,
                                                           final ConcurrentMap<T, V> target) {
        return UTZip.zip(source, target);
    }

    /**
     * 集合和哈希表的拉平操作，只提取集合中包含的元素
     *
     * <pre><code>
     *     Set1: key1, key2
     *     Map2: key1 =
     *           key2 =
     *           key3 =
     *     最终计算结果
     *     Map3: key1 =
     *           key2 =
     * </code></pre>
     *
     * @param from 集合
     * @param to   哈希表
     * @param <K>  key 类型
     * @param <V>  value 类型
     * @return 拉平后的哈希表
     */
    public static <K, V> ConcurrentMap<K, V> elementZip(final Set<K> from, final ConcurrentMap<K, V> to) {
        return UTZip.zip(from, to);
    }

}

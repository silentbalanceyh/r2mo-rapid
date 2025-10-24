package io.r2mo.base.dbe.relation;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * SQL 重命名
 * <pre>
 *     1. 若使用了 `A` AS `B`
 *     2. 一个列可能会包含多个别名
 * </pre>
 * 所以此处的数据结构向量如
 * <pre>
 *     1. field -> column
 *     2. findAlias -> field -> column
 * </pre>
 *
 * @author lang : 2025-10-18
 */
public class JoinAlias {
    /**
     * 混合向量
     * <pre>
     *     1. field -> table
     *     2. findAlias -> table
     * </pre>
     */
    private final ConcurrentMap<String, String> mixVector = new ConcurrentHashMap<>();
    /**
     * 列别名映射
     * <pre>
     *     1. column -> alias1, alias2, alias3, ...
     *     2. 标准模式：column -> field
     *     3. 增强模式：column -> field, alias1, ...
     * </pre>
     */
    private final ConcurrentMap<String, Set<String>> mixColumn = new ConcurrentHashMap<>();

    /**
     * 根据两个名称提取表名
     * <pre>
     *     1. field -> table
     *     2. findAlias -> table
     * </pre>
     *
     * @param fieldOrAlias 字段名或者别名
     *
     * @return 返回表名
     */
    String findTable(final String fieldOrAlias) {
        return this.mixVector.getOrDefault(fieldOrAlias, null);
    }
}

package io.r2mo.dbe.mybatisplus.spi;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import io.r2mo.base.dbe.join.DBNode;
import io.r2mo.base.dbe.join.DBRef;
import io.r2mo.base.dbe.operation.QrAnalyzer;
import io.r2mo.base.dbe.syntax.QLeaf;
import io.r2mo.base.dbe.syntax.QNode;
import io.r2mo.base.dbe.syntax.QPager;
import io.r2mo.base.dbe.syntax.QQuery;
import io.r2mo.base.dbe.syntax.QSorter;
import io.r2mo.base.dbe.syntax.QTree;
import io.r2mo.base.dbe.syntax.QValue;
import io.r2mo.typed.common.Kv;
import io.r2mo.typed.exception.web._400BadRequestException;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author lang : 2025-10-23
 */
@Slf4j
public class OpJoinAnalyzer<T> implements QrAnalyzer<MPJQueryWrapper<T>> {
    private final DBRef ref;
    private final ConcurrentMap<Class<T>, MetaTable<T>> metaMap = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    public OpJoinAnalyzer(final DBRef ref) {
        this.ref = ref;
        ref.findAll().forEach(node -> {
            final Class<T> entityCls = (Class<T>) node.entity();
            this.metaMap.putIfAbsent(entityCls, MetaTable.of(entityCls));
        });
        log.info("[ R2MO ] 合计加载 JOIN 关联实体元信息：{}", this.metaMap.keySet());
    }

    @Override
    public MPJQueryWrapper<T> whereIn(final String field, final Object... values) {
        final QLeaf qValue = QValue.of(field, values);
        final String column = this.seekColumn(qValue.field());
        final MPJQueryWrapper<T> condition = this.waitForQuery();
        condition.in(column, MetaFix.toCollection(values));
        return condition;
    }

    @Override
    public MPJQueryWrapper<T> where(final Map<String, Object> map) {
        if (Objects.isNull(map) || map.isEmpty()) {
            // 特殊不带条件的模式，只能通过 Map.of() 传递
            return this.waitForQuery();
        }
        // 无嵌套
        final MPJQueryWrapper<T> condition = this.waitForQuery();
        map.forEach((field, value) -> {
            final QLeaf leaf = QValue.of(field, value);
            condition.and(w -> this.whereLeaf(leaf, w));
        });
        return condition;
    }

    @Override
    public MPJQueryWrapper<T> where(final String field, final Object value) {
        final QLeaf qValue = QValue.of(field, value);
        final MPJQueryWrapper<T> condition = this.waitForQuery();
        this.whereLeaf(qValue, condition);
        return condition;
    }

    @Override
    public MPJQueryWrapper<T> where(final QTree tree, final QSorter sorter) {
        if (Objects.isNull(tree)) {
            return null;
        }

        final MPJQueryWrapper<T> condition = this.waitForQuery();
        // 读取根节点
        final QNode node = tree.item();
        this.whereTree(node, condition);
        // 排序
        this.orderBy(condition, sorter);
        return condition;
    }

    @Override
    public MPJQueryWrapper<T> where(final QQuery query) {

        Objects.requireNonNull(query);
        // 条件 / 排序
        final MPJQueryWrapper<T> condition = this.where(query.criteria(), query.sorter());

        // 列过滤
        MetaFix.filterBy(condition, query.projection(), this::seekColumn);
        return condition;
    }

    @Override
    @SuppressWarnings("all")
    public IPage<?> page(final QQuery query) {
        if (Objects.isNull(query)) {
            return null;
        }
        final QPager pager = query.pager();
        if (Objects.isNull(pager)) {
            return null;
        }
        return new Page<>(pager.getPage(), pager.getSize());
    }

    private MPJQueryWrapper<T> waitForQuery() {
        final MPJQueryWrapper<T> query = new MPJQueryWrapper<>();
        final DBNode first = this.ref.find();
        final DBNode second = this.ref.findSecond();
        // 先设置别名
        final String firstAlias = this.ref.seekAlias(first.entity());
        query.setAlias(firstAlias);
        // A as LT
        // 再查找第二个表别名
        final String secondAlias = this.ref.seekAlias(second.entity());
        final Set<Kv<String, String>> joinOn = this.ref.seekJoin(second.entity());
        // 迭代处理 ON 条件
        final StringBuilder joinStr = new StringBuilder();
        joinStr.append(second.table()).append(" AS ").append(secondAlias).append(" ON (");
        final List<String> onList = new ArrayList<>();
        joinOn.forEach(vector -> {
            final String fieldJoin = vector.key();
            final String field = vector.value();
            // 翻译表名
            final String columnJoin = this.metaMap.get(second.entity()).vColumn(fieldJoin);
            final String column = this.metaMap.get(first.entity()).vColumn(field);
            onList.add(secondAlias + "." + columnJoin + " = " + firstAlias + "." + column);
        });
        joinStr.append(String.join(" AND ", onList));
        joinStr.append(")");
        log.info("[ R2MO ] 构建 MPJ / JOIN 语句: \uD83C\uDF9F️ `{}`", joinStr);
        query.leftJoin(joinStr.toString());
        return query;
    }

    private void whereTree(final QNode node, final MPJQueryWrapper<T> query) {
        MetaFix.whereTree(node, query, leaf -> this.seekColumn(leaf.field()));
    }

    private void whereLeaf(final QLeaf node, final MPJQueryWrapper<T> query) {
        MetaFix.whereLeaf(node, query, leaf -> this.seekColumn(leaf.field()));
    }

    private void orderBy(final MPJQueryWrapper<T> query, final QSorter sorter) {
        MetaFix.orderBy(query, sorter, this::seekColumn);
    }

    /**
     * 通过属性名查找列对应信息
     * <pre>
     *     1. 列中包含了表别名，别名会出现在 SQL 语句中
     *     2. 属性名 -> 列表名
     * </pre>
     *
     * @param field 属性名
     *
     * @return 表别名 + 列名
     */
    private String seekColumn(final String field) {
        // 1) 先用主实体
        final DBNode node = this.ref.find();
        final MetaTable<?> primary = this.metaMap.get(node.entity());
        final String c1 = primary.vColumn(field);
        if (c1 != null) {
            final String tableAlias = this.ref.seekAlias(node.entity());
            return tableAlias + "." + c1;
        }

        // 2) 其他实体（第一个匹配即返回）
        for (final Class<?> other : this.metaMap.keySet()) {
            final MetaTable<?> meta = this.metaMap.get(other);
            if (meta == primary) {
                continue;
            }
            final String c2 = meta.vColumn(field);
            if (c2 != null) {
                // 这里假定 MetaTable 提供表名访问（常见命名：table() / tableName()）
                final String tableAlias = this.ref.seekAlias(other);
                return tableAlias + "." + c2;
            }
        }

        // 3) 都找不到 -> 400
        throw new _400BadRequestException("[ R2MO ] 无法识别的查询字段: " + field);
    }
}

package io.r2mo.dbe.mybatisplus.spi;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.r2mo.base.dbe.DBMeta;
import io.r2mo.base.dbe.common.DBNode;
import io.r2mo.base.dbe.operation.QrAnalyzer;
import io.r2mo.base.dbe.syntax.QLeaf;
import io.r2mo.base.dbe.syntax.QNode;
import io.r2mo.base.dbe.syntax.QPager;
import io.r2mo.base.dbe.syntax.QQuery;
import io.r2mo.base.dbe.syntax.QSorter;
import io.r2mo.base.dbe.syntax.QTree;
import io.r2mo.base.dbe.syntax.QValue;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

/**
 * @author lang : 2025-08-28
 */
class QrAnalyzerImpl<T> implements QrAnalyzer<QueryWrapper<T>> {
    private final Class<T> entityCls;
    private final DBNode node;

    QrAnalyzerImpl(final Class<T> entityCls) {
        this.entityCls = entityCls;
        this.node = DBMeta.of().findBy(entityCls);
    }

    @Override
    public QueryWrapper<T> whereIn(final String field, final Object... values) {
        final QLeaf qValue = QValue.of(field, values);
        final String column = this.node.vColumn(qValue.field());
        return Wrappers.query(this.entityCls).in(column, Arrays.asList(values));
    }

    @Override
    public QueryWrapper<T> where(final String field, final Object value) {
        final QLeaf qValue = QValue.of(field, value);
        final QueryWrapper<T> condition = Wrappers.query(this.entityCls);
        this.whereLeaf(qValue, condition);
        return condition;
    }

    @Override
    public QueryWrapper<T> where(final Map<String, Object> map) {
        if (Objects.isNull(map) || map.isEmpty()) {
            // 特殊不带条件的模式，只能通过 Map.of() 传递
            return Wrappers.query();
        }
        // 无嵌套
        final QueryWrapper<T> condition = Wrappers.query(this.entityCls);
        map.forEach((field, value) -> {
            final QLeaf leaf = QValue.of(field, value);
            condition.and(w -> this.whereLeaf(leaf, w));
        });
        return condition;
        /*
         * FIX-DBE: 旧代码，此处的 Map 允许追加操作符作为 AND 条件，只是不支持嵌套而已
         * final Map<String, Object> column = this.meta.vColumn(condition);
         * return Wrappers.query(this.entityCls).allEq(column);
         * 旧版的类似 orderId,> 这种不允许出现在 Map 中会导致一定问题
         */
    }

    @Override
    public QueryWrapper<T> where(final QTree tree, final QSorter sorter) {
        if (Objects.isNull(tree)) {
            return null;
        }
        final QueryWrapper<T> condition = Wrappers.query(this.entityCls);
        // 读取根节点
        final QNode root = tree.item();
        this.whereTree(root, condition);
        // 排序
        this.orderBy(condition, sorter);
        return condition;
    }

    @Override
    public QueryWrapper<T> where(final QQuery query) {
        Objects.requireNonNull(query);
        // 条件 / 排序
        final QueryWrapper<T> condition = this.where(query.criteria(), query.sorter());

        // 列过滤
        MetaFix.filterBy(condition, query.projection(), this.node::vColumn);
        return condition;
    }

    @Override
    @SuppressWarnings("all")
    public IPage<T> page(final QQuery query) {
        if (Objects.isNull(query)) {
            return null;
        }
        final QPager pager = query.pager();
        if (Objects.isNull(pager)) {
            return null;
        }
        return new Page<>(pager.getPage(), pager.getSize());
    }

    private void whereTree(final QNode node, final QueryWrapper<T> query) {
        MetaFix.whereTree(node, query, leaf -> this.node.vColumn(leaf.field()));
    }

    private void whereLeaf(final QLeaf node, final QueryWrapper<T> query) {
        MetaFix.whereLeaf(node, query, leaf -> this.node.vColumn(leaf.field()));
    }

    private void orderBy(final QueryWrapper<T> query, final QSorter sorter) {
        MetaFix.orderBy(query, sorter, this.node::vColumn);
    }
}

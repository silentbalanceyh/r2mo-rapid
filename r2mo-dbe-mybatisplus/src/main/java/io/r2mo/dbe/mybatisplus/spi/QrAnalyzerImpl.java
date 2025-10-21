package io.r2mo.dbe.mybatisplus.spi;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.r2mo.base.dbe.constant.QOp;
import io.r2mo.base.dbe.operation.QrAnalyzer;
import io.r2mo.base.dbe.syntax.QBranch;
import io.r2mo.base.dbe.syntax.QLeaf;
import io.r2mo.base.dbe.syntax.QNode;
import io.r2mo.base.dbe.syntax.QPager;
import io.r2mo.base.dbe.syntax.QProjection;
import io.r2mo.base.dbe.syntax.QQuery;
import io.r2mo.base.dbe.syntax.QSorter;
import io.r2mo.base.dbe.syntax.QTree;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author lang : 2025-08-28
 */
class QrAnalyzerImpl<T> implements QrAnalyzer<QueryWrapper<T>> {
    private final Class<T> entityCls;
    private final MetaTable<T> meta;

    QrAnalyzerImpl(final Class<T> entityCls) {
        this.entityCls = entityCls;
        this.meta = MetaTable.of(entityCls);
    }

    @Override
    public QueryWrapper<T> whereIn(final String field, final Object... values) {
        final String column = this.meta.vColumn(field);
        return Wrappers.query(this.entityCls).in(column, Arrays.asList(values));
    }

    @Override
    public QueryWrapper<T> where(final String field, final Object value) {
        final String column = this.meta.vColumn(field);
        return Wrappers.query(this.entityCls).eq(column, value);
    }

    @Override
    public QueryWrapper<T> where(final Map<String, Object> condition) {
        if (Objects.isNull(condition) || condition.isEmpty()) {
            // 特殊不带条件的模式，只能通过 Map.of() 传递
            return Wrappers.query();
        }
        
        final Map<String, Object> column = this.meta.vColumn(condition);
        return Wrappers.query(this.entityCls).allEq(column);
    }

    @Override
    public QueryWrapper<T> where(final QTree tree, final QSorter sorter) {
        if (Objects.isNull(tree)) {
            return null;
        }
        final QueryWrapper<T> condition = Wrappers.query(this.entityCls);
        // 读取根节点
        final QNode root = tree.item();
        this.where(root, condition);
        // 排序
        this.meta.orderBy(condition, sorter);
        return condition;
    }

    @Override
    public QueryWrapper<T> where(final QQuery query) {
        Objects.requireNonNull(query);
        // 条件 / 排序
        final QueryWrapper<T> condition = this.where(query.criteria(), query.sorter());


        // 列过滤
        final QProjection fields = query.projection();
        if (fields.isOk()) {
            final List<String> fieldList = fields.item();
            if (!fieldList.isEmpty()) {
                final List<String> columnList = this.meta.vColumn(fieldList);
                condition.select(columnList);
            }
        }
        return condition;
    }

    private void where(final QNode node, final QueryWrapper<T> query) {
        if (node instanceof final QBranch branch) {
            // 节点不是子节点
            this.whereInternal(branch, query);
        } else if (node instanceof final QLeaf leaf) {
            // 节点是子节点
            this.whereInternal(leaf, query);
        }
    }

    private void whereInternal(final QBranch branch, final QueryWrapper<T> query) {
        final Set<QNode> nodes = branch.nodes();
        final QOp op = branch.op();
        nodes.forEach(node -> {
            if (QOp.AND == op) {
                query.and(w -> this.where(node, w));
            } else {
                query.or(w -> this.where(node, w));
            }
        });
    }

    private void whereInternal(final QLeaf leaf, final QueryWrapper<T> query) {
        final QOp op = leaf.op();
        final String column = this.meta.vColumn(leaf.field());
        switch (op) {
            case EQ -> query.eq(column, leaf.value());                // ==
            case NEQ -> query.ne(column, leaf.value());               // <>
            case GT -> query.gt(column, leaf.value());                // >
            case GTE -> query.ge(column, leaf.value());               // >=
            case LT -> query.lt(column, leaf.value());                // <
            case LTE -> query.le(column, leaf.value());               // <=
            case CONTAIN -> query.like(column, leaf.value());         // like '%value%'
            case START -> query.likeRight(column, leaf.value());      // like 'value%'
            case END -> query.likeLeft(column, leaf.value());         // like '%value'
            // Fix Issue 1: 存在类型转换的软处理流程
            case IN -> this.meta.in(leaf, query);                         // stream (value1, value2, ...)
            case NOT_IN -> query.notIn(column, leaf.value());         // not stream (value1, value2, ...)
            case NULL -> query.isNull(column);                        // is null
            case NOT_NULL -> query.isNotNull(column);                 // is not null
        }
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
}

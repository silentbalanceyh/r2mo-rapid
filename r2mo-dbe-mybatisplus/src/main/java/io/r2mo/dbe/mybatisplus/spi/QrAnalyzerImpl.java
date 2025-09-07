package io.r2mo.dbe.mybatisplus.spi;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.r2mo.base.dbe.constant.QOp;
import io.r2mo.base.dbe.operation.QrAnalyzer;
import io.r2mo.base.dbe.syntax.*;

import java.util.*;

/**
 * @author lang : 2025-08-28
 */
class QrAnalyzerImpl<T> implements QrAnalyzer<QueryWrapper<T>> {
    private final Class<T> entityCls;

    QrAnalyzerImpl(final Class<T> entityCls) {
        this.entityCls = entityCls;
    }

    @Override
    public QueryWrapper<T> whereIn(final String field, final Object... values) {
        return Wrappers.query(this.entityCls).in(field, Arrays.asList(values));
    }

    @Override
    public QueryWrapper<T> where(final String field, final Object value) {
        return Wrappers.query(this.entityCls).eq(field, value);
    }

    @Override
    public QueryWrapper<T> where(final Map<String, Object> condition) {
        return Wrappers.query(this.entityCls).allEq(condition);
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
        this.orderBy(condition, sorter);
        return condition;
    }

    @Override
    public QueryWrapper<T> where(final QQuery query) {
        Objects.requireNonNull(query);
        // 条件 / 排序
        final QueryWrapper<T> condition = this.where(query.criteria(), query.sorter());


        // 列过滤
        final QProjection columns = query.projection();
        if (columns.isOk()) {
            final List<String> columnList = columns.item();
            if (!columnList.isEmpty()) {
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
        switch (op) {
            case EQ -> query.eq(leaf.field(), leaf.value());                // ==
            case NEQ -> query.ne(leaf.field(), leaf.value());               // <>
            case GT -> query.gt(leaf.field(), leaf.value());                // >
            case GTE -> query.ge(leaf.field(), leaf.value());               // >=
            case LT -> query.lt(leaf.field(), leaf.value());                // <
            case LTE -> query.le(leaf.field(), leaf.value());               // <=
            case CONTAIN -> query.like(leaf.field(), leaf.value());         // like '%value%'
            case START -> query.likeRight(leaf.field(), leaf.value());      // like 'value%'
            case END -> query.likeLeft(leaf.field(), leaf.value());         // like '%value'
            // Fix Issue 1: 存在类型转换的软处理流程
            case IN -> QrSoftValue.in(leaf, query);                         // in (value1, value2, ...)
            case NOT_IN -> query.notIn(leaf.field(), leaf.value());         // not in (value1, value2, ...)
            case NULL -> query.isNull(leaf.field());                        // is null
            case NOT_NULL -> query.isNotNull(leaf.field());                 // is not null
        }
    }

    private void orderBy(final QueryWrapper<T> query, final QSorter sorter) {
        if (Objects.isNull(sorter)) {
            return;
        }
        sorter.item().forEach(kv -> {
            final boolean isAsc = kv.value();
            final String field = kv.key();
            if (isAsc) {
                query.orderByAsc(field);
            } else {
                query.orderByDesc(field);
            }
        });
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

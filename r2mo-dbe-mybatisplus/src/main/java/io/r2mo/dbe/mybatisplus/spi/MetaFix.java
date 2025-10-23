package io.r2mo.dbe.mybatisplus.spi;

import com.baomidou.mybatisplus.core.conditions.AbstractWrapper;
import com.baomidou.mybatisplus.core.conditions.query.Query;
import io.r2mo.base.dbe.constant.QOp;
import io.r2mo.base.dbe.join.DBRef;
import io.r2mo.base.dbe.syntax.QBranch;
import io.r2mo.base.dbe.syntax.QLeaf;
import io.r2mo.base.dbe.syntax.QNode;
import io.r2mo.base.dbe.syntax.QProjection;
import io.r2mo.base.dbe.syntax.QSorter;
import io.r2mo.spi.SPI;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

/**
 * @author lang : 2025-10-23
 */
@Slf4j
class MetaFix {

    static <T> ConcurrentMap<Class<?>, MetaTable<?>> toMetaMap(final DBRef ref) {
        final ConcurrentMap<Class<?>, MetaTable<?>> metaMap = new ConcurrentHashMap<>();
        ref.findAll().forEach(node -> {
            final Class<?> entityCls = node.entity();
            metaMap.putIfAbsent(entityCls, MetaTable.of(entityCls));
        });
        return metaMap;
    }

    /*
     * Fix Issue: Cannot convert string '\xAC\xED\x00\x05sr...' from binary to utf8mb4
     */
    static Collection<?> toCollection(final Object value) {
        if (value instanceof Collection<?>) {
            return (Collection<?>) value;
        } else {
            /*
             * 有可能是实现部分，所以此处的核心转换要借用 UTIL 中的内容来完成
             */
            return SPI.V_UTIL.toCollection(value);
        }
    }

    static <T, Q extends Query<Q, T, String>> void filterBy(
        final Q query, final QProjection fields, final Function<String, String> columnFn) {
        if (fields.isOk()) {
            final List<String> fieldList = fields.item();
            if (!fieldList.isEmpty()) {
                final List<String> columnList = fieldList
                    .stream().map(columnFn).filter(Objects::nonNull).toList();
                query.select(columnList);
            }
        }
    }

    static <T, Q extends AbstractWrapper<T, String, Q>> void orderBy(
        final Q query, final QSorter sorter, final Function<String, String> columnFn) {
        if (Objects.isNull(sorter)) {
            return;
        }
        sorter.item().forEach(kv -> {
            final boolean isAsc = kv.value();
            final String field = kv.key();
            final String column = columnFn.apply(field);
            if (isAsc) {
                query.orderByAsc(column);
            } else {
                query.orderByDesc(column);
            }
        });
    }

    static <T, Q extends AbstractWrapper<T, String, Q>> void whereTree(
        final QNode node, final Q query, final Function<QLeaf, String> columnFn) {
        if (node instanceof final QBranch branch) {
            // 节点不是子节点
            whereBranch(branch, query, columnFn);
        } else if (node instanceof final QLeaf leaf) {
            // 节点是子节点
            whereLeaf(leaf, query, columnFn);
        }
    }

    private static <T, Q extends AbstractWrapper<T, String, Q>> void whereBranch(
        final QBranch branch, final Q query, final Function<QLeaf, String> columnFn) {
        final Set<QNode> nodes = branch.nodes();
        final QOp op = branch.op();
        nodes.forEach(node -> {
            if (QOp.AND == op) {
                query.and(w -> whereTree(node, w, columnFn));
            } else {
                query.or(w -> whereTree(node, w, columnFn));
            }
        });
    }

    static <T, Q extends AbstractWrapper<T, String, Q>> void whereLeaf(
        final QLeaf leaf, final Q query, final Function<QLeaf, String> columnFn) {
        final String column = columnFn.apply(leaf);
        switch (leaf.op()) {
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
            case IN -> query.in(column, toCollection(leaf.value()));
            // stream (value1, value2, ...)
            case NOT_IN -> query.notIn(column, leaf.value());         // not stream (value1, value2, ...)
            case NULL -> query.isNull(column);                        // is null
            case NOT_NULL -> query.isNotNull(column);                 // is not null
        }
    }
}

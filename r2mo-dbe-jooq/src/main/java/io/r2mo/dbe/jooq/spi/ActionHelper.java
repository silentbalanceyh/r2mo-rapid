package io.r2mo.dbe.jooq.spi;

import io.r2mo.base.dbe.syntax.QSorter;
import io.r2mo.base.dbe.syntax.QValue;
import io.r2mo.dbe.jooq.core.condition.Clause;
import io.r2mo.dbe.jooq.core.gap.MySQLGap;
import io.r2mo.spi.SPI;
import io.r2mo.typed.common.Kv;
import io.r2mo.typed.json.JObject;
import io.r2mo.typed.json.JUtil;
import lombok.extern.slf4j.Slf4j;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Operator;
import org.jooq.OrderField;
import org.jooq.impl.DSL;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * @author lang : 2025-10-19
 */
@SuppressWarnings("all")
@Slf4j
class ActionHelper {
    private static final JUtil UT = SPI.V_UTIL;

    static List<OrderField<?>> forOrderBy(final QSorter sorter,
                                          final Function<String, Field<?>> columnFn,
                                          final Function<String, String> prefixFn) {
        final List<Kv<String, Boolean>> items = sorter.items();
        final List<OrderField<?>> orders = new ArrayList<>();
        items.forEach(kv -> {
            final boolean asc = kv.value();
            final String field = kv.key();
            // 列解析
            final Field<?> column = columnFn.apply(field);


            // 处理前缀
            final String prefix;
            if (Objects.isNull(prefixFn)) {
                prefix = null;
            } else {
                prefix = prefixFn.apply(column.getName());
            }


            // 根据前缀处理
            if (Objects.isNull(prefix)) {
                orders.add(asc ? column.asc() : column.desc());
            } else {
                final Field<?> normalized = DSL.field(prefix + "." + column.getName());
                orders.add(asc ? normalized.asc() : normalized.desc());
            }
        });
        return orders;
    }
    // ------------------ 核心API调用 ------------------

    public static Condition transform(final JObject filters,
                                      final Function<String, Field> fnAnalyze,
                                      final Function<String, String> fnTable) {
        return transform(filters, null, fnAnalyze, fnTable);
    }

    public static Condition transform(final JObject filters,
                                      final Function<String, Field> fnAnalyze) {
        return transform(filters, null, fnAnalyze);
    }

    public static Condition transform(final JObject filters,
                                      final Operator operator,
                                      final Function<String, Field> fnAnalyze) {
        return transform(filters, operator, fnAnalyze, null);
    }

    // ------------------ 下边是私有方法 ------------------
    private static Condition transform(final JObject filters, final Operator operator,
                                       final Function<String, Field> columnFn,
                                       final Function<String, String> prefixFn) {
        final Condition condition;
        final boolean isTree = filters.isNested();
        if (!filters.isEmpty()) {
            log.debug("[ ZERO ] 模式选择：{}", isTree ? "树型结构" : "线性结构");
        }


        /*
         * 若 operator = null, 先计算操作符，如果 "" 节点存在，则以节点值为主
         * 1）"" = true -> AND
         * 2）"" = false -> OR
         * 若 operator != null, 则以传入的 operator 为主
         */
        final Operator opFinal = Objects.isNull(operator) ? forOperator(filters) : operator;
        if (isTree) {
            filters.put("", opFinal == Operator.AND);   // 回写操作符
            condition = transformTree(filters, columnFn, prefixFn);
        } else {
            condition = transformLeaf(filters, opFinal, columnFn, prefixFn);
        }
        return condition;
    }

    private static Operator forOperator(final JObject data) {
        final Operator operator;
        if (data.containsKey("")) {
            final boolean isAnd = Boolean.parseBoolean(data.get("").toString());
            operator = isAnd ? Operator.AND : Operator.OR;
        } else {
            // 默认使用 OR 连接，和 MyBatis-Plus 不同
            operator = Operator.OR;
        }
        return operator;
    }

    private static JObject forLinear(final JObject filters) {
        final JObject copied = filters.copy();
        for (final String field : filters.fieldNames()) {
            final Object value = copied.get(field);
            if (UT.isJObject(value)) {
                copied.remove(field);
            }
        }
        return copied;
    }

    private static Condition transformTree(final JObject tree,
                                           final Function<String, Field> columnFn,
                                           final Function<String, String> prefixFn) {
        final Condition condition;
        // 计算操作符
        final Operator operator = forOperator(tree);


        // 计算是叶节点还是子节点
        final JObject copied = tree.copy();
        copied.remove("");          // 操作符节点不作为条件节点，所以移除


        // 线性节点
        final Condition condLine = transformLeaf(forLinear(copied), operator, columnFn, prefixFn);


        // 计算所有树型节点
        final List<Condition> condTree = transformLeaf(tree, columnFn, prefixFn);


        // 本层线性节点追加
        if (Objects.nonNull(condLine)) {
            condTree.add(condLine);
        }


        // 最终条件连接
        if (1 == condTree.size()) {
            condition = condTree.get(0);
        } else {
            condition = (Operator.AND == operator) ? DSL.and(condTree) : DSL.or(condTree);
        }
        return condition;
    }

    private static List<Condition> transformLeaf(final JObject tree,
                                                 final Function<String, Field> columnFn,
                                                 final Function<String, String> prefixFn) {
        final List<Condition> conditions = new ArrayList<>();
        if (!tree.isEmpty()) {
            for (final String field : tree.fieldNames()) {
                final Object value = tree.get(field);
                if (!UT.isJObject(value)) {
                    // 跳过，不处理非 tree 类型子节点
                    continue;
                }

                // 合法基础语法树解析
                conditions.add(transformTree(UT.toJObject(value), columnFn, prefixFn));
            }
        }
        return conditions;
    }

    private static Condition transformLeaf(final JObject filters, final Operator operator,
                                           final Function<String, Field> columnFn,
                                           final Function<String, String> prefixFn) {
        /* 只有线性内容，没有嵌套，所以可以直接转换成条件的列表 */
        final List<Condition> conditions = new ArrayList<>();

        for (final String field : filters.fieldNames()) {


            /* 从 JObject 中提取值信息 */
            final Object value = filters.get(field);


            /* 根据列值得到 OOp 的转换模式 */
            final QValue qValue = QValue.of(field, value);


            final Condition item;
            /* 字段名处理 */
            if (Objects.nonNull(columnFn)) {


                // 函数 columnFn 调用 -> 得到之后绑定类型
                final Field<?> columnField = columnFn.apply(field);
                // <-- type 绑定
                qValue.type(columnField.getType());


                // 函数 prefixFn 调用
                final String column = MySQLGap.normFor(columnField.getName(), prefixFn);


                // 构造新的 QValue
                final QValue copied = QValue.copyOf(qValue, column);
                // --> type 有类型
                final Clause clause = Clause.of(copied);


                // 计算最终条件
                item = clause.where(columnField, copied);
            } else {
                // 无 columnFn 调用，直接使用原始字段名
                final Clause clause = Clause.of(qValue);


                final String column = MySQLGap.normFor(field, prefixFn);


                final Field<?> columnField = DSL.field(column);
                item = clause.where(columnField, qValue);
            }
            conditions.add(item);
        }
        return (Operator.AND == operator) ? DSL.and(conditions) : DSL.or(conditions);
    }
}

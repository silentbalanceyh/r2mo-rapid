package io.r2mo.dbe.jooq.spi;

import io.r2mo.base.dbe.syntax.QSorter;
import io.r2mo.spi.SPI;
import io.r2mo.typed.common.Kv;
import io.r2mo.typed.json.JObject;
import io.r2mo.typed.json.JUtil;
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
class QrHelper {
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

    // ------------------ 下边是私有方法 ------------------
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

    private static Condition transformLinear(final JObject filters, final Operator operator,
                                             final Function<String, Field<?>> columnFn,
                                             final Function<String, String> prefixFn) {
        /* 只有线性内容，没有嵌套，所以可以直接转换成条件的列表 */
        final List<Condition> conditions = new ArrayList<>();

        for (final String field : filters.fieldNames()) {


            /* 从 JObject 中提取值信息 */
            final Object value = filters.get(field);


            /* 根据列值得到 OOp 的转换模式 */
        }
        return null;
    }
}

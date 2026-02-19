package io.r2mo.dbe.jooq.spi;

import io.r2mo.base.dbe.constant.QOp;
import io.r2mo.base.dbe.operation.QrAnalyzer;
import io.r2mo.base.dbe.syntax.QQuery;
import io.r2mo.base.dbe.syntax.QSorter;
import io.r2mo.base.dbe.syntax.QTree;
import io.r2mo.base.dbe.syntax.QValue;
import io.r2mo.dbe.jooq.core.condition.Clause;
import io.r2mo.dbe.jooq.core.domain.JooqMeta;
import io.r2mo.typed.exception.web._501NotSupportException;
import io.r2mo.typed.json.JObject;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.impl.DSL;

import java.util.*;

/**
 * @author lang : 2025-10-19
 */
public class QrAnalyzerJooq implements QrAnalyzer<Condition> {
    private final JooqMeta meta;

    public QrAnalyzerJooq(final Class<?> entityCls, final DSLContext context) {
        final JooqMeta detect = LoadREF.of().loadMeta(entityCls);
        Objects.requireNonNull(detect, "[ R2MO ] 无法从实体类中提取元数据：" + entityCls.getName());
        this.meta = detect;
    }

    @Override
    public Condition whereIn(final String field, final Object... values) {
        final Field<?> column = this.meta.findColumn(field);
        final QValue value = QValue.of(column.getName(), QOp.IN, values).type(column.getType());
        return Clause.of(value).where(column, value);
    }

    @Override
    public Condition where(final Map<String, Object> condition) {
        if (Objects.isNull(condition) || condition.isEmpty()) {
            // 特殊不带条件的模式，只能通过 Map.of() 传递
            return DSL.trueCondition();
        }


        final List<Condition> conditions = new ArrayList<>();
        condition.forEach((k, v) -> {
            final Field<?> column = this.meta.findColumn(k);
            final QValue qValue = QValue.of(column.getName(), QOp.EQ, v).type(column.getType());
            final Condition cond = Clause.of(qValue).where(column, qValue);
            conditions.add(cond);
        });
        return DSL.and(conditions);
    }

    @Override
    public Condition where(final String field, final Object value) {
        final Field<?> column = this.meta.findColumn(field);

        // 判断 value 是否是集合类型
        final QOp operator;
        final Object actualValue;

        if (value instanceof Collection && !((Collection<?>) value).isEmpty()) {
            // 集合类型，使用 IN 操作符
            operator = QOp.IN;
            actualValue = value;  // 保持集合形式
        } else if (value instanceof Object[] && ((Object[]) value).length > 0) {
            // 数组类型，转成 List 并使用 IN
            operator = QOp.IN;
            actualValue = Arrays.asList((Object[]) value);
        } else {
            // 单值类型，使用 EQ
            operator = QOp.EQ;
            actualValue = value;
        }

        final QValue qValue = QValue.of(column.getName(), operator, actualValue)
                .type(column.getType());
        return Clause.of(qValue).where(column, qValue);
    }

    // 此处 sorter 会忽略
    @Override
    public Condition where(final QTree tree, final QSorter sorter) {
        // Fix: java.lang.NullPointerException: Cannot invoke "io.r2mo.base.dbe.syntax.QTree.data()" because "tree" is null
        if (Objects.isNull(tree)) {
            return DSL.trueCondition();
        }
        if (!tree.isOk()) {
            return DSL.trueCondition();
        }
        final JObject treeJ = tree.data();
        return JooqHelper.transform(treeJ, this.meta::findColumn);
    }

    // 此处只处理 QTree
    @Override
    public Condition where(final QQuery query) {
        if (Objects.isNull(query)) {
            return DSL.trueCondition();
        }
        final QTree tree = query.criteria();
        if (Objects.isNull(tree)) {
            return DSL.trueCondition();
        }
        if (!tree.isOk()) {
            return DSL.trueCondition();
        }
        return this.where(tree, query.sorter());
    }

    @Override
    public <PAGE> PAGE page(final QQuery query) {
        throw new _501NotSupportException("[ R2MO ] QrAnalyzerCondition 不支持分页操作，请调用其他 API 在外层实现分页逻辑！");
    }
}

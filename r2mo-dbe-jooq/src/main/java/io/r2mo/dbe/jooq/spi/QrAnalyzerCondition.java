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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author lang : 2025-10-19
 */
class QrAnalyzerCondition implements QrAnalyzer<Condition> {
    private final JooqMeta meta;

    QrAnalyzerCondition(final Class<?> entityCls, final DSLContext context) {
        this.meta = JooqMeta.getOr(entityCls);
        Objects.requireNonNull(this.meta, "[ R2MO ] 无法从实体类中提取元数据：" + entityCls.getName());
    }

    @Override
    public Condition whereIn(final String field, final Object... values) {
        final Field<?> column = this.meta.findColumn(field);
        final QValue value = QValue.of(column.getName(), QOp.IN, values).type(column.getType());
        return Clause.of(value).where(column, value);
    }

    @Override
    public Condition where(final Map<String, Object> condition) {
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
        final QValue qValue = QValue.of(column.getName(), QOp.EQ, value).type(column.getType());
        return Clause.of(qValue).where(column, qValue);
    }

    // 此处 sorter 会忽略
    @Override
    public Condition where(final QTree tree, final QSorter sorter) {
        final JObject treeJ = tree.data();
        return QrHelper.transform(treeJ, this.meta::findColumn);
    }

    // 此处只处理 QTree
    @Override
    public Condition where(final QQuery query) {
        final QTree tree = query.criteria();
        if (Objects.isNull(tree)) {
            return DSL.trueCondition();
        }
        return this.where(tree, null);
    }

    @Override
    public <PAGE> PAGE page(final QQuery query) {
        throw new _501NotSupportException("[ R2MO ] QrAnalyzerCondition 不支持分页操作，请调用其他 API 在外层实现分页逻辑！");
    }
}

package io.r2mo.dbe.jooq.spi;

import io.r2mo.base.dbe.common.DBRef;
import io.r2mo.base.dbe.operation.QrAnalyzer;
import io.r2mo.base.dbe.syntax.QLeaf;
import io.r2mo.base.dbe.syntax.QQuery;
import io.r2mo.base.dbe.syntax.QSorter;
import io.r2mo.base.dbe.syntax.QTree;
import io.r2mo.base.dbe.syntax.QValue;
import io.r2mo.typed.json.JObject;
import lombok.extern.slf4j.Slf4j;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.impl.DSL;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author lang : 2025-10-24
 */
@Slf4j
public class QrAnalyzerJoin implements QrAnalyzer<Condition> {
    private final DBRef ref;

    public QrAnalyzerJoin(final DBRef ref) {
        this.ref = ref;
    }

    @Override
    public Condition whereIn(final String field, final Object... values) {
        final QLeaf qValue = QValue.of(field, values);
        final String columnName = this.ref.seekColumn(qValue.field());
        final Field<Object> column = DSL.field(columnName);
        return column.in(values);
    }

    @Override
    public Condition where(final Map<String, Object> map) {
        if (Objects.isNull(map) || map.isEmpty()) {
            return DSL.trueCondition();    // 特殊不带条件的模式，只能通过 Map.of() 传递
        }

        final List<Condition> conditions = new ArrayList<>();
        map.forEach((k, v) -> {
            final Condition condition = JooqHelper.transform(k, v, this::findColumn);
            conditions.add(condition);
        });
        return DSL.and(conditions);
    }

    @Override
    public Condition where(final String field, final Object value) {
        return JooqHelper.transform(field, value, this::findColumn);
    }

    @Override
    public Condition where(final QTree tree, final QSorter sorter) {
        if (Objects.isNull(tree)) {
            return DSL.falseCondition();        // 只有 Map 为空才适合 true 条件
        }
        final JObject filters = tree.data();    // 兼容写法
        return JooqHelper.transform(filters, this::findColumn);
    }

    private Field<?> findColumn(final String field) {
        final String column = this.ref.seekColumn(field);
        return DSL.field(column);
    }

    @Override
    public Condition whereId(final Serializable id) {
        return null;
    }

    @Override
    public Condition where(final QQuery query) {
        return null;
    }

    @Override
    public <PAGE> PAGE page(final QQuery query) {
        return null;
    }
}

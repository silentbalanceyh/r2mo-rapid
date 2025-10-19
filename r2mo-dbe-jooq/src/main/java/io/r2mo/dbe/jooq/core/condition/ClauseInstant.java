package io.r2mo.dbe.jooq.core.condition;

import io.r2mo.base.dbe.syntax.QValue;
import io.r2mo.base.util.R2MO;
import org.jooq.Condition;
import org.jooq.Field;

import java.util.Objects;

/**
 * @author lang : 2025-10-19
 */
class ClauseInstant extends ClauseString {
    @Override
    public Condition where(final Field<?> field, final QValue qValue) {
        final Object value = qValue.value();
        if (Objects.isNull(value)) {
            return null;
        }


        final Object normalized = R2MO.parseFull(value.toString());
        // 值更新
        final QValue waitFor = QValue.copyOf(qValue, normalized);
        if (Objects.nonNull(qValue.mark())) {
            // mark 不为空的情况
            return ClauseFun.MARK_MAP.get(qValue.mark())
                .apply(field, waitFor);
        } else {
            // mark = null
            return ClauseFun.DATE_MAP.get(qValue.op().name())
                .apply(field, waitFor);
        }
    }
}

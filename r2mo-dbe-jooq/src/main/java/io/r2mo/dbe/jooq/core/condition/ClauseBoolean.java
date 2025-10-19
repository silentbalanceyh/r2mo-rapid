package io.r2mo.dbe.jooq.core.condition;

import io.r2mo.base.dbe.syntax.QValue;
import io.r2mo.base.util.R2MO;
import org.jooq.Condition;
import org.jooq.Field;

/**
 * @author lang : 2025-10-19
 */
class ClauseBoolean extends ClauseString {
    @Override
    public Condition where(final Field<?> field, final QValue qValue) {
        Object waitFor = qValue.value();
        final Object value = qValue.value().getClass();
        if (R2MO.isBoolean(value)) {
            waitFor = ClauseFun.eachFn(waitFor, from -> Boolean.valueOf(from.toString()));
        }
        return super.where(field, QValue.of(qValue, waitFor));
    }
}

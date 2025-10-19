package io.r2mo.dbe.jooq.core.condition;

import io.r2mo.base.dbe.syntax.QValue;
import io.r2mo.base.util.R2MO;
import org.jooq.Condition;
import org.jooq.Field;

/**
 * @author lang : 2025-10-19
 */
class ClauseNumber extends ClauseString {

    @Override
    @SuppressWarnings("all")
    public Condition where(final Field<?> field, final QValue qValue) {
        final Object value = qValue.value();
        final Class<?> type = value.getClass();
        Object normalized = value;
        if (R2MO.isInteger(value)) {
            normalized = ClauseFun.eachFn(value, from -> {
                if (Long.class == type || long.class == type) {
                    return Long.valueOf(from.toString());
                } else if (Short.class == type || short.class == type) {
                    return Short.valueOf(from.toString());
                } else {
                    return Integer.valueOf(from.toString());
                }
            });
        }
        return super.where(field, QValue.copyOf(qValue, normalized));
    }
}

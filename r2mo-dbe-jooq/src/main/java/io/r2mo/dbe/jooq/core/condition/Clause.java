package io.r2mo.dbe.jooq.core.condition;

import io.r2mo.base.dbe.syntax.QValue;
import io.r2mo.typed.cc.Cc;
import org.jooq.Condition;
import org.jooq.Field;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * @author lang : 2025-10-19
 */
public interface Clause {

    Cc<String, Clause> CC_SKELETON = Cc.openThread();

    static Clause of(final QValue qValue) {
        if (Objects.isNull(qValue)) {
            return of(Object.class);
        }
        final Class<?> type = qValue.type();
        if (Objects.isNull(type)) {
            return of(Object.class);
        }
        return of(type);
    }

    static Clause of(final Class<?> type) {
        return CC_SKELETON.pick(() -> Optional.ofNullable(ClauseFun.CLAUSE_MAP.get(type))
            .map(Supplier::get)
            .orElseGet(ClauseString::new), type.getName());
    }

    Condition where(Field<?> field, QValue qValue);

    @SuppressWarnings("all") // 兼容遗留系统，此处的处理方式不能变更
    default Condition where(final Field columnName, final String fieldName, final String op, final Object value) {
        return this.where(columnName, QValue.of(fieldName, op, value));
    }
}

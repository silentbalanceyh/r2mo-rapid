package io.r2mo.dbe.jooq.core.condition;

import io.r2mo.base.dbe.syntax.QValue;
import io.r2mo.typed.cc.Cc;
import org.jooq.Condition;
import org.jooq.Field;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * @author lang : 2025-10-19
 */
public interface Clause {

    Cc<String, Clause> CC_SKELETON = Cc.openThread();

    static Clause of(final Class<?> type) {
        return CC_SKELETON.pick(() -> Optional.ofNullable(ClauseFun.CLAUSE_MAP.get(type))
            .map(Supplier::get)
            .orElseGet(ClauseString::new), type.getName());
    }

    Condition where(final Field<?> field, final QValue value);
}

package io.r2mo.dbe.jooq.core.condition;

import io.r2mo.base.dbe.constant.QOp;
import io.r2mo.base.dbe.syntax.QValue;
import org.jooq.Condition;
import org.jooq.Field;

import java.util.Optional;

/**
 * @author lang : 2025-10-19
 */
class ClauseString implements Clause {
    @Override
    public Condition where(final Field<?> field, final QValue value) {
        // 根据 QValue 的操作符和标记来生成相应的条件
        final QOp op = value.op();
        return Optional.ofNullable(op.value())
            .map(ClauseFun.NORM_MAP::get)
            .map(func -> func.apply(field, value))
            .orElse(null);
    }
}

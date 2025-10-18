package io.r2mo.dbe.jooq.spi;

import io.r2mo.base.dbe.operation.QrOne;
import io.r2mo.base.dbe.syntax.QTree;
import io.r2mo.dbe.common.operation.AbstractDbOperation;
import org.jooq.Condition;
import org.jooq.DSLContext;

import java.io.Serializable;
import java.util.Map;
import java.util.Optional;

/**
 * @author lang : 2025-10-18
 */
class QrOneJooq<T> extends AbstractDbOperation<Condition, T, DSLContext> implements QrOne<T> {
    QrOneJooq(final Class<T> entityCls, final DSLContext dslContext) {
        super(entityCls, dslContext);
    }

    @Override
    public Optional<T> execute(final QTree syntax) {
        return Optional.empty();
    }

    @Override
    public Optional<T> execute(final Serializable id) {
        return Optional.empty();
    }

    @Override
    public Optional<T> execute(final String field, final Object value) {
        return Optional.empty();
    }

    @Override
    public Optional<T> execute(final Map<String, Object> condition) {
        return Optional.empty();
    }
}

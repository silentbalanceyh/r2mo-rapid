package io.r2mo.dbe.jooq.spi;

import cn.hutool.core.util.StrUtil;
import io.r2mo.base.dbe.operation.QrOne;
import io.r2mo.base.dbe.syntax.QTree;
import org.jooq.DSLContext;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * @author lang : 2025-10-18
 */
class QrOneJooq<T> extends AbstractDbJooq<T> implements QrOne<T> {
    QrOneJooq(final Class<T> entityCls, final DSLContext dslContext) {
        super(entityCls, dslContext);
    }

    @Override
    public Optional<T> execute(final QTree syntax) {
        if (Objects.isNull(syntax) || !syntax.isOk()) {
            return Optional.empty();
        }


        return this.findOne(this.analyzer().where(syntax));
    }

    @Override
    public Optional<T> execute(final Serializable id) {
        if (Objects.isNull(id)) {
            return Optional.empty();
        }


        return this.findOne(this.setter.whereId(id));
    }

    @Override
    public Optional<T> execute(final String field, final Object value) {
        if (StrUtil.isEmpty(field)) {
            return Optional.empty();
        }
        
        return this.findOne(this.analyzer().where(field, value));
    }

    @Override
    public Optional<T> execute(final Map<String, Object> condition) {
        if (Objects.isNull(condition) || condition.isEmpty()) {
            return Optional.empty();
        }


        return this.findOne(this.analyzer().where(condition));
    }
}

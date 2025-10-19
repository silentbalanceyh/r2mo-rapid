package io.r2mo.dbe.jooq.spi;

import cn.hutool.core.util.StrUtil;
import io.r2mo.base.dbe.operation.QrMany;
import io.r2mo.base.dbe.syntax.QQuery;
import io.r2mo.base.dbe.syntax.QTree;
import org.jooq.DSLContext;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author lang : 2025-10-19
 */
class QrManyJooq<T> extends AbstractDbJooq<T> implements QrMany<T> {
    protected QrManyJooq(final Class<T> entityCls, final DSLContext context) {
        super(entityCls, context);
    }

    @Override
    public List<T> execute(final QQuery query) {
        if (Objects.isNull(query)) {
            return new ArrayList<>();
        }


        return this.findMany(this.analyzer().where(query));
    }

    @Override
    public List<T> execute(final QTree tree) {
        if (Objects.isNull(tree) || !tree.isOk()) {
            return new ArrayList<>();
        }


        return this.findMany(this.analyzer().where(tree));
    }

    @Override
    @SuppressWarnings("all")
    public List<T> execute(final Serializable... ids) {
        if (0 == ids.length) {
            return new ArrayList<>();
        }


        final String primaryKey = this.meta.keyPrimary();
        return this.findMany(this.analyzer().whereIn(primaryKey, ids));
    }

    @Override
    public List<T> execute(final String field, final Object value) {
        if (StrUtil.isEmpty(field)) {
            return new ArrayList<>();
        }


        return this.findMany(this.analyzer().where(field, value));
    }

    @Override
    public List<T> execute(final Map<String, Object> condition) {
        if (Objects.isNull(condition) || condition.isEmpty()) {
            return new ArrayList<>();
        }


        return this.findMany(this.analyzer().where(condition));
    }
}

package io.r2mo.dbe.jooq.spi;

import io.r2mo.base.dbe.common.DBRef;
import io.r2mo.base.dbe.operation.OpJoin;
import io.r2mo.base.dbe.syntax.QQuery;
import io.r2mo.typed.json.JArray;
import io.r2mo.typed.json.JObject;
import org.jooq.DSLContext;

import java.io.Serializable;
import java.util.Optional;

/**
 * @author lang : 2025-10-24
 */
class OpJoinJooq<T> implements OpJoin<T, DSLContext> {
    private final DSLContext context;
    private final DBRef ref;

    OpJoinJooq(final DBRef ref, final DSLContext context) {
        this.ref = ref;
        this.context = context;
    }

    @Override
    public JArray findMany(final DSLContext dslContext) {
        return null;
    }

    @Override
    public JObject findOne(final DSLContext dslContext) {
        return null;
    }

    @Override
    public JObject findPage(final QQuery query) {
        return null;
    }

    @Override
    public JObject findById(final Serializable id) {
        return null;
    }

    @Override
    public Optional<Long> count(final DSLContext dslContext) {
        return Optional.empty();
    }

    @Override
    public JObject create(final JObject latest) {
        return null;
    }

    @Override
    public Boolean removeById(final Serializable id) {
        return null;
    }

    @Override
    public Boolean removeBy(final DSLContext dslContext) {
        return null;
    }

    @Override
    public JObject updateById(final Serializable id, final JObject latest) {
        return null;
    }

    @Override
    public JObject update(final DSLContext dslContext, final JObject latest) {
        return null;
    }
}

package io.r2mo.dbe.jooq.spi;

import io.r2mo.base.dbe.common.DBRef;
import io.r2mo.base.dbe.operation.OpJoin;
import io.r2mo.base.dbe.syntax.QQuery;
import io.r2mo.spi.SPI;
import io.r2mo.typed.json.JArray;
import io.r2mo.typed.json.JBase;
import io.r2mo.typed.json.JObject;
import lombok.extern.slf4j.Slf4j;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SelectWhereStep;
import org.jooq.TableOnConditionStep;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

/**
 * @author lang : 2025-10-24
 */
@Slf4j
class OpJoinJooq<T> implements OpJoin<T, Condition> {
    private final DSLContext context;
    private final DBRef ref;

    OpJoinJooq(final DBRef ref, final DSLContext context) {
        this.ref = ref;
        this.context = context;
    }

    @Override
    public JArray findMany(final Condition condition) {
        final TableOnConditionStep<Record> joinOn = JoinQr.buildJoin(this.ref);

        log.debug("[ R2MO ] findMany 查询条件：{}, {}", joinOn, condition);
        final SelectWhereStep<Record> started = this.context.selectFrom(joinOn);

        final Result<Record> record = this.context.fetch(started.where(condition));
        final JArray array = SPI.A();
        record.stream()
            .map(JoinResult.of(this.ref)::toResponse)
            .map(JBase::data)
            .forEach(array::add);
        return array;
    }

    @Override
    public JObject findOne(final Condition condition) {
        final TableOnConditionStep<Record> joinOn = JoinQr.buildJoin(this.ref);
        log.debug("[ R2MO ] findOne 查询条件：{}, {}", joinOn, condition);

        final SelectWhereStep<Record> started = this.context.selectFrom(joinOn);

        final Record record = this.context.fetchOne(started.where(condition));
        if (Objects.isNull(record)) {
            return SPI.J();
        }
        return JoinResult.of(this.ref).toResponse(record);
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
    public Optional<Long> count(final Condition condition) {
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
    public Boolean removeBy(final Condition condition) {
        return null;
    }

    @Override
    public JObject updateById(final Serializable id, final JObject latest) {
        return null;
    }

    @Override
    public JObject update(final Condition condition, final JObject latest) {
        return null;
    }
}

package io.r2mo.dbe.jooq.spi;

import io.r2mo.dbe.common.operation.AbstractDbOperation;
import io.r2mo.dbe.jooq.core.domain.JooqMeta;
import io.r2mo.dbe.jooq.core.domain.JooqObject;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.ResultQuery;
import org.jooq.SelectConditionStep;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;


/**
 * @author lang : 2025-10-19
 */
class AbstractDbJooq<T> extends AbstractDbOperation<Condition, T, DSLContext> {
    protected final JooqMeta meta;
    protected final JooqObject setter;

    protected AbstractDbJooq(final Class<T> entityCls, final DSLContext context) {
        super(entityCls, context);
        this.meta = JooqMeta.getOr(entityCls);
        this.setter = new JooqObject(this.meta, context);
    }

    @SuppressWarnings("all")
    protected Optional<T> findOne(final Condition condition) {
        final SelectConditionStep<?> selectStep = this.executor().selectFrom(this.meta.table())
            .where(condition);
        final T queried = (T) ((ResultQuery) selectStep).fetchOneInto(this.meta.entityCls());
        this.log().info("[ R2MO ] ( Jooq ) 查询单条数据，条件 / {} ", condition);
        return Optional.ofNullable(queried);
    }

    @SuppressWarnings("all")
    protected List<T> findMany(final Condition condition) {
        final SelectConditionStep<?> selectStep = this.executor().selectFrom(this.meta.table())
            .where(condition);
        final List<T> list = (List<T>) ((ResultQuery) selectStep).fetchInto(this.meta.entityCls());
        this.log().info("[ R2MO ] ( Jooq ) 查询多条数据，条件 / {}, 结果数量：{}", condition, list.size());
        return list;
    }

    protected Logger log() {
        return LoggerFactory.getLogger(this.getClass());
    }
}

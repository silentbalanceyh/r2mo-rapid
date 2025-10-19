package io.r2mo.dbe.jooq.spi;

import io.r2mo.base.dbe.operation.OpVary;
import io.r2mo.base.dbe.syntax.QQuery;
import io.r2mo.typed.common.Pagination;
import lombok.extern.slf4j.Slf4j;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.ResultQuery;
import org.jooq.SelectWhereStep;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @author lang : 2025-10-19
 */
@Slf4j
class OpVaryJooq<T> extends AbstractDbJooq<T> implements OpVary<T, Condition> {
    private final QrAnalyzerJooq jqAnalyzer;
    private final OpDbJooq<T> db;

    protected OpVaryJooq(final Class<T> entityCls, final DSLContext context) {
        super(entityCls, context);

        this.jqAnalyzer = new QrAnalyzerJooq(entityCls, context);
        this.db = new OpDbJooq<>(entityCls, context);
    }

    @Override
    public Pagination<T> findPage(final QQuery query) {
        if (Objects.isNull(query)) {
            return new Pagination<>();
        }

        return this.jqAnalyzer.page(query);
    }

    @Override
    public List<T> findMany(final Condition condition) {
        if (Objects.isNull(condition)) {
            return new ArrayList<>();
        }
        return this.findMany(condition);
    }

    @Override
    public boolean removeBy(final Condition condition) {
        if (Objects.isNull(condition)) {
            return false;
        }
        final int rows = this.executor().delete(this.meta.table()).where(condition).execute();
        log.info("[ R2MO ] ( Jooq ) 条件删除 {}，影响行数 / {}", condition, rows);
        return rows > 0;
    }

    @Override
    @SuppressWarnings("all")
    public List<T> findAll() {
        final SelectWhereStep<?> selectStep = this.executor().selectFrom(this.meta.table());
        final List<T> list = ((ResultQuery) selectStep).fetchInto(this.meta.entityCls());
        log.info("[ R2MO ] ( Jooq ) 读取数据: {}", list.size());
        return list;
    }

    @Override
    public Optional<T> findOne(final Condition condition) {
        if (Objects.isNull(condition)) {
            return Optional.empty();
        }
        return this.findOne(condition);
    }

    @Override
    public boolean removeById(final Serializable id) {
        if (Objects.isNull(id)) {
            return false;
        }

        return this.removeBy(this.setter.whereId(id));
    }

    @Override
    public T save(final Optional<T> queried, final T latest) {
        if (queried.isPresent()) {
            // UPDATE
            return this.update(queried, latest);
        } else {
            // INSERT
            this.db.insert(latest);
            return latest;
        }
    }

    @Override
    public T update(final Optional<T> queried, final T latest) {
        if (queried.isPresent()) {
            // UPDATE 更新数据
            final T waiting = queried.get();
            this.setter.copyFrom(waiting, latest);
            this.db.update(waiting);
            return waiting;
        }
        return null;
    }
}

package io.r2mo.vertx.jooq;

import io.r2mo.base.dbe.operation.QrAnalyzer;
import io.r2mo.dbe.jooq.DBE;
import io.r2mo.dbe.jooq.core.domain.JooqMeta;
import io.r2mo.dbe.jooq.core.domain.JooqObject;
import io.r2mo.dbe.jooq.spi.QrAnalyzerCondition;
import io.r2mo.vertx.jooq.classic.VertxDAO;
import io.vertx.core.Future;
import org.jooq.Condition;

import java.util.List;
import java.util.Objects;

/**
 * @author lang : 2025-10-19
 */
@SuppressWarnings("all")
class AsyncDBEAction<T> {
    protected final AsyncMeta metaAsync;
    protected final JooqMeta meta;
    protected final JooqObject setter;
    protected final DBE<T> dbe;

    private final QrAnalyzer<Condition> analyzer;
    private final Class<T> entityCls;
    private final VertxDAO executor;

    protected AsyncDBEAction(Class<T> entityCls, VertxDAO vertxDAO) {
        this.executor = vertxDAO;
        this.entityCls = entityCls;


        final AsyncMeta meta = AsyncMeta.getOr(entityCls);
        Objects.requireNonNull(meta, "[ R2MO ] 无法从实体类中提取元数据：" + entityCls.getName());
        this.setter = new JooqObject(meta.metaJooq(), meta.context());
        this.dbe = DBE.<T>of((Class<T>) meta.metaEntity(), meta.context());


        this.meta = meta.metaJooq();
        this.metaAsync = meta;
        this.analyzer = new QrAnalyzerCondition(entityCls, meta.context());
    }

    protected VertxDAO executor() {
        return this.executor;
    }

    protected QrAnalyzer<Condition> analyzer() {
        return this.analyzer;
    }

    protected Future<T> findOneAsync(final Condition condition) {
        if (Objects.isNull(condition)) {
            return Future.succeededFuture();
        }
        return (Future<T>) this.executor().findOneByCondition(condition);
    }

    protected Future<List<T>> findManyAsync(final Condition condition) {
        if (Objects.isNull(condition)) {
            return Future.succeededFuture();
        }
        return (Future<List<T>>) this.executor().findManyByCondition(condition);
    }
}

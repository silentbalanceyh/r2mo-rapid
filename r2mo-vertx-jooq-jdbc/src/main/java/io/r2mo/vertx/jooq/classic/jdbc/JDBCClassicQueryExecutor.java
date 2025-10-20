package io.r2mo.vertx.jooq.classic.jdbc;

import io.r2mo.vertx.jooq.shared.internal.QueryExecutor;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.InsertResultStep;
import org.jooq.ResultQuery;
import org.jooq.UpdatableRecord;

import java.util.List;
import java.util.function.Function;

/**
 * Created by jensklingsporn on 20.12.17.
 */
public class JDBCClassicQueryExecutor<R extends UpdatableRecord<R>, P, T> extends JDBCClassicGenericQueryExecutor implements QueryExecutor<R, T, Future<List<P>>, Future<P>, Future<Integer>, Future<T>> {

    private final Class<P> daoType;

    public JDBCClassicQueryExecutor(final Configuration configuration, final Class<P> daoType, final Vertx vertx) {
        super(configuration, vertx);
        this.daoType = daoType;
    }

    @Override
    public Future<List<P>> findMany(final Function<DSLContext, ? extends ResultQuery<R>> queryFunction) {
        return this.executeBlocking(h -> h.complete(this.createQuery(queryFunction).fetchInto(this.daoType)));
    }

    @Override
    public Future<P> findOne(final Function<DSLContext, ? extends ResultQuery<R>> queryFunction) {
        return this.executeBlocking(h -> h.complete(this.createQuery(queryFunction).fetchOneInto(this.daoType)));
    }

    @Override
    public Future<T> insertReturning(final Function<DSLContext, ? extends InsertResultStep<R>> queryFunction, final Function<Object, T> keyMapper) {
        return this.executeBlocking(h -> h.complete(keyMapper.apply(this.createQuery(queryFunction).fetchOne())));
    }


}

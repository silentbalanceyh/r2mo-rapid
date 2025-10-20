package io.r2mo.vertx.jooq.classic.jdbc;

import io.r2mo.vertx.jooq.classic.ClassicQueryExecutor;
import io.r2mo.vertx.jooq.shared.internal.AbstractQueryExecutor;
import io.r2mo.vertx.jooq.shared.internal.QueryResult;
import io.r2mo.vertx.jooq.shared.internal.jdbc.JDBCQueryExecutor;
import io.r2mo.vertx.jooq.shared.internal.jdbc.JDBCQueryResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.impl.future.PromiseImpl;
import io.vertx.core.internal.ContextInternal;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.Query;
import org.jooq.Record;
import org.jooq.ResultQuery;
import org.jooq.impl.DSL;

import java.util.concurrent.Callable;
import java.util.function.Function;

/**
 * Created by jensklingsporn on 05.02.18.
 */
public class JDBCClassicGenericQueryExecutor extends AbstractQueryExecutor implements JDBCQueryExecutor<Future<?>>, ClassicQueryExecutor {

    protected final Vertx vertx;

    public JDBCClassicGenericQueryExecutor(final Configuration configuration, final Vertx vertx) {
        super(configuration);
        this.vertx = vertx;
    }

    @Override
    public <X> Future<X> executeAny(final Function<DSLContext, X> function) {
        return this.executeBlocking(h -> h.complete(function.apply(DSL.using(this.configuration()))));
    }

    protected <X> Future<X> executeBlocking(final Handler<Promise<X>> blockingCodeHandler) {
        final Promise<X> promise = new PromiseImpl<>((ContextInternal) this.vertx.getOrCreateContext());
        final Callable<X> callable = () -> {
            try {
                blockingCodeHandler.handle(promise);
                // 这里不能直接返回结果，因为用户是异步 complete()
                // 所以返回 null 交给 promise.future() 处理
                return null;
            } catch (final Throwable e) {
                promise.fail(e);
                return null;
            }
        };
        this.vertx.executeBlocking(callable, false).onComplete(ar -> {
            // 如果用户在 handler 里 complete/fail 了，这里就不用管
            // 如果用户什么都没做，就把 Vert.x 的结果传回
            if (ar.succeeded() && !promise.future().isComplete()) {
                promise.complete(ar.result());
            } else if (ar.failed() && !promise.future().isComplete()) {
                promise.fail(ar.cause());
            }
        });
        return promise.future();
    }

    @Override
    public Future<Integer> execute(final Function<DSLContext, ? extends Query> queryFunction) {
        return this.executeBlocking(h -> h.complete(this.createQuery(queryFunction).execute()));
    }

    @Override
    public <R extends Record> Future<QueryResult> query(final Function<DSLContext, ? extends ResultQuery<R>> queryFunction) {
        return this.executeBlocking(h -> h.complete(new JDBCQueryResult(this.createQuery(queryFunction).fetch())));
    }
}

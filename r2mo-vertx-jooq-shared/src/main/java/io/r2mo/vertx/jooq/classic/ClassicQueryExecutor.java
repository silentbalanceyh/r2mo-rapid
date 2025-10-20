package io.r2mo.vertx.jooq.classic;

import io.r2mo.vertx.jooq.shared.internal.QueryResult;
import io.r2mo.vertx.jooq.shared.internal.UnifiedQueryExecutor;
import io.vertx.core.Future;

/**
 * A {@code UnifiedQueryExecutor} using the {@code Future}-based API.
 */
public interface ClassicQueryExecutor extends UnifiedQueryExecutor<Future<Integer>, Future<QueryResult>> {


}

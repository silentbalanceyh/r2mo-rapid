package io.r2mo.vertx.dbe;

import io.r2mo.base.dbe.constant.OpType;
import io.vertx.core.Future;

import java.util.List;

/**
 * @author lang : 2025-08-28
 */
public interface AsyncDb<T> {

    Future<T> executeAsync(T entity, OpType opType);

    Future<List<T>> executeAsync(List<T> entities, OpType opType);
}

package io.r2mo.vertx.dbe;

import io.r2mo.base.dbe.syntax.QTree;
import io.vertx.core.Future;

import java.io.Serializable;
import java.util.Map;

/**
 * @author lang : 2025-08-28
 */
public interface AsyncOne<T> {

    Future<T> execute(QTree syntax);

    Future<T> execute(Serializable id);

    Future<T> execute(String field, Object value);

    Future<T> execute(Map<String, Object> condition);
}

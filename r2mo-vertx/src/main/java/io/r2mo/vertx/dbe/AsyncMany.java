package io.r2mo.vertx.dbe;

import io.r2mo.base.dbe.syntax.QQuery;
import io.r2mo.base.dbe.syntax.QTree;
import io.vertx.core.Future;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author lang : 2025-08-28
 */
public interface AsyncMany<T> {

    Future<List<T>> executeAsync(QQuery query);

    Future<List<T>> executeAsync(QTree tree);

    Future<List<T>> executeAsync(Serializable... ids);

    Future<List<T>> executeAsync(String field, Object value);

    Future<List<T>> executeAsync(Map<String, Object> condition);
}

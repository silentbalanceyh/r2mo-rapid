package io.r2mo.vertx.dbe;

import io.r2mo.base.dbe.syntax.QQuery;
import io.r2mo.typed.common.Pagination;
import io.vertx.core.Future;

import java.io.Serializable;
import java.util.List;

/**
 * @author lang : 2025-08-28
 */
public interface AsyncVary<T, CONDITION> {
    /**
     * 分页查询
     *
     * @param query 条件、列过滤、排序、分页
     *
     * @return 分页结果
     */
    Future<Pagination<T>> findPageAsync(QQuery query);

    Future<Boolean> removeByAsync(CONDITION condition);

    Future<List<T>> findManyAsync(CONDITION condition);

    Future<List<T>> findAllAsync();

    Future<T> findOneAsync(CONDITION condition);

    Future<Boolean> removeByIdAsync(Serializable id);

    Future<T> saveAsync(Future<T> queried, T latest);

    Future<T> updateAsync(Future<T> queried, T latest);
}

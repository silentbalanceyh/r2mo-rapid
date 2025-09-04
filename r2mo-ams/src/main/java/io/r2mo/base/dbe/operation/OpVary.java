package io.r2mo.base.dbe.operation;

import io.r2mo.base.dbe.syntax.QQuery;
import io.r2mo.typed.common.Pagination;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author lang : 2025-08-28
 */
public interface OpVary<T, CONDITION> {
    /**
     * 分页查询
     *
     * @param query 条件、列过滤、排序、分页
     *
     * @return 分页结果
     */
    Pagination<T> findPage(QQuery query);

    boolean removeBy(CONDITION condition);

    List<T> findMany(CONDITION condition);

    List<T> findAll();

    Optional<T> findOne(CONDITION condition);

    Optional<T> findOne(Map<String, Object> condition);

    boolean removeById(Serializable id);

    @SuppressWarnings("all")
    T save(Optional<T> queried, T latest);

    @SuppressWarnings("all")
    T update(Optional<T> queried, T latest);
}

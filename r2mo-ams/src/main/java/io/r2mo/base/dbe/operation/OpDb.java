package io.r2mo.base.dbe.operation;

import io.r2mo.base.dbe.constant.OpType;

import java.util.List;

/**
 * @author lang : 2025-08-28
 */
public interface OpDb<T> {

    T execute(T entity, OpType opType);

    List<T> execute(List<T> entities, OpType opType, int batchSize);
}

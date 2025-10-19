package io.r2mo.vertx.common.mapping;

import io.r2mo.base.program.R2Vector;

/**
 * @author lang : 2025-10-19
 */
public abstract class PolyBase<T, C> implements Poly<T, C> {
    protected final Class<?> entityCls;
    protected final R2Vector vector;

    protected PolyBase(final R2Vector vector, final Class<?> entityCls) {
        this.vector = vector;
        this.entityCls = entityCls;
    }
}

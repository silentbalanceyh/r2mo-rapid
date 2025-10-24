package io.r2mo.dbe.jooq.spi;

import io.r2mo.base.dbe.common.DBLoad;
import io.r2mo.base.dbe.common.DBNode;
import io.r2mo.base.program.R2Vector;

/**
 * @author lang : 2025-10-24
 */
public class LoadJooq implements DBLoad {
    @Override
    public DBNode configure(final Class<?> entity, final R2Vector vector) {
        return null;
    }
}

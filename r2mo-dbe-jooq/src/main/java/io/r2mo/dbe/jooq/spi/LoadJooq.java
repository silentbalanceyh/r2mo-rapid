package io.r2mo.dbe.jooq.spi;

import io.r2mo.base.dbe.DBS;
import io.r2mo.base.dbe.common.DBLoadBase;
import io.r2mo.base.dbe.common.DBNode;
import io.r2mo.base.program.R2Vector;

/**
 *
 * @author lang : 2025-10-24
 */
public class LoadJooq extends DBLoadBase {

    @Override
    protected void setupTable(final DBNode node, final Class<?> entity) {

    }

    @Override
    public DBNode configure(final Class<?> daoCls, final R2Vector vector, final DBS dbs) {
        final Class<?> entityCls = LoadREF.of().loadEntity(daoCls);
        return null;
    }
}

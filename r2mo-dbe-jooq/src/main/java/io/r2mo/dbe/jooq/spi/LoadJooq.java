package io.r2mo.dbe.jooq.spi;

import io.r2mo.base.dbe.DBS;
import io.r2mo.base.dbe.common.DBLoadBase;
import io.r2mo.base.dbe.common.DBNode;
import io.r2mo.base.program.R2Vector;

/**
 * 加载器，Jooq 兼容的加载器部分，之后会带上一个附加的信息，此处主要还是 {@link DBNode} 替代
 *
 * @author lang : 2025-10-24
 */
public class LoadJooq extends DBLoadBase {

    @Override
    protected void setupTable(DBNode node, Class<?> entity) {

    }

    @Override
    public DBNode configure(final Class<?> entity, final R2Vector vector, final DBS dbs) {
        return null;
    }
}

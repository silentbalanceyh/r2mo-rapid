package io.r2mo.dbe.jooq.spi;

import io.r2mo.base.dbe.common.DBLoadBase;
import io.r2mo.base.dbe.common.DBNode;

/**
 * 加载器，Jooq 兼容的加载器部分，之后会带上一个附加的信息，此处主要还是 {@link DBNode} 替代
 *
 * @author lang : 2025-10-24
 */
public class LoadJooq extends DBLoadBase {
    @Override
    protected void setupTable(final DBNode node, final Class<?> daoCls) {
        
    }
}

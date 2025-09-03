package io.r2mo.base.dbe.syntax;

import io.r2mo.spi.SPI;
import io.r2mo.typed.json.JObject;

/**
 * @author lang : 2025-08-28
 */
public interface QQuery extends SPI {
    static QQuery of(final JObject queryJ) {
        return new QQueryImpl(queryJ);
    }

    QPager pager();

    QProjection projection();

    QSorter sorter();

    QTree criteria();
}

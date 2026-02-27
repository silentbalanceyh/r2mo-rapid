package io.r2mo.base.dbe.syntax;

import io.r2mo.base.dbe.constant.QCV;
import io.r2mo.spi.SPI;
import io.r2mo.typed.json.JObject;
import io.r2mo.typed.json.JUtil;

import java.util.Objects;

/**
 *
 *
 * @author lang : 2025-08-28
 */
class QQueryImpl implements QQuery {
    private static final JUtil UT = SPI.V_UTIL;

    private final QPager pager;
    private final QProjection projection;
    private final QSorter sorter;
    private final QTree criteria;

    QQueryImpl(final JObject queryJ) {
        Objects.requireNonNull(queryJ, "[ R2MO ] 参数 queryJ 不能为空");
        final JObject pageJ = UT.valueJObject(queryJ, QCV.P_PAGER);
        // FIX: 剔除分页参数相关信息，若没有传分页参数则直接不带默认分页，直接构造 null
        this.pager = UT.isEmpty(pageJ) ? null : QPager.of(pageJ);
        this.sorter = QSorter.of(UT.valueJArray(queryJ, QCV.P_SORTER));
        this.projection = QProjection.of(UT.valueJArray(queryJ, QCV.P_PROJECTION));
        this.criteria = QTree.of(UT.valueJObject(queryJ, QCV.P_CRITERIA));
    }

    @Override
    public QPager pager() {
        return this.pager;
    }

    @Override
    public QProjection projection() {
        return this.projection;
    }

    @Override
    public QSorter sorter() {
        return this.sorter;
    }

    @Override
    public QTree criteria() {
        return this.criteria;
    }
}

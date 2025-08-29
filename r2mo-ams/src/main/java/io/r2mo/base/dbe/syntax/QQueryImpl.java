package io.r2mo.base.dbe.syntax;

import io.r2mo.base.dbe.constant.QCV;
import io.r2mo.typed.json.JObject;

import java.util.Objects;

/**
 * @author lang : 2025-08-28
 */
class QQueryImpl implements QQuery {

    private final QPager pager;
    private final QProjection projection;
    private final QSorter sorter;
    private final QTree criteria;

    QQueryImpl(final JObject queryJ) {
        Objects.requireNonNull(queryJ, "[ R2MO ] 参数 queryJ 不能为空");
        this.pager = QPager.of(_UTJ.valueJObject(queryJ, QCV.P_PAGER));
        this.sorter = QSorter.of(_UTJ.valueJArray(queryJ, QCV.P_SORTER));
        this.projection = QProjection.of(_UTJ.valueJArray(queryJ, QCV.P_PROJECTION));
        this.criteria = QTree.of(_UTJ.valueJObject(queryJ, QCV.P_CRITERIA));
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

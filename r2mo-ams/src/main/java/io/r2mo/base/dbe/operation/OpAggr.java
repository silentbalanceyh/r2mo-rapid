package io.r2mo.base.dbe.operation;

import io.r2mo.base.dbe.constant.QCV;
import io.r2mo.base.dbe.syntax.QTree;

import java.util.Optional;

/**
 * @author lang : 2025-08-28
 */
public interface OpAggr {

    <N extends Number> Optional<N> execute(String aggrField, Class<N> returnCls, QCV.Aggr aggr,
                                           String field, Object value);

    <N extends Number> Optional<N> execute(String aggrField, Class<N> returnCls, QCV.Aggr aggr,
                                           QTree criteria);
}

package io.r2mo.vertx.dbe;

import io.r2mo.base.dbe.constant.QCV;
import io.r2mo.base.dbe.syntax.QTree;
import io.vertx.core.Future;

import java.util.Map;

/**
 * @author lang : 2025-10-18
 */
public interface AsyncAggr {

    <N extends Number> Future<N> executeAsync(String aggrField, Class<N> returnCls, QCV.Aggr aggr,
                                              String field, Object value);

    <N extends Number> Future<N> executeAsync(String aggrField, Class<N> returnCls, QCV.Aggr aggr,
                                              QTree criteria);

    <N extends Number> Future<N> executeAsync(String aggrField, Class<N> returnCls, QCV.Aggr aggr,
                                              Map<String, Object> map);
}

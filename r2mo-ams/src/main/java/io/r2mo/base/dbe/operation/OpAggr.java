package io.r2mo.base.dbe.operation;

import io.r2mo.base.dbe.constant.QCV;
import io.r2mo.base.dbe.syntax.QTree;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentMap;

/**
 * @author lang : 2025-08-28
 */
public interface OpAggr {

    <N extends Number> Optional<N> execute(
        String aggrField, Class<N> returnCls, QCV.Aggr aggr, String field, Object value);

    <N extends Number> Optional<N> execute(
        String aggrField, Class<N> returnCls, QCV.Aggr aggr, QTree criteria);

    <N extends Number> Optional<N> execute(
        String aggrField, Class<N> returnCls, QCV.Aggr aggr, Map<String, Object> map);

    <N extends Number> ConcurrentMap<String, N> execute(
        String aggrField, Class<N> returnCls, QCV.Aggr aggr, String field, Object value, String groupBy);

    <N extends Number> ConcurrentMap<String, N> execute(
        String aggrField, Class<N> returnCls, QCV.Aggr aggr, QTree criteria, String groupBy);

    <N extends Number> ConcurrentMap<String, N> execute(
        String aggrField, Class<N> returnCls, QCV.Aggr aggr, Map<String, Object> map, String groupBy);
}

package io.r2mo.dbe.mybatisplus.spi;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.r2mo.base.dbe.constant.QCV;
import io.r2mo.base.dbe.operation.OpAggr;
import io.r2mo.base.dbe.syntax.QTree;
import io.r2mo.dbe.common.operation.AbstractDbOperation;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * 聚集函数操作
 *
 * @author lang : 2025-08-28
 */
class OpAggrImpl<T, M extends BaseMapper<T>> extends AbstractDbOperation<QueryWrapper<T>, T, M> implements OpAggr {
    OpAggrImpl(final Class<T> entityCls, final M mapper) {
        super(entityCls, mapper);
    }

    @Override
    public <N extends Number> Optional<N> execute(final String aggrField, final Class<N> returnCls,
                                                  final QCV.Aggr aggr,
                                                  final String field, final Object value) {
        // Execute
        final QueryWrapper<T> query = this.analyzer().where(field, value);

        return this.execute(aggrField, returnCls, query, aggr);
    }


    @Override
    public <N extends Number> Optional<N> execute(final String aggrField, final Class<N> returnCls,
                                                  final QCV.Aggr aggr,
                                                  final QTree criteria) {

        // Execute
        final QueryWrapper<T> query = this.analyzer().where(criteria);

        return this.execute(aggrField, returnCls, query, aggr);
    }

    @Override
    public <N extends Number> Optional<N> execute(final String aggrField, final Class<N> returnCls,
                                                  final QCV.Aggr aggr,
                                                  final Map<String, Object> map) {
        // Execute
        final QueryWrapper<T> query = this.analyzer().where(map);

        return this.execute(aggrField, returnCls, query, aggr);
    }

    private <N extends Number> Optional<N> execute(
        final String aggrField, final Class<N> returnCls, final QueryWrapper<T> query, final QCV.Aggr aggr
    ) {
        if (Objects.isNull(aggr)) {
            return Optional.empty();
        }

        if (QCV.Aggr.COUNT == aggr) {
            // COUNT 特殊处理
            return this.buildResult(this.executor().selectCount(query), returnCls);
        } else {
            // aggrField
            Objects.requireNonNull(aggrField);
            final String fieldSelect = this.buildAggr(aggrField, aggr);
            query.select(fieldSelect);

            // 结果集
            final List<Object> retultList = this.executor().selectObjs(query);
            if (retultList.isEmpty()) {
                return Optional.empty();
            }
            final Object ret = retultList.get(0);
            return this.buildResult(ret, returnCls);
        }
    }

    @SuppressWarnings("all")
    private <N extends Number> Optional<N> buildResult(final Object ret, final Class<N> targetClass) {
        if (Objects.isNull(ret)) {
            return Optional.empty();
        }
        // 特殊情况
        if (ret instanceof final Long retLong && Long.class == targetClass) {
            return Optional.of((N) retLong);
        }
        // 其他情况
        final String str = ret.toString();
        final N retN;
        if (targetClass == Byte.class || targetClass == byte.class) {
            retN = (N) Byte.valueOf(str);
        } else if (targetClass == Short.class || targetClass == short.class) {
            retN = (N) Short.valueOf(str);
        } else if (targetClass == Integer.class || targetClass == int.class) {
            retN = (N) Integer.valueOf(str);
        } else if (targetClass == Long.class || targetClass == long.class) {
            retN = (N) Long.valueOf(str);
        } else if (targetClass == Float.class || targetClass == float.class) {
            retN = (N) Float.valueOf(str);
        } else if (targetClass == Double.class || targetClass == double.class) {
            retN = (N) Double.valueOf(str);
        } else if (targetClass == java.math.BigDecimal.class) {
            retN = (N) new java.math.BigDecimal(str);
        } else if (targetClass == java.math.BigInteger.class) {
            retN = (N) new java.math.BigInteger(str);
        } else {
            retN = null;
        }
        return Optional.ofNullable(retN);
    }

    private String buildAggr(final String fieldAggr, final QCV.Aggr aggr) {
        return switch (aggr) {
            case COUNT -> "COUNT(" + fieldAggr + ")";
            case SUM -> "SUM(" + fieldAggr + ")";
            case AVG -> "AVG(" + fieldAggr + ")";
            case MAX -> "MAX(" + fieldAggr + ")";
            case MIN -> "MIN(" + fieldAggr + ")";
        };
    }
}

package io.r2mo.vertx.jooq;

import io.r2mo.base.dbe.constant.QCV;
import io.r2mo.base.dbe.syntax.QTree;
import io.r2mo.vertx.dbe.AsyncAggr;
import io.r2mo.vertx.jooq.classic.VertxDAO;
import io.vertx.core.Future;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

/**
 * 现阶段版本直接桥接处理，访问同步的聚集函数
 *
 * @author lang : 2025-10-19
 */
@SuppressWarnings("all")
class AsyncAggrJooq<T> extends AsyncDBEAction<T> implements AsyncAggr {
    @SuppressWarnings("all")
    protected AsyncAggrJooq(final Class<T> entityCls, final VertxDAO vertxDAO) {
        super(entityCls, vertxDAO);
    }

    @Override
    public <N extends Number> Future<N> executeAsync(final String aggrField, final Class<N> returnCls,
                                                     final QCV.Aggr aggr, final String field, final Object value) {
        return Future.succeededFuture(switch (aggr) {
            case COUNT -> this.coerceLong(this.dbe.count(field, value).orElse(-1L), returnCls);
            case AVG -> this.coerceBigDecimal(this.dbe.avg(aggrField, field, value).orElse(BigDecimal.ZERO), returnCls);
            case SUM -> this.coerceBigDecimal(this.dbe.sum(aggrField, field, value).orElse(BigDecimal.ZERO), returnCls);
            case MAX -> this.coerceBigDecimal(this.dbe.max(aggrField, field, value).orElse(BigDecimal.ZERO), returnCls);
            case MIN -> this.coerceBigDecimal(this.dbe.min(aggrField, field, value).orElse(BigDecimal.ZERO), returnCls);
        });
    }

    @Override
    public <N extends Number> Future<N> executeAsync(final String aggrField, final Class<N> returnCls,
                                                     final QCV.Aggr aggr, final QTree criteria) {
        return Future.succeededFuture(switch (aggr) {
            case COUNT -> this.coerceLong(this.dbe.count(criteria).orElse(-1L), returnCls);
            case AVG -> this.coerceBigDecimal(this.dbe.avg(aggrField, criteria).orElse(BigDecimal.ZERO), returnCls);
            case SUM -> this.coerceBigDecimal(this.dbe.sum(aggrField, criteria).orElse(BigDecimal.ZERO), returnCls);
            case MAX -> this.coerceBigDecimal(this.dbe.max(aggrField, criteria).orElse(BigDecimal.ZERO), returnCls);
            case MIN -> this.coerceBigDecimal(this.dbe.min(aggrField, criteria).orElse(BigDecimal.ZERO), returnCls);
        });
    }

    @Override
    public <N extends Number> Future<N> executeAsync(final String aggrField, final Class<N> returnCls,
                                                     final QCV.Aggr aggr, final Map<String, Object> map) {
        return Future.succeededFuture(switch (aggr) {
            case COUNT -> this.coerceLong(this.dbe.count(map).orElse(-1L), returnCls);
            case AVG -> this.coerceBigDecimal(this.dbe.avg(aggrField, map).orElse(BigDecimal.ZERO), returnCls);
            case SUM -> this.coerceBigDecimal(this.dbe.sum(aggrField, map).orElse(BigDecimal.ZERO), returnCls);
            case MAX -> this.coerceBigDecimal(this.dbe.max(aggrField, map).orElse(BigDecimal.ZERO), returnCls);
            case MIN -> this.coerceBigDecimal(this.dbe.min(aggrField, map).orElse(BigDecimal.ZERO), returnCls);
        });
    }

    @Override
    public <N extends Number> Future<ConcurrentMap<String, N>> executeAsync(
        final String aggrField, final Class<N> returnCls, final QCV.Aggr aggr,
        final String field, final Object value, final String groupBy) {
        return Future.succeededFuture(
            switch (aggr) {
                case COUNT -> mapLong(this.dbe.countBy(field, value, groupBy), returnCls);
                case SUM -> mapDecimal(this.dbe.sumBy(aggrField, field, value, groupBy), returnCls);
                case AVG -> mapDecimal(this.dbe.avgBy(aggrField, field, value, groupBy), returnCls);
                case MAX -> mapDecimal(this.dbe.maxBy(aggrField, field, value, groupBy), returnCls);
                case MIN -> mapDecimal(this.dbe.minBy(aggrField, field, value, groupBy), returnCls);
            }
        );
    }

    @Override
    public <N extends Number> Future<ConcurrentMap<String, N>> executeAsync(
        final String aggrField, final Class<N> returnCls, final QCV.Aggr aggr,
        final QTree criteria, final String groupBy) {
        return Future.succeededFuture(
            switch (aggr) {
                case COUNT -> mapLong(this.dbe.countBy(criteria, groupBy), returnCls);
                case SUM -> mapDecimal(this.dbe.sumBy(aggrField, criteria, groupBy), returnCls);
                case AVG -> mapDecimal(this.dbe.avgBy(aggrField, criteria, groupBy), returnCls);
                case MAX -> mapDecimal(this.dbe.maxBy(aggrField, criteria, groupBy), returnCls);
                case MIN -> mapDecimal(this.dbe.minBy(aggrField, criteria, groupBy), returnCls);
            }
        );
    }

    @Override
    public <N extends Number> Future<ConcurrentMap<String, N>> executeAsync(
        final String aggrField, final Class<N> returnCls, final QCV.Aggr aggr,
        final Map<String, Object> map, final String groupBy) {
        return Future.succeededFuture(
            switch (aggr) {
                case COUNT -> mapLong(this.dbe.countBy(map, groupBy), returnCls);
                case SUM -> mapDecimal(this.dbe.sumBy(aggrField, map, groupBy), returnCls);
                case AVG -> mapDecimal(this.dbe.avgBy(aggrField, map, groupBy), returnCls);
                case MAX -> mapDecimal(this.dbe.maxBy(aggrField, map, groupBy), returnCls);
                case MIN -> mapDecimal(this.dbe.minBy(aggrField, map, groupBy), returnCls);
            }
        );
    }


    private <N extends Number> ConcurrentMap<String, N> mapLong(
        final ConcurrentMap<String, Long> src, final Class<N> returnCls) {
        final ConcurrentMap<String, N> out =
            new java.util.concurrent.ConcurrentHashMap<>(src == null ? 16 : Math.max(16, src.size() * 2));
        if (src != null) {
            src.forEach((k, v) -> out.put(k, this.coerceLong(v, returnCls)));
        }
        return out;
    }

    private <N extends Number> ConcurrentMap<String, N> mapDecimal(
        final ConcurrentMap<String, BigDecimal> src, final Class<N> returnCls) {
        final ConcurrentMap<String, N> out =
            new java.util.concurrent.ConcurrentHashMap<>(src == null ? 16 : Math.max(16, src.size() * 2));
        if (src != null) {
            src.forEach((k, v) -> out.put(k, this.coerceBigDecimal(v, returnCls)));
        }
        return out;
    }

    /** Long -> N（泛型返回） */
    private <N extends Number> N coerceLong(final Long value, final Class<N> target) {
        return this.coerce(value, target);
    }

    /** BigDecimal -> N（泛型返回） */
    private <N extends Number> N coerceBigDecimal(final BigDecimal value, final Class<N> target) {
        return this.coerce(value, target);
    }

    @SuppressWarnings("unchecked")
    private <N extends Number> N coerce(final Number value, final Class<N> target) {
        if (value == null || target == null) {
            throw new NullPointerException("[ R2MO ] 参数 value/target 不能为空。");
        }
        if (!(value instanceof Long || value instanceof BigDecimal)) {
            throw new IllegalArgumentException("[ R2MO ] 仅支持 BigDecimal 或 Long 作为输入，实际为: " + value.getClass());
        }
        if (target.isInstance(value)) {
            return (N) value; // 同类型直接返回
        }

        // 目标：BigDecimal
        if (target == BigDecimal.class) {
            if (value instanceof final BigDecimal bd) {
                return (N) bd;
            }
            return (N) BigDecimal.valueOf((Long) value);
        }

        // 目标：BigInteger（要求整数且不越界）
        if (target == BigInteger.class) {
            if (value instanceof final BigDecimal bd) {
                try {
                    return (N) bd.toBigIntegerExact();
                } catch (final ArithmeticException e) {
                    throw new ArithmeticException("[ R2MO ] 非整数值，无法精确转换为 BigInteger: " + bd);
                }
            } else { // Long
                return (N) BigInteger.valueOf((Long) value);
            }
        }

        // 目标：Long（要求整数且不越界）
        if (target == Long.class) {
            if (value instanceof final BigDecimal bd) {
                try {
                    return (N) Long.valueOf(bd.longValueExact());
                } catch (final ArithmeticException e) {
                    throw new ArithmeticException("[ R2MO ] 非整数或超出 long 可表示范围: " + bd);
                }
            } else { // Long -> Long
                return (N) value;
            }
        }

        // 目标：Integer / Short / Byte（要求整数且不越界）
        if (target == Integer.class || target == Short.class || target == Byte.class) {
            final long l;
            if (value instanceof final BigDecimal bd) {
                try {
                    l = bd.longValueExact(); // 内联 exactLong 校验
                } catch (final ArithmeticException e) {
                    throw new ArithmeticException("[ R2MO ] 非整数或超出 " + target.getSimpleName() + " 可表示范围: " + bd);
                }
            } else {
                l = (Long) value;
            }

            if (target == Integer.class) {
                if (l < Integer.MIN_VALUE || l > Integer.MAX_VALUE) {
                    throw new ArithmeticException("[ R2MO ] 数值超出 Integer 可表示范围: " + l);
                }
                return (N) Integer.valueOf((int) l);
            }
            if (target == Short.class) {
                if (l < Short.MIN_VALUE || l > Short.MAX_VALUE) {
                    throw new ArithmeticException("[ R2MO ] 数值超出 Short 可表示范围: " + l);
                }
                return (N) Short.valueOf((short) l);
            }
            // Byte
            if (l < Byte.MIN_VALUE || l > Byte.MAX_VALUE) {
                throw new ArithmeticException("[ R2MO ] 数值超出 Byte 可表示范围: " + l);
            }
            return (N) Byte.valueOf((byte) l);
        }

        // 目标：Double / Float（允许精度损失）
        if (target == Double.class) {
            final double d = (value instanceof final BigDecimal bd) ? bd.doubleValue() : ((Long) value).doubleValue();
            return (N) Double.valueOf(d);
        }
        if (target == Float.class) {
            final float f = (value instanceof final BigDecimal bd) ? bd.floatValue() : ((Long) value).floatValue();
            return (N) Float.valueOf(f);
        }

        throw new IllegalArgumentException("[ R2MO ] 不支持的数值目标类型: " + target.getName());
    }

}

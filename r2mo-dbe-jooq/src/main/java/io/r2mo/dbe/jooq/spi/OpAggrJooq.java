package io.r2mo.dbe.jooq.spi;

import io.r2mo.base.dbe.constant.QCV;
import io.r2mo.base.dbe.operation.OpAggr;
import io.r2mo.base.dbe.operation.QrAnalyzer;
import io.r2mo.base.dbe.syntax.QTree;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.impl.DSL;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author lang : 2025-10-19
 */
class OpAggrJooq<T> extends AbstractDbJooq<T> implements OpAggr {
    private final QrAnalyzer<Condition> analyzer;

    protected OpAggrJooq(final Class<T> entityCls, final DSLContext context) {
        super(entityCls, context);
        this.analyzer = new QrAnalyzerJooq(entityCls, context);
    }

    @Override
    public <N extends Number> Optional<N> execute(final String aggrField,
                                                  final Class<N> returnCls,
                                                  final QCV.Aggr aggr, final String field, final Object value) {
        final Condition condition = this.analyzer.where(field, value);
        return this.execute(aggrField, returnCls, aggr, condition);
    }

    @Override
    public <N extends Number> Optional<N> execute(final String aggrField,
                                                  final Class<N> returnCls,
                                                  final QCV.Aggr aggr, final QTree criteria) {
        final Condition condition = this.analyzer.where(criteria);
        return this.execute(aggrField, returnCls, aggr, condition);
    }

    @Override
    public <N extends Number> Optional<N> execute(final String aggrField,
                                                  final Class<N> returnCls,
                                                  final QCV.Aggr aggr, final Map<String, Object> map) {
        final Condition condition = this.analyzer.where(map);
        return this.execute(aggrField, returnCls, aggr, condition);
    }

    @Override
    public <N extends Number> ConcurrentMap<String, N> execute(final String aggrField,
                                                               final Class<N> returnCls,
                                                               final QCV.Aggr aggr,
                                                               final String field,
                                                               final Object value,
                                                               final String groupBy) {
        final Condition condition = this.analyzer.where(field, value);
        return this.executeGrouped(aggrField, returnCls, aggr, condition, groupBy);
    }

    @Override
    public <N extends Number> ConcurrentMap<String, N> execute(final String aggrField,
                                                               final Class<N> returnCls,
                                                               final QCV.Aggr aggr,
                                                               final QTree criteria,
                                                               final String groupBy) {
        final Condition condition = this.analyzer.where(criteria);
        return this.executeGrouped(aggrField, returnCls, aggr, condition, groupBy);
    }

    @Override
    public <N extends Number> ConcurrentMap<String, N> execute(final String aggrField,
                                                               final Class<N> returnCls,
                                                               final QCV.Aggr aggr,
                                                               final Map<String, Object> map,
                                                               final String groupBy) {
        final Condition condition = this.analyzer.where(map);
        return this.executeGrouped(aggrField, returnCls, aggr, condition, groupBy);
    }

    /* ==================== 私有通用方法：分组聚集（严格列名版） ==================== */
    private <N extends Number> ConcurrentMap<String, N> executeGrouped(
        final String aggrField,
        final Class<N> returnCls,
        final QCV.Aggr aggr,
        final Condition condition,
        final String groupBy
    ) {
        Objects.requireNonNull(aggr, "[ R2MO ] 聚集函数 aggr 不能为空");
        Objects.requireNonNull(groupBy, "[ R2MO ] 分组字段 groupBy 不能为空");

        // 1) 分组字段：必须是实体真实列
        @SuppressWarnings("unchecked") final Field<Object> groupField = (Field<Object>) Objects.requireNonNull(
            this.meta.findColumn(groupBy),
            "[ R2MO ] 分组字段不存在或不可识别: " + groupBy
        );

        // 2) 聚集源字段（COUNT 例外）：也要求是实体真实列
        final Field<?> srcField;
        if (aggr == QCV.Aggr.COUNT) {
            srcField = null; // COUNT(*) 用不到具体列
        } else {
            Objects.requireNonNull(aggrField, "[ R2MO ] 非 COUNT 聚集时，聚集字段 aggrField 不能为空");
            srcField = Objects.requireNonNull(
                this.meta.findColumn(aggrField),
                "[ R2MO ] 聚集字段不存在或不可识别: " + aggrField
            );
        }

        // 3) 聚集函数
        final Field<N> aggrFn = (aggr == QCV.Aggr.COUNT)
            ? DSL.count().cast(returnCls)                        // COUNT(*) 语义更直观
            : this.buildAggrFunction(srcField, aggr, returnCls); // SUM/AVG/MAX/MIN

        // 4) 执行 SQL：SELECT groupBy, AGGR(...) FROM table WHERE ... GROUP BY groupBy
        final Map<Object, N> raw = this.executor()
            .select(groupField, aggrFn)
            .from(this.meta.table())
            .where(condition)
            .groupBy(groupField)
            .fetchMap(groupField, aggrFn);

        // 5) 转为 ConcurrentMap<String, N>
        final ConcurrentMap<String, N> result =
            new ConcurrentHashMap<>(Math.max(16, raw.size() * 2));
        raw.forEach((k, v) -> {
            if (k != null && v != null) {
                result.put(String.valueOf(k), v);
            }
        });
        return result;
    }


    /**
     * 🔄 执行聚合查询并返回 Optional 结果
     *
     * @param <N>       🎯 返回值的数字类型
     * @param aggrField 📈 要进行聚合的字段名称
     * @param returnCls 🔄 期望返回的数字类型
     * @param aggr      🧮 聚合操作类型
     * @param condition 🔍 查询条件
     *
     * @return 💰 包含聚合结果的 Optional，如果无结果则为空
     * @since 💡 1.0.0
     */
    private <N extends Number> Optional<N> execute(
        final String aggrField,
        final Class<N> returnCls,
        final QCV.Aggr aggr,
        final Condition condition) {

        // 🏗️ 获取字段
        final Field<?> field = Objects.isNull(aggrField) ? DSL.field("*") : this.meta.findColumn(aggrField);
        final Field<N> aggrFunction = this.buildAggrFunction(field, aggr, returnCls);

        // 🔍 执行查询
        final N result = this.executor().select(aggrFunction)
            .from(this.meta.table())
            .where(condition)
            .fetchOne(aggrFunction);

        return Optional.ofNullable(result);
    }

    /**
     * 🔧 构建聚合函数
     */
    @SuppressWarnings("unchecked")
    private <N extends Number> Field<N> buildAggrFunction(
        final Field<?> field,
        final QCV.Aggr aggr,
        final Class<N> returnCls) {

        return switch (aggr) {
            case COUNT -> DSL.count().cast(returnCls);
            case SUM -> DSL.sum((Field<? extends Number>) field).cast(returnCls);
            case AVG -> DSL.avg((Field<? extends Number>) field).cast(returnCls);
            case MAX -> DSL.max(field).cast(returnCls);
            case MIN -> DSL.min(field).cast(returnCls);
        };
    }
}

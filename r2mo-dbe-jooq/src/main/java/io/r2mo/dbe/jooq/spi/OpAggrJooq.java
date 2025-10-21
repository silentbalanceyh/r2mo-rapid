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

/**
 * @author lang : 2025-10-19
 */
class OpAggrJooq<T> extends AbstractDbJooq<T> implements OpAggr {
    private final QrAnalyzer<Condition> analyzer;

    protected OpAggrJooq(final Class<T> entityCls, final DSLContext context) {
        super(entityCls, context);
        this.analyzer = new QrAnalyzerCondition(entityCls, context);
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
            case COUNT -> DSL.count(field).cast(returnCls);
            case SUM -> DSL.sum((Field<? extends Number>) field).cast(returnCls);
            case AVG -> DSL.avg((Field<? extends Number>) field).cast(returnCls);
            case MAX -> DSL.max(field).cast(returnCls);
            case MIN -> DSL.min(field).cast(returnCls);
        };
    }
}

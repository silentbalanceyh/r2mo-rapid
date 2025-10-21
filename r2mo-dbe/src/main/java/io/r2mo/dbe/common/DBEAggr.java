package io.r2mo.dbe.common;

import io.r2mo.base.dbe.constant.QCV;
import io.r2mo.base.dbe.operation.OpAggr;
import io.r2mo.base.dbe.operation.OpDb;
import io.r2mo.base.dbe.operation.OpVary;
import io.r2mo.base.dbe.operation.QrAnalyzer;
import io.r2mo.base.dbe.operation.QrMany;
import io.r2mo.base.dbe.operation.QrOne;
import io.r2mo.base.dbe.syntax.QTree;
import io.r2mo.spi.SPI;
import io.r2mo.typed.json.JObject;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentMap;

/**
 * @author lang : 2025-10-21
 */
class DBEAggr<QR, T, EXECUTOR> extends DBEConfiguration {
    private final EXECUTOR executor;
    protected final Class<T> entityCls;
    // 操作专用函数
    protected final OpAggr opAggr;
    protected final OpDb<T> opDb;
    protected final QrOne<T> qrOne;
    protected final QrMany<T> qrMany;

    protected final OpVary<T, QR> opVary;
    protected final QrAnalyzer<QR> qrAnalyzer;

    protected DBEAggr(final Class<T> entityCls, final EXECUTOR executor) {
        this.entityCls = entityCls;
        this.executor = executor;

        this.opAggr = SPI.SPI_DB.opAggr(entityCls, executor);
        this.qrOne = SPI.SPI_DB.qrOne(entityCls, executor);
        this.qrMany = SPI.SPI_DB.qrMany(entityCls, executor);
        this.opDb = SPI.SPI_DB.opDb(entityCls, executor);

        this.opVary = SPI.SPI_DB.opVary(entityCls, executor);
        this.qrAnalyzer = SPI.SPI_DB.qrAnalyzer(entityCls, executor);
    }

    protected EXECUTOR executor() {
        return this.executor;
    }

    protected Class<T> entityCls() {
        return this.entityCls;
    }

    // ---- COUNT
    public Optional<Long> count() {
        return this.opAggr.execute(null, Long.class, QCV.Aggr.COUNT, (QTree) null);
    }

    public Optional<Long> count(final String field, final Object value) {
        return this.opAggr.execute(null, Long.class, QCV.Aggr.COUNT, field, value);
    }

    public Optional<Long> count(final QTree criteria) {
        return this.opAggr.execute(null, Long.class, QCV.Aggr.COUNT, criteria);
    }

    public Optional<Long> count(final Map<String, Object> condition) {
        return this.opAggr.execute(null, Long.class, QCV.Aggr.COUNT, condition);
    }

    public Optional<Long> count(final JObject criteriaJ) {
        return this.count(QTree.of(criteriaJ));
    }

    // ---- SUM
    public Optional<BigDecimal> sum(final String aggrField, final String field, final Object value) {
        return this.opAggr.execute(aggrField, BigDecimal.class, QCV.Aggr.SUM, field, value);
    }

    public Optional<BigDecimal> sum(final String aggrField, final QTree criteria) {
        return this.opAggr.execute(aggrField, BigDecimal.class, QCV.Aggr.SUM, criteria);
    }

    public Optional<BigDecimal> sum(final String aggrField, final Map<String, Object> condition) {
        return this.opAggr.execute(aggrField, BigDecimal.class, QCV.Aggr.SUM, condition);
    }

    public Optional<BigDecimal> sum(final String aggrField, final JObject criteriaJ) {
        return this.sum(aggrField, QTree.of(criteriaJ));
    }

    // ---- AVG
    public Optional<BigDecimal> avg(final String aggrField, final String field, final Object value) {
        return this.opAggr.execute(aggrField, BigDecimal.class, QCV.Aggr.AVG, field, value);
    }

    public Optional<BigDecimal> avg(final String aggrField, final QTree criteria) {
        return this.opAggr.execute(aggrField, BigDecimal.class, QCV.Aggr.AVG, criteria);
    }

    public Optional<BigDecimal> avg(final String aggrField, final JObject criteriaJ) {
        return this.avg(aggrField, QTree.of(criteriaJ));
    }

    public Optional<BigDecimal> avg(final String aggrField, final Map<String, Object> condition) {
        return this.opAggr.execute(aggrField, BigDecimal.class, QCV.Aggr.AVG, condition);
    }

    // ---- MIN
    public Optional<BigDecimal> min(final String aggrField, final String field, final Object value) {
        return this.opAggr.execute(aggrField, BigDecimal.class, QCV.Aggr.MIN, field, value);
    }

    public Optional<BigDecimal> min(final String aggrField, final QTree criteria) {
        return this.opAggr.execute(aggrField, BigDecimal.class, QCV.Aggr.MIN, criteria);
    }

    public Optional<BigDecimal> min(final String aggrField, final Map<String, Object> condition) {
        return this.opAggr.execute(aggrField, BigDecimal.class, QCV.Aggr.MIN, condition);
    }

    public Optional<BigDecimal> min(final String aggrField, final JObject criteriaJ) {
        return this.min(aggrField, QTree.of(criteriaJ));
    }

    // ---- MAX
    public Optional<BigDecimal> max(final String aggrField, final String field, final Object value) {
        return this.opAggr.execute(aggrField, BigDecimal.class, QCV.Aggr.MAX, field, value);
    }

    public Optional<BigDecimal> max(final String aggrField, final QTree criteria) {
        return this.opAggr.execute(aggrField, BigDecimal.class, QCV.Aggr.MAX, criteria);
    }

    public Optional<BigDecimal> max(final String aggrField, final Map<String, Object> condition) {
        return this.opAggr.execute(aggrField, BigDecimal.class, QCV.Aggr.MAX, condition);
    }

    public Optional<BigDecimal> max(final String aggrField, final JObject criteriaJ) {
        return this.max(aggrField, QTree.of(criteriaJ));
    }

    // ---- 标准分组聚集
    // ---- COUNT (groupBy)
    public ConcurrentMap<String, Long> countBy(final String groupBy) {
        return this.opAggr.execute(null, Long.class, QCV.Aggr.COUNT, (QTree) null, groupBy);
    }

    public ConcurrentMap<String, Long> countBy(final String field, final Object value, final String groupBy) {
        return this.opAggr.execute(null, Long.class, QCV.Aggr.COUNT, field, value, groupBy);
    }

    public ConcurrentMap<String, Long> countBy(final QTree criteria, final String groupBy) {
        return this.opAggr.execute(null, Long.class, QCV.Aggr.COUNT, criteria, groupBy);
    }

    public ConcurrentMap<String, Long> countBy(final Map<String, Object> condition, final String groupBy) {
        return this.opAggr.execute(null, Long.class, QCV.Aggr.COUNT, condition, groupBy);
    }

    public ConcurrentMap<String, Long> countBy(final JObject criteriaJ, final String groupBy) {
        return this.countBy(QTree.of(criteriaJ), groupBy);
    }

    // ---- SUM (groupBy)
    public ConcurrentMap<String, BigDecimal> sumBy(final String aggrField, final String field, final Object value, final String groupBy) {
        return this.opAggr.execute(aggrField, BigDecimal.class, QCV.Aggr.SUM, field, value, groupBy);
    }

    public ConcurrentMap<String, BigDecimal> sumBy(final String aggrField, final QTree criteria, final String groupBy) {
        return this.opAggr.execute(aggrField, BigDecimal.class, QCV.Aggr.SUM, criteria, groupBy);
    }

    public ConcurrentMap<String, BigDecimal> sumBy(final String aggrField, final Map<String, Object> condition, final String groupBy) {
        return this.opAggr.execute(aggrField, BigDecimal.class, QCV.Aggr.SUM, condition, groupBy);
    }

    public ConcurrentMap<String, BigDecimal> sumBy(final String aggrField, final JObject criteriaJ, final String groupBy) {
        return this.sumBy(aggrField, QTree.of(criteriaJ), groupBy);
    }

    // ---- AVG (groupBy)
    public ConcurrentMap<String, BigDecimal> avgBy(final String aggrField, final String field, final Object value, final String groupBy) {
        return this.opAggr.execute(aggrField, BigDecimal.class, QCV.Aggr.AVG, field, value, groupBy);
    }

    public ConcurrentMap<String, BigDecimal> avgBy(final String aggrField, final QTree criteria, final String groupBy) {
        return this.opAggr.execute(aggrField, BigDecimal.class, QCV.Aggr.AVG, criteria, groupBy);
    }

    public ConcurrentMap<String, BigDecimal> avgBy(final String aggrField, final Map<String, Object> condition, final String groupBy) {
        return this.opAggr.execute(aggrField, BigDecimal.class, QCV.Aggr.AVG, condition, groupBy);
    }

    public ConcurrentMap<String, BigDecimal> avgBy(final String aggrField, final JObject criteriaJ, final String groupBy) {
        return this.avgBy(aggrField, QTree.of(criteriaJ), groupBy);
    }

    // ---- MIN (groupBy)
    public ConcurrentMap<String, BigDecimal> minBy(final String aggrField, final String field, final Object value, final String groupBy) {
        return this.opAggr.execute(aggrField, BigDecimal.class, QCV.Aggr.MIN, field, value, groupBy);
    }

    public ConcurrentMap<String, BigDecimal> minBy(final String aggrField, final QTree criteria, final String groupBy) {
        return this.opAggr.execute(aggrField, BigDecimal.class, QCV.Aggr.MIN, criteria, groupBy);
    }

    public ConcurrentMap<String, BigDecimal> minBy(final String aggrField, final Map<String, Object> condition, final String groupBy) {
        return this.opAggr.execute(aggrField, BigDecimal.class, QCV.Aggr.MIN, condition, groupBy);
    }

    public ConcurrentMap<String, BigDecimal> minBy(final String aggrField, final JObject criteriaJ, final String groupBy) {
        return this.minBy(aggrField, QTree.of(criteriaJ), groupBy);
    }

    // ---- MAX (groupBy)
    public ConcurrentMap<String, BigDecimal> maxBy(final String aggrField, final String field, final Object value, final String groupBy) {
        return this.opAggr.execute(aggrField, BigDecimal.class, QCV.Aggr.MAX, field, value, groupBy);
    }

    public ConcurrentMap<String, BigDecimal> maxBy(final String aggrField, final QTree criteria, final String groupBy) {
        return this.opAggr.execute(aggrField, BigDecimal.class, QCV.Aggr.MAX, criteria, groupBy);
    }

    public ConcurrentMap<String, BigDecimal> maxBy(final String aggrField, final Map<String, Object> condition, final String groupBy) {
        return this.opAggr.execute(aggrField, BigDecimal.class, QCV.Aggr.MAX, condition, groupBy);
    }

    public ConcurrentMap<String, BigDecimal> maxBy(final String aggrField, final JObject criteriaJ, final String groupBy) {
        return this.maxBy(aggrField, QTree.of(criteriaJ), groupBy);
    }

    // ===================== SUM (aggrGroup = aggrField = groupBy) =====================
    public ConcurrentMap<String, BigDecimal> sumBy(final String aggrGroup, final String field, final Object value) {
        return this.opAggr.execute(aggrGroup, BigDecimal.class, QCV.Aggr.SUM, field, value, aggrGroup);
    }

    public ConcurrentMap<String, BigDecimal> sumBy(final String aggrGroup, final QTree criteria) {
        return this.opAggr.execute(aggrGroup, BigDecimal.class, QCV.Aggr.SUM, criteria, aggrGroup);
    }

    public ConcurrentMap<String, BigDecimal> sumBy(final String aggrGroup, final Map<String, Object> condition) {
        return this.opAggr.execute(aggrGroup, BigDecimal.class, QCV.Aggr.SUM, condition, aggrGroup);
    }

    public ConcurrentMap<String, BigDecimal> sumBy(final String aggrGroup, final JObject criteriaJ) {
        return this.sumBy(aggrGroup, QTree.of(criteriaJ));
    }

    // ===================== AVG (aggrGroup = aggrField = groupBy) =====================
    public ConcurrentMap<String, BigDecimal> avgBy(final String aggrGroup, final String field, final Object value) {
        return this.opAggr.execute(aggrGroup, BigDecimal.class, QCV.Aggr.AVG, field, value, aggrGroup);
    }

    public ConcurrentMap<String, BigDecimal> avgBy(final String aggrGroup, final QTree criteria) {
        return this.opAggr.execute(aggrGroup, BigDecimal.class, QCV.Aggr.AVG, criteria, aggrGroup);
    }

    public ConcurrentMap<String, BigDecimal> avgBy(final String aggrGroup, final Map<String, Object> condition) {
        return this.opAggr.execute(aggrGroup, BigDecimal.class, QCV.Aggr.AVG, condition, aggrGroup);
    }

    public ConcurrentMap<String, BigDecimal> avgBy(final String aggrGroup, final JObject criteriaJ) {
        return this.avgBy(aggrGroup, QTree.of(criteriaJ));
    }

    // ===================== MIN (aggrGroup = aggrField = groupBy) =====================
    public ConcurrentMap<String, BigDecimal> minBy(final String aggrGroup, final String field, final Object value) {
        return this.opAggr.execute(aggrGroup, BigDecimal.class, QCV.Aggr.MIN, field, value, aggrGroup);
    }

    public ConcurrentMap<String, BigDecimal> minBy(final String aggrGroup, final QTree criteria) {
        return this.opAggr.execute(aggrGroup, BigDecimal.class, QCV.Aggr.MIN, criteria, aggrGroup);
    }

    public ConcurrentMap<String, BigDecimal> minBy(final String aggrGroup, final Map<String, Object> condition) {
        return this.opAggr.execute(aggrGroup, BigDecimal.class, QCV.Aggr.MIN, condition, aggrGroup);
    }

    public ConcurrentMap<String, BigDecimal> minBy(final String aggrGroup, final JObject criteriaJ) {
        return this.minBy(aggrGroup, QTree.of(criteriaJ));
    }

    // ===================== MAX (aggrGroup = aggrField = groupBy) =====================
    public ConcurrentMap<String, BigDecimal> maxBy(final String aggrGroup, final String field, final Object value) {
        return this.opAggr.execute(aggrGroup, BigDecimal.class, QCV.Aggr.MAX, field, value, aggrGroup);
    }

    public ConcurrentMap<String, BigDecimal> maxBy(final String aggrGroup, final QTree criteria) {
        return this.opAggr.execute(aggrGroup, BigDecimal.class, QCV.Aggr.MAX, criteria, aggrGroup);
    }

    public ConcurrentMap<String, BigDecimal> maxBy(final String aggrGroup, final Map<String, Object> condition) {
        return this.opAggr.execute(aggrGroup, BigDecimal.class, QCV.Aggr.MAX, condition, aggrGroup);
    }

    public ConcurrentMap<String, BigDecimal> maxBy(final String aggrGroup, final JObject criteriaJ) {
        return this.maxBy(aggrGroup, QTree.of(criteriaJ), aggrGroup);
    }
}

package io.r2mo.vertx.dbe;

import io.r2mo.base.dbe.constant.QCV;
import io.r2mo.base.dbe.operation.QrAnalyzer;
import io.r2mo.base.dbe.syntax.QTree;
import io.r2mo.spi.SPI;
import io.r2mo.typed.json.JObject;
import io.vertx.core.Future;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

/**
 * @author lang : 2025-10-21
 */
class AsyncDBEAggr<QR, T, METADATA> {
    private static final FactoryDBAsync factory = SPI.findOne(FactoryDBAsync.class);
    // 操作专用函数
    protected final AsyncAggr opAggr;
    protected final AsyncDb<T> opDb;
    protected final AsyncOne<T> qrOne;
    protected final AsyncMany<T> qrMany;

    protected final AsyncVary<T, QR> opVary;
    protected final QrAnalyzer<QR> qrAnalyzer;


    protected final Class<T> entityCls;
    protected final METADATA metadata;

    protected AsyncDBEAggr(final Class<T> entityCls, final METADATA meta) {
        this.entityCls = entityCls;
        this.metadata = meta;

        this.opAggr = factory.opAggr(entityCls, meta);
        this.opDb = factory.opDb(entityCls, meta);
        this.qrOne = factory.qrOne(entityCls, meta);
        this.qrMany = factory.qrMany(entityCls, meta);
        this.opVary = factory.opVary(entityCls, meta);
        this.qrAnalyzer = factory.qrAnalyzer(entityCls, meta);
    }

    // ====== COUNT ======
    public Future<Long> countAsync() {
        return this.opAggr.executeAsync(null, Long.class, QCV.Aggr.COUNT, (QTree) null);
    }

    public Future<Long> countAsync(final String field, final Object value) {
        return this.opAggr.executeAsync(null, Long.class, QCV.Aggr.COUNT, field, value);
    }

    public Future<Long> countAsync(final QTree criteria) {
        return this.opAggr.executeAsync(null, Long.class, QCV.Aggr.COUNT, criteria);
    }

    public Future<Long> countAsync(final Map<String, Object> condition) {
        return this.opAggr.executeAsync(null, Long.class, QCV.Aggr.COUNT, condition);
    }

    public Future<Long> countAsync(final JObject criteriaJ) {
        return this.countAsync(QTree.of(criteriaJ));
    }

    // ====== SUM ======
    public Future<BigDecimal> sumAsync(final String aggrField, final String field, final Object value) {
        return this.opAggr.executeAsync(aggrField, BigDecimal.class, QCV.Aggr.SUM, field, value);
    }

    public Future<BigDecimal> sumAsync(final String aggrField, final QTree criteria) {
        return this.opAggr.executeAsync(aggrField, BigDecimal.class, QCV.Aggr.SUM, criteria);
    }

    public Future<BigDecimal> sumAsync(final String aggrField, final Map<String, Object> condition) {
        return this.opAggr.executeAsync(aggrField, BigDecimal.class, QCV.Aggr.SUM, condition);
    }

    public Future<BigDecimal> sumAsync(final String aggrField, final JObject criteriaJ) {
        return this.sumAsync(aggrField, QTree.of(criteriaJ));
    }

    // ====== AVG ======
    public Future<BigDecimal> avgAsync(final String aggrField, final String field, final Object value) {
        return this.opAggr.executeAsync(aggrField, BigDecimal.class, QCV.Aggr.AVG, field, value);
    }

    public Future<BigDecimal> avgAsync(final String aggrField, final QTree criteria) {
        return this.opAggr.executeAsync(aggrField, BigDecimal.class, QCV.Aggr.AVG, criteria);
    }

    public Future<BigDecimal> avgAsync(final String aggrField, final JObject criteriaJ) {
        return this.avgAsync(aggrField, QTree.of(criteriaJ));
    }

    public Future<BigDecimal> avgAsync(final String aggrField, final Map<String, Object> condition) {
        return this.opAggr.executeAsync(aggrField, BigDecimal.class, QCV.Aggr.AVG, condition);
    }

    // ====== MIN ======
    public Future<BigDecimal> minAsync(final String aggrField, final String field, final Object value) {
        return this.opAggr.executeAsync(aggrField, BigDecimal.class, QCV.Aggr.MIN, field, value);
    }

    public Future<BigDecimal> minAsync(final String aggrField, final QTree criteria) {
        return this.opAggr.executeAsync(aggrField, BigDecimal.class, QCV.Aggr.MIN, criteria);
    }

    public Future<BigDecimal> minAsync(final String aggrField, final Map<String, Object> condition) {
        return this.opAggr.executeAsync(aggrField, BigDecimal.class, QCV.Aggr.MIN, condition);
    }

    public Future<BigDecimal> minAsync(final String aggrField, final JObject criteriaJ) {
        return this.minAsync(aggrField, QTree.of(criteriaJ));
    }

    // ====== MAX ======
    public Future<BigDecimal> maxAsync(final String aggrField, final String field, final Object value) {
        return this.opAggr.executeAsync(aggrField, BigDecimal.class, QCV.Aggr.MAX, field, value);
    }

    public Future<BigDecimal> maxAsync(final String aggrField, final QTree criteria) {
        return this.opAggr.executeAsync(aggrField, BigDecimal.class, QCV.Aggr.MAX, criteria);
    }

    public Future<BigDecimal> maxAsync(final String aggrField, final Map<String, Object> condition) {
        return this.opAggr.executeAsync(aggrField, BigDecimal.class, QCV.Aggr.MAX, condition);
    }

    public Future<BigDecimal> maxAsync(final String aggrField, final JObject criteriaJ) {
        return this.maxAsync(aggrField, QTree.of(criteriaJ));
    }

    // ====== 标准分组聚集（By） ======
    // COUNT By
    public Future<ConcurrentMap<String, Long>> countByAsync(final String groupBy) {
        return this.opAggr.executeAsync(null, Long.class, QCV.Aggr.COUNT, (QTree) null, groupBy);
    }

    public Future<ConcurrentMap<String, Long>> countByAsync(final String field, final Object value, final String groupBy) {
        return this.opAggr.executeAsync(null, Long.class, QCV.Aggr.COUNT, field, value, groupBy);
    }

    public Future<ConcurrentMap<String, Long>> countByAsync(final QTree criteria, final String groupBy) {
        return this.opAggr.executeAsync(null, Long.class, QCV.Aggr.COUNT, criteria, groupBy);
    }

    public Future<ConcurrentMap<String, Long>> countByAsync(final Map<String, Object> condition, final String groupBy) {
        return this.opAggr.executeAsync(null, Long.class, QCV.Aggr.COUNT, condition, groupBy);
    }

    public Future<ConcurrentMap<String, Long>> countByAsync(final JObject criteriaJ, final String groupBy) {
        return this.countByAsync(QTree.of(criteriaJ), groupBy);
    }

    // SUM By
    public Future<ConcurrentMap<String, BigDecimal>> sumByAsync(final String aggrField, final String field, final Object value, final String groupBy) {
        return this.opAggr.executeAsync(aggrField, BigDecimal.class, QCV.Aggr.SUM, field, value, groupBy);
    }

    public Future<ConcurrentMap<String, BigDecimal>> sumByAsync(final String aggrField, final QTree criteria, final String groupBy) {
        return this.opAggr.executeAsync(aggrField, BigDecimal.class, QCV.Aggr.SUM, criteria, groupBy);
    }

    public Future<ConcurrentMap<String, BigDecimal>> sumByAsync(final String aggrField, final Map<String, Object> condition, final String groupBy) {
        return this.opAggr.executeAsync(aggrField, BigDecimal.class, QCV.Aggr.SUM, condition, groupBy);
    }

    public Future<ConcurrentMap<String, BigDecimal>> sumByAsync(final String aggrField, final JObject criteriaJ, final String groupBy) {
        return this.sumByAsync(aggrField, QTree.of(criteriaJ), groupBy);
    }

    // AVG By
    public Future<ConcurrentMap<String, BigDecimal>> avgByAsync(final String aggrField, final String field, final Object value, final String groupBy) {
        return this.opAggr.executeAsync(aggrField, BigDecimal.class, QCV.Aggr.AVG, field, value, groupBy);
    }

    public Future<ConcurrentMap<String, BigDecimal>> avgByAsync(final String aggrField, final QTree criteria, final String groupBy) {
        return this.opAggr.executeAsync(aggrField, BigDecimal.class, QCV.Aggr.AVG, criteria, groupBy);
    }

    public Future<ConcurrentMap<String, BigDecimal>> avgByAsync(final String aggrField, final Map<String, Object> condition, final String groupBy) {
        return this.opAggr.executeAsync(aggrField, BigDecimal.class, QCV.Aggr.AVG, condition, groupBy);
    }

    public Future<ConcurrentMap<String, BigDecimal>> avgByAsync(final String aggrField, final JObject criteriaJ, final String groupBy) {
        return this.avgByAsync(aggrField, QTree.of(criteriaJ), groupBy);
    }

    // MIN By
    public Future<ConcurrentMap<String, BigDecimal>> minByAsync(final String aggrField, final String field, final Object value, final String groupBy) {
        return this.opAggr.executeAsync(aggrField, BigDecimal.class, QCV.Aggr.MIN, field, value, groupBy);
    }

    public Future<ConcurrentMap<String, BigDecimal>> minByAsync(final String aggrField, final QTree criteria, final String groupBy) {
        return this.opAggr.executeAsync(aggrField, BigDecimal.class, QCV.Aggr.MIN, criteria, groupBy);
    }

    public Future<ConcurrentMap<String, BigDecimal>> minByAsync(final String aggrField, final Map<String, Object> condition, final String groupBy) {
        return this.opAggr.executeAsync(aggrField, BigDecimal.class, QCV.Aggr.MIN, condition, groupBy);
    }

    public Future<ConcurrentMap<String, BigDecimal>> minByAsync(final String aggrField, final JObject criteriaJ, final String groupBy) {
        return this.minByAsync(aggrField, QTree.of(criteriaJ), groupBy);
    }

    // MAX By
    public Future<ConcurrentMap<String, BigDecimal>> maxByAsync(final String aggrField, final String field, final Object value, final String groupBy) {
        return this.opAggr.executeAsync(aggrField, BigDecimal.class, QCV.Aggr.MAX, field, value, groupBy);
    }

    public Future<ConcurrentMap<String, BigDecimal>> maxByAsync(final String aggrField, final QTree criteria, final String groupBy) {
        return this.opAggr.executeAsync(aggrField, BigDecimal.class, QCV.Aggr.MAX, criteria, groupBy);
    }

    public Future<ConcurrentMap<String, BigDecimal>> maxByAsync(final String aggrField, final Map<String, Object> condition, final String groupBy) {
        return this.opAggr.executeAsync(aggrField, BigDecimal.class, QCV.Aggr.MAX, condition, groupBy);
    }

    public Future<ConcurrentMap<String, BigDecimal>> maxByAsync(final String aggrField, final JObject criteriaJ, final String groupBy) {
        return this.maxByAsync(aggrField, QTree.of(criteriaJ), groupBy);
    }

    // ====== 快捷版（aggrGroup = aggrField = groupBy） ======
    // SUM
    public Future<ConcurrentMap<String, BigDecimal>> sumByAsync(final String aggrGroup, final String field, final Object value) {
        return this.opAggr.executeAsync(aggrGroup, BigDecimal.class, QCV.Aggr.SUM, field, value, aggrGroup);
    }

    public Future<ConcurrentMap<String, BigDecimal>> sumByAsync(final String aggrGroup, final QTree criteria) {
        return this.opAggr.executeAsync(aggrGroup, BigDecimal.class, QCV.Aggr.SUM, criteria, aggrGroup);
    }

    public Future<ConcurrentMap<String, BigDecimal>> sumByAsync(final String aggrGroup, final Map<String, Object> condition) {
        return this.opAggr.executeAsync(aggrGroup, BigDecimal.class, QCV.Aggr.SUM, condition, aggrGroup);
    }

    public Future<ConcurrentMap<String, BigDecimal>> sumByAsync(final String aggrGroup, final JObject criteriaJ) {
        return this.sumByAsync(aggrGroup, QTree.of(criteriaJ));
    }

    // AVG
    public Future<ConcurrentMap<String, BigDecimal>> avgByAsync(final String aggrGroup, final String field, final Object value) {
        return this.opAggr.executeAsync(aggrGroup, BigDecimal.class, QCV.Aggr.AVG, field, value, aggrGroup);
    }

    public Future<ConcurrentMap<String, BigDecimal>> avgByAsync(final String aggrGroup, final QTree criteria) {
        return this.opAggr.executeAsync(aggrGroup, BigDecimal.class, QCV.Aggr.AVG, criteria, aggrGroup);
    }

    public Future<ConcurrentMap<String, BigDecimal>> avgByAsync(final String aggrGroup, final Map<String, Object> condition) {
        return this.opAggr.executeAsync(aggrGroup, BigDecimal.class, QCV.Aggr.AVG, condition, aggrGroup);
    }

    public Future<ConcurrentMap<String, BigDecimal>> avgByAsync(final String aggrGroup, final JObject criteriaJ) {
        return this.avgByAsync(aggrGroup, QTree.of(criteriaJ));
    }

    // MIN
    public Future<ConcurrentMap<String, BigDecimal>> minByAsync(final String aggrGroup, final String field, final Object value) {
        return this.opAggr.executeAsync(aggrGroup, BigDecimal.class, QCV.Aggr.MIN, field, value, aggrGroup);
    }

    public Future<ConcurrentMap<String, BigDecimal>> minByAsync(final String aggrGroup, final QTree criteria) {
        return this.opAggr.executeAsync(aggrGroup, BigDecimal.class, QCV.Aggr.MIN, criteria, aggrGroup);
    }

    public Future<ConcurrentMap<String, BigDecimal>> minByAsync(final String aggrGroup, final Map<String, Object> condition) {
        return this.opAggr.executeAsync(aggrGroup, BigDecimal.class, QCV.Aggr.MIN, condition, aggrGroup);
    }

    public Future<ConcurrentMap<String, BigDecimal>> minByAsync(final String aggrGroup, final JObject criteriaJ) {
        return this.minByAsync(aggrGroup, QTree.of(criteriaJ));
    }

    // MAX
    public Future<ConcurrentMap<String, BigDecimal>> maxByAsync(final String aggrGroup, final String field, final Object value) {
        return this.opAggr.executeAsync(aggrGroup, BigDecimal.class, QCV.Aggr.MAX, field, value, aggrGroup);
    }

    public Future<ConcurrentMap<String, BigDecimal>> maxByAsync(final String aggrGroup, final QTree criteria) {
        return this.opAggr.executeAsync(aggrGroup, BigDecimal.class, QCV.Aggr.MAX, criteria, aggrGroup);
    }

    public Future<ConcurrentMap<String, BigDecimal>> maxByAsync(final String aggrGroup, final Map<String, Object> condition) {
        return this.opAggr.executeAsync(aggrGroup, BigDecimal.class, QCV.Aggr.MAX, condition, aggrGroup);
    }

    public Future<ConcurrentMap<String, BigDecimal>> maxByAsync(final String aggrGroup, final JObject criteriaJ) {
        return this.maxByAsync(aggrGroup, QTree.of(criteriaJ), aggrGroup);
    }

}

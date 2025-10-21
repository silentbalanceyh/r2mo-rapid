package io.r2mo.dbe.common;

import io.r2mo.base.dbe.constant.OpType;
import io.r2mo.base.dbe.constant.QCV;
import io.r2mo.base.dbe.operation.OpAggr;
import io.r2mo.base.dbe.operation.OpDb;
import io.r2mo.base.dbe.operation.OpVary;
import io.r2mo.base.dbe.operation.QrAnalyzer;
import io.r2mo.base.dbe.operation.QrMany;
import io.r2mo.base.dbe.operation.QrOne;
import io.r2mo.base.dbe.syntax.QQuery;
import io.r2mo.base.dbe.syntax.QTree;
import io.r2mo.spi.SPI;
import io.r2mo.typed.common.Pagination;
import io.r2mo.typed.json.JObject;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author lang : 2025-08-28
 */
public abstract class DBE<QR, T, EXECUTOR> extends DBEConfiguration {

    private final Class<T> entityCls;
    private final EXECUTOR executor;
    // 操作专用函数
    private final OpAggr opAggr;
    private final OpDb<T> opDb;
    private final QrOne<T> qrOne;
    private final QrMany<T> qrMany;

    private final OpVary<T, QR> opVary;
    private final QrAnalyzer<QR> qrAnalyzer;

    protected DBE(final Class<T> entityCls, final EXECUTOR executor) {
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

    // ---- findOne
    public Optional<T> findOne(final Serializable id) {
        return this.qrOne.execute(id);
    }

    public Optional<T> findOne(final String field, final Object value) {
        return this.qrOne.execute(field, value);
    }

    public Optional<T> findOne(final QTree criteria) {
        return this.qrOne.execute(criteria);
    }

    public Optional<T> findOne(final JObject criteriaJ) {
        return this.findOne(QTree.of(criteriaJ));
    }

    public Optional<T> findOne(final Map<String, Object> condition) {
        return this.qrOne.execute(condition);
    }

    // ---- findMany
    public List<T> findMany(final Serializable... ids) {
        return this.qrMany.execute(ids);
    }

    public List<T> findMany(final String field, final Object value) {
        return this.qrMany.execute(field, value);
    }

    public List<T> findMany(final QTree criteria) {
        return this.qrMany.execute(criteria);
    }

    public List<T> findMany(final JObject criteriaJ) {
        return this.findMany(QTree.of(criteriaJ));
    }

    public List<T> findMany(final Map<String, Object> condition) {
        return this.qrMany.execute(condition);
    }

    // ---- findFull
    public List<T> findFull(final QQuery query) {
        return this.qrMany.execute(query);
    }

    public List<T> findFull(final JObject queryJ) {
        return this.findFull(QQuery.of(queryJ));
    }

    // ---- findPage
    public Pagination<T> findPage(final QQuery query) {
        return this.opVary.findPage(query);
    }

    public Pagination<T> findPage(final JObject queryJ) {
        return this.findPage(QQuery.of(queryJ));
    }

    // ---- findAll
    public List<T> findAll() {
        return this.opVary.findAll();
    }

    public <K> Map<K, List<T>> findGroupBy(final String groupBy) {
        final List<T> entities = this.findAll();
        return DBETool.groupBy(entities, groupBy, this.entityCls);
    }
    // ---- findGroupBy

    public <K> Map<K, List<T>> findGroupBy(final QTree criteria, final String groupBy) {
        final List<T> entities = this.findMany(criteria);
        return DBETool.groupBy(entities, groupBy, this.entityCls);
    }

    public <K> Map<K, List<T>> findGroupBy(final Map<String, Object> map, final String groupBy) {
        final List<T> entities = this.findMany(map);
        return DBETool.groupBy(entities, groupBy, this.entityCls);
    }

    public <K> Map<K, List<T>> findGroupBy(final JObject criteriaJ, final String groupBy) {
        return this.findGroupBy(QTree.of(criteriaJ), groupBy);
    }

    public <K> Map<K, List<T>> findGroupBy(final String field, final Object value, final String groupBy) {
        final List<T> entities = this.findMany(field, value);
        return DBETool.groupBy(entities, groupBy, this.entityCls);
    }

    // ---- findMap
    public List<T> findManyBy(final JObject mapJ) {
        final QR condition = this.qrAnalyzer.where(mapJ.toMap());
        return this.opVary.findMany(condition);
    }

    // ---- findManyIn
    public List<T> findManyIn(final String field, final Object... values) {
        final QR condition = this.qrAnalyzer.whereIn(field, values);
        return this.opVary.findMany(condition);
    }

    public List<T> findManyIn(final String field, final List<?> values) {
        return this.findManyIn(field, values.toArray());
    }

    // ---- CRUD
    public T create(final T entity) {
        return this.opDb.execute(entity, OpType.CREATE);
    }

    public List<T> create(final List<T> entities, final int batchSize) {
        return this.opDb.execute(entities, OpType.CREATE, batchSize);
    }

    public List<T> create(final List<T> entities) {
        return this.create(entities, this.pBatchSize());
    }

    public T update(final T entity) {
        return this.opDb.execute(entity, OpType.UPDATE);
    }

    public List<T> update(final List<T> entities, final int batchSize) {
        return this.opDb.execute(entities, OpType.UPDATE, batchSize);
    }

    public List<T> update(final List<T> entities) {
        return this.update(entities, this.pBatchSize());
    }

    public T save(final T entity) {
        return this.opDb.execute(entity, OpType.SAVE);
    }

    public List<T> save(final List<T> entities, final int batchSize) {
        return this.opDb.execute(entities, OpType.SAVE, batchSize);
    }

    public List<T> save(final List<T> entities) {
        return this.save(entities, this.pBatchSize());
    }

    public T remove(final T entity) {
        return this.opDb.execute(entity, OpType.REMOVE);
    }

    public List<T> remove(final List<T> entities, final int batchSize) {
        return this.opDb.execute(entities, OpType.REMOVE, batchSize);
    }

    public List<T> remove(final List<T> entities) {
        return this.remove(entities, this.pBatchSize());
    }

    // ---- CRUD Advanced
    public boolean removeBy(final Serializable id) {
        return this.opVary.removeById(id);
    }

    public boolean removeBy(final String field, final Object value) {
        final QR condition = this.qrAnalyzer.where(field, value);
        return this.opVary.removeBy(condition);
    }

    public boolean removeBy(final QTree criteria) {
        final QR condition = this.qrAnalyzer.where(criteria);
        return this.opVary.removeBy(condition);
    }

    public boolean removeBy(final Map<String, Object> map) {
        final QR condition = this.qrAnalyzer.where(map);
        return this.opVary.removeBy(condition);
    }

    public boolean removeBy(final JObject criteriaJ) {
        return this.removeBy(QTree.of(criteriaJ));
    }

    public T updateBy(final Serializable id, final T latest) {
        return this.opVary.update(this.findOne(id), latest);
    }

    public T updateBy(final String field, final Object value, final T latest) {
        return this.opVary.update(this.findOne(field, value), latest);
    }

    public T updateBy(final QTree criteria, final T latest) {
        return this.opVary.update(this.findOne(criteria), latest);
    }

    public T updateBy(final Map<String, Object> map, final T latest) {
        return this.opVary.update(this.findOne(map), latest);
    }

    public T updateBy(final JObject criteriaJ, final T latest) {
        return this.updateBy(QTree.of(criteriaJ), latest);
    }

    public T saveBy(final Serializable id, final T latest) {
        return this.opVary.save(this.findOne(id), latest);
    }

    public T saveBy(final String field, final Object value, final T latest) {
        return this.opVary.save(this.findOne(field, value), latest);
    }

    public T saveBy(final Map<String, Object> map, final T latest) {
        return this.opVary.save(this.findOne(map), latest);
    }

    public T saveBy(final QTree criteria, final T latest) {
        return this.opVary.save(this.findOne(criteria), latest);
    }

    public T saveBy(final JObject criteriaJ, final T latest) {
        return this.saveBy(QTree.of(criteriaJ), latest);
    }
}

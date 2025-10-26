package io.r2mo.vertx.jooq;

import io.r2mo.base.dbe.DBS;
import io.r2mo.base.dbe.syntax.QQuery;
import io.r2mo.base.dbe.syntax.QTree;
import io.r2mo.typed.common.Pagination;
import io.r2mo.typed.json.JObject;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentMap;

/**
 * 抽象同步方法集合，此处的所有方法均是同步阻塞方法
 * <pre>
 *     1. 方法签名全是同步
 *     2. 支持高阶的三种查询对象
 *        - {@link QTree}
 *        - {@link QQuery}
 *        - {@link JObject}
 * </pre>
 *
 * @author lang : 2025-10-20
 */
class DBExCommon<T> extends DBExBase<T> {
    protected DBExCommon(final Class<T> daoCls, final DBS dbs) {
        super(daoCls, dbs);
    }

    // region DBE 同步模式下的所有方法调用
    // ==================== COUNT 方法 ====================
    public Optional<Long> count() {
        return this.dbe.count();
    }

    public Optional<Long> count(final String field, final Object value) {
        return this.dbe.count(field, value);
    }

    public Optional<Long> count(final QTree criteria) {
        return this.dbe.count(criteria);
    }

    public Optional<Long> count(final Map<String, Object> condition) {
        return this.dbe.count(condition);
    }

    public Optional<Long> count(final JObject criteriaJ) {
        return this.dbe.count(criteriaJ);
    }

    // ==================== SUM 方法 ====================
    public Optional<BigDecimal> sum(final String aggrField, final String field, final Object value) {
        return this.dbe.sum(aggrField, field, value);
    }

    public Optional<BigDecimal> sum(final String aggrField, final QTree criteria) {
        return this.dbe.sum(aggrField, criteria);
    }

    public Optional<BigDecimal> sum(final String aggrField, final Map<String, Object> condition) {
        return this.dbe.sum(aggrField, condition);
    }

    public Optional<BigDecimal> sum(final String aggrField) {
        return this.dbe.sum(aggrField);
    }

    public Optional<BigDecimal> sum(final String aggrField, final JObject criteriaJ) {
        return this.dbe.sum(aggrField, criteriaJ);
    }

    // ==================== AVG 方法 ====================
    public Optional<BigDecimal> avg(final String aggrField, final String field, final Object value) {
        return this.dbe.avg(aggrField, field, value);
    }

    public Optional<BigDecimal> avg(final String aggrField, final QTree criteria) {
        return this.dbe.avg(aggrField, criteria);
    }

    public Optional<BigDecimal> avg(final String aggrField, final JObject criteriaJ) {
        return this.dbe.avg(aggrField, criteriaJ);
    }

    public Optional<BigDecimal> avg(final String aggrField, final Map<String, Object> condition) {
        return this.dbe.avg(aggrField, condition);
    }

    public Optional<BigDecimal> avg(final String aggrField) {
        return this.dbe.avg(aggrField);
    }

    // ==================== MIN 方法 ====================
    public Optional<BigDecimal> min(final String aggrField, final String field, final Object value) {
        return this.dbe.min(aggrField, field, value);
    }

    public Optional<BigDecimal> min(final String aggrField, final QTree criteria) {
        return this.dbe.min(aggrField, criteria);
    }

    public Optional<BigDecimal> min(final String aggrField, final Map<String, Object> condition) {
        return this.dbe.min(aggrField, condition);
    }

    public Optional<BigDecimal> min(final String aggrField) {
        return this.dbe.min(aggrField);
    }

    public Optional<BigDecimal> min(final String aggrField, final JObject criteriaJ) {
        return this.dbe.min(aggrField, criteriaJ);
    }

    // ==================== MAX 方法 ====================
    public Optional<BigDecimal> max(final String aggrField, final String field, final Object value) {
        return this.dbe.max(aggrField, field, value);
    }

    public Optional<BigDecimal> max(final String aggrField, final QTree criteria) {
        return this.dbe.max(aggrField, criteria);
    }

    public Optional<BigDecimal> max(final String aggrField, final Map<String, Object> condition) {
        return this.dbe.max(aggrField, condition);
    }

    public Optional<BigDecimal> max(final String aggrField) {
        return this.dbe.max(aggrField);
    }

    public Optional<BigDecimal> max(final String aggrField, final JObject criteriaJ) {
        return this.dbe.max(aggrField, criteriaJ);
    }

    // ==================== 分组聚集（By）方法 ====================
    // ---- COUNT (groupBy)
    public ConcurrentMap<String, Long> countBy(final String groupBy) {
        return this.dbe.countBy(groupBy);
    }

    public ConcurrentMap<String, Long> countBy(final String field, final Object value, final String groupBy) {
        return this.dbe.countBy(field, value, groupBy);
    }

    public ConcurrentMap<String, Long> countBy(final QTree criteria, final String groupBy) {
        return this.dbe.countBy(criteria, groupBy);
    }

    public ConcurrentMap<String, Long> countBy(final Map<String, Object> condition, final String groupBy) {
        return this.dbe.countBy(condition, groupBy);
    }

    public ConcurrentMap<String, Long> countBy(final JObject criteriaJ, final String groupBy) {
        return this.dbe.countBy(criteriaJ, groupBy);
    }

    // ---- SUM (groupBy)
    public ConcurrentMap<String, BigDecimal> sumBy(final String aggrField, final String field, final Object value, final String groupBy) {
        return this.dbe.sumBy(aggrField, field, value, groupBy);
    }

    public ConcurrentMap<String, BigDecimal> sumBy(final String aggrField, final QTree criteria, final String groupBy) {
        return this.dbe.sumBy(aggrField, criteria, groupBy);
    }

    public ConcurrentMap<String, BigDecimal> sumBy(final String aggrField, final Map<String, Object> condition, final String groupBy) {
        return this.dbe.sumBy(aggrField, condition, groupBy);
    }

    public ConcurrentMap<String, BigDecimal> sumBy(final String aggrField, final String groupBy) {
        return this.dbe.sumBy(aggrField, groupBy);
    }

    public ConcurrentMap<String, BigDecimal> sumBy(final String aggrField, final JObject criteriaJ, final String groupBy) {
        return this.dbe.sumBy(aggrField, criteriaJ, groupBy);
    }

    // ---- AVG (groupBy)
    public ConcurrentMap<String, BigDecimal> avgBy(final String aggrField, final String field, final Object value, final String groupBy) {
        return this.dbe.avgBy(aggrField, field, value, groupBy);
    }

    public ConcurrentMap<String, BigDecimal> avgBy(final String aggrField, final QTree criteria, final String groupBy) {
        return this.dbe.avgBy(aggrField, criteria, groupBy);
    }

    public ConcurrentMap<String, BigDecimal> avgBy(final String aggrField, final Map<String, Object> condition, final String groupBy) {
        return this.dbe.avgBy(aggrField, condition, groupBy);
    }

    public ConcurrentMap<String, BigDecimal> avgBy(final String aggrField, final String groupBy) {
        return this.dbe.avgBy(aggrField, groupBy);
    }

    public ConcurrentMap<String, BigDecimal> avgBy(final String aggrField, final JObject criteriaJ, final String groupBy) {
        return this.dbe.avgBy(aggrField, criteriaJ, groupBy);
    }

    // ---- MIN (groupBy)
    public ConcurrentMap<String, BigDecimal> minBy(final String aggrField, final String field, final Object value, final String groupBy) {
        return this.dbe.minBy(aggrField, field, value, groupBy);
    }

    public ConcurrentMap<String, BigDecimal> minBy(final String aggrField, final QTree criteria, final String groupBy) {
        return this.dbe.minBy(aggrField, criteria, groupBy);
    }

    public ConcurrentMap<String, BigDecimal> minBy(final String aggrField, final Map<String, Object> condition, final String groupBy) {
        return this.dbe.minBy(aggrField, condition, groupBy);
    }

    public ConcurrentMap<String, BigDecimal> minBy(final String aggrField, final String groupBy) {
        return this.dbe.minBy(aggrField, groupBy);
    }

    public ConcurrentMap<String, BigDecimal> minBy(final String aggrField, final JObject criteriaJ, final String groupBy) {
        return this.dbe.minBy(aggrField, criteriaJ, groupBy);
    }

    // ---- MAX (groupBy)
    public ConcurrentMap<String, BigDecimal> maxBy(final String aggrField, final String field, final Object value, final String groupBy) {
        return this.dbe.maxBy(aggrField, field, value, groupBy);
    }

    public ConcurrentMap<String, BigDecimal> maxBy(final String aggrField, final QTree criteria, final String groupBy) {
        return this.dbe.maxBy(aggrField, criteria, groupBy);
    }

    public ConcurrentMap<String, BigDecimal> maxBy(final String aggrField, final String groupBy) {
        return this.dbe.maxBy(aggrField, groupBy);
    }

    public ConcurrentMap<String, BigDecimal> maxBy(final String aggrField, final Map<String, Object> condition, final String groupBy) {
        return this.dbe.maxBy(aggrField, condition, groupBy);
    }

    public ConcurrentMap<String, BigDecimal> maxBy(final String aggrField, final JObject criteriaJ, final String groupBy) {
        return this.dbe.maxBy(aggrField, criteriaJ, groupBy);
    }

    // ===================== 快捷 By（aggrGroup = aggrField = groupBy） =====================
    // ---- SUM
    public ConcurrentMap<String, BigDecimal> sumBy(final String aggrGroup, final String field, final Object value) {
        return this.dbe.sumBy(aggrGroup, field, value);
    }

    public ConcurrentMap<String, BigDecimal> sumBy(final String aggrGroup, final QTree criteria) {
        return this.dbe.sumBy(aggrGroup, criteria);
    }

    public ConcurrentMap<String, BigDecimal> sumBy(final String aggrGroup, final Map<String, Object> condition) {
        return this.dbe.sumBy(aggrGroup, condition);
    }

    public ConcurrentMap<String, BigDecimal> sumBy(final String aggrGroup, final JObject criteriaJ) {
        return this.dbe.sumBy(aggrGroup, criteriaJ);
    }

    // ---- AVG
    public ConcurrentMap<String, BigDecimal> avgBy(final String aggrGroup, final String field, final Object value) {
        return this.dbe.avgBy(aggrGroup, field, value);
    }

    public ConcurrentMap<String, BigDecimal> avgBy(final String aggrGroup, final QTree criteria) {
        return this.dbe.avgBy(aggrGroup, criteria);
    }

    public ConcurrentMap<String, BigDecimal> avgBy(final String aggrGroup, final Map<String, Object> condition) {
        return this.dbe.avgBy(aggrGroup, condition);
    }

    public ConcurrentMap<String, BigDecimal> avgBy(final String aggrGroup, final JObject criteriaJ) {
        return this.dbe.avgBy(aggrGroup, criteriaJ);
    }

    // ---- MIN
    public ConcurrentMap<String, BigDecimal> minBy(final String aggrGroup, final String field, final Object value) {
        return this.dbe.minBy(aggrGroup, field, value);
    }

    public ConcurrentMap<String, BigDecimal> minBy(final String aggrGroup, final QTree criteria) {
        return this.dbe.minBy(aggrGroup, criteria);
    }

    public ConcurrentMap<String, BigDecimal> minBy(final String aggrGroup, final Map<String, Object> condition) {
        return this.dbe.minBy(aggrGroup, condition);
    }

    public ConcurrentMap<String, BigDecimal> minBy(final String aggrGroup, final JObject criteriaJ) {
        return this.dbe.minBy(aggrGroup, criteriaJ);
    }

    // ---- MAX
    public ConcurrentMap<String, BigDecimal> maxBy(final String aggrGroup, final String field, final Object value) {
        return this.dbe.maxBy(aggrGroup, field, value);
    }

    public ConcurrentMap<String, BigDecimal> maxBy(final String aggrGroup, final QTree criteria) {
        return this.dbe.maxBy(aggrGroup, criteria);
    }

    public ConcurrentMap<String, BigDecimal> maxBy(final String aggrGroup, final Map<String, Object> condition) {
        return this.dbe.maxBy(aggrGroup, condition);
    }

    public ConcurrentMap<String, BigDecimal> maxBy(final String aggrGroup, final JObject criteriaJ) {
        return this.dbe.maxBy(aggrGroup, criteriaJ);
    }

    public boolean findExist(final Serializable id) {
        return this.dbe.findExist(id);
    }

    public boolean findExist(final QTree criteria) {
        return this.dbe.findExist(criteria);
    }

    public boolean findExist(final JObject criteriaJ) {
        return this.dbe.findExist(criteriaJ);
    }

    // ==================== findOneAsync 方法 ====================
    public Optional<T> findOne(final Serializable id) {
        return this.dbe.findOne(id);
    }

    public Optional<T> findOne(final String field, final Object value) {
        return this.dbe.findOne(field, value);
    }

    public Optional<T> findOne(final QTree criteria) {
        return this.dbe.findOne(criteria);
    }

    public Optional<T> findOne(final JObject criteriaJ) {
        return this.dbe.findOne(criteriaJ);
    }

    public Optional<T> findOne(final Map<String, Object> condition) {
        return this.dbe.findOne(condition);
    }

    // ==================== findManyAsync 方法 ====================
    public List<T> findMany(final Serializable... ids) {
        return this.dbe.findMany(ids);
    }

    public List<T> findMany(final String field, final Object value) {
        return this.dbe.findMany(field, value);
    }

    public List<T> findMany(final QTree criteria) {
        return this.dbe.findMany(criteria);
    }

    public List<T> findMany(final JObject criteriaJ) {
        return this.dbe.findMany(criteriaJ);
    }

    public List<T> findMany(final Map<String, Object> condition) {
        return this.dbe.findMany(condition);
    }

    // ==================== findFull 方法 ====================
    public List<T> findFull(final QQuery query) {
        return this.dbe.findFull(query);
    }

    public List<T> findFull(final JObject queryJ) {
        return this.dbe.findFull(queryJ);
    }

    // ==================== findPageAsync 方法 ====================
    public Pagination<T> findPage(final QQuery query) {
        return this.dbe.findPage(query);
    }

    public Pagination<T> findPage(final JObject queryJ) {
        return this.dbe.findPage(queryJ);
    }

    // ==================== findAllAsync 方法 ====================
    public List<T> findAll() {
        return this.dbe.findAll();
    }

    // ==================== findGroupBy 方法 ====================
    public <K> Map<K, List<T>> findGroupBy(final String groupBy) {
        return this.dbe.findGroupBy(groupBy);
    }

    public <K> Map<K, List<T>> findGroupBy(final QTree criteria, final String groupBy) {
        return this.dbe.findGroupBy(criteria, groupBy);
    }

    public <K> Map<K, List<T>> findGroupBy(final Map<String, Object> map, final String groupBy) {
        return this.dbe.findGroupBy(map, groupBy);
    }

    public <K> Map<K, List<T>> findGroupBy(final JObject criteriaJ, final String groupBy) {
        return this.dbe.findGroupBy(criteriaJ, groupBy);
    }

    public <K> Map<K, List<T>> findGroupBy(final String field, final Object value, final String groupBy) {
        return this.dbe.findGroupBy(field, value, groupBy);
    }

    // ==================== findMap 方法 ====================
    public List<T> findManyBy(final JObject mapJ) {
        return this.dbe.findManyBy(mapJ);
    }

    // ==================== findManyIn 方法 ====================
    public List<T> findManyIn(final String field, final Object... values) {
        return this.dbe.findManyIn(field, values);
    }

    public List<T> findManyIn(final String field, final List<?> values) {
        return this.dbe.findManyIn(field, values);
    }

    // ==================== CRUD 方法 ====================
    public T create(final T entity) {
        return this.dbe.create(entity);
    }

    public List<T> create(final List<T> entities, final int batchSize) {
        return this.dbe.create(entities, batchSize);
    }

    public List<T> create(final List<T> entities) {
        return this.dbe.create(entities);
    }

    public T update(final T entity) {
        return this.dbe.update(entity);
    }

    public List<T> update(final List<T> entities, final int batchSize) {
        return this.dbe.update(entities, batchSize);
    }

    public List<T> update(final List<T> entities) {
        return this.dbe.update(entities);
    }

    public T save(final T entity) {
        return this.dbe.save(entity);
    }

    public List<T> save(final List<T> entities, final int batchSize) {
        return this.dbe.save(entities, batchSize);
    }

    public List<T> save(final List<T> entities) {
        return this.dbe.save(entities);
    }

    public T remove(final T entity) {
        return this.dbe.remove(entity);
    }

    public List<T> remove(final List<T> entities, final int batchSize) {
        return this.dbe.remove(entities, batchSize);
    }

    public List<T> remove(final List<T> entities) {
        return this.dbe.remove(entities);
    }

    // ==================== CRUD Advanced 方法 ====================
    public boolean removeBy(final Serializable id) {
        return this.dbe.removeBy(id);
    }

    public boolean removeBy(final String field, final Object value) {
        return this.dbe.removeBy(field, value);
    }

    public boolean removeBy(final QTree criteria) {
        return this.dbe.removeBy(criteria);
    }

    public boolean removeBy(final Map<String, Object> map) {
        return this.dbe.removeBy(map);
    }

    public boolean removeBy(final JObject criteriaJ) {
        return this.dbe.removeBy(criteriaJ);
    }

    public T updateBy(final Serializable id, final T latest) {
        return this.dbe.updateBy(id, latest);
    }

    public T updateBy(final String field, final Object value, final T latest) {
        return this.dbe.updateBy(field, value, latest);
    }

    public T updateBy(final QTree criteria, final T latest) {
        return this.dbe.updateBy(criteria, latest);
    }

    public T updateBy(final Map<String, Object> map, final T latest) {
        return this.dbe.updateBy(map, latest);
    }

    public T updateBy(final JObject criteriaJ, final T latest) {
        return this.dbe.updateBy(criteriaJ, latest);
    }

    public T saveBy(final Serializable id, final T latest) {
        return this.dbe.saveBy(id, latest);
    }

    public T saveBy(final String field, final Object value, final T latest) {
        return this.dbe.saveBy(field, value, latest);
    }

    public T saveBy(final Map<String, Object> map, final T latest) {
        return this.dbe.saveBy(map, latest);
    }

    public T saveBy(final QTree criteria, final T latest) {
        return this.dbe.saveBy(criteria, latest);
    }

    public T saveBy(final JObject criteriaJ, final T latest) {
        return this.dbe.saveBy(criteriaJ, latest);
    }
    // endregion
}

package io.r2mo.vertx.jooq;

import io.r2mo.base.dbe.DBS;
import io.r2mo.base.dbe.syntax.QQuery;
import io.r2mo.base.dbe.syntax.QTree;
import io.r2mo.typed.common.Pagination;
import io.r2mo.typed.json.JObject;
import io.vertx.core.Future;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

/**
 * 抽象异步方法集合，此处所有方法都是异步非阻塞的
 * <pre>
 *     1. 方法签名全是异步
 *     2. 返回值全部是 {@link Future}
 *     3. 支持高阶的三种查询对象
 *        - {@link QTree}
 *        - {@link QQuery}
 *        - {@link JObject}
 * </pre>
 */
class DBExFuture<T> extends DBExCommon<T> {
    protected DBExFuture(final Class<T> daoCls, final DBS dbs) {
        super(daoCls, dbs);
    }

    // ========== Aggregations ==========
    // ---- COUNT (single)
    public Future<Long> countAsync() {
        return this.dbeAsync.countAsync();
    }

    public Future<Long> countAsync(final String field, final Object value) {
        return this.dbeAsync.countAsync(field, value);
    }

    public Future<Long> countAsync(final QTree criteria) {
        return this.dbeAsync.countAsync(criteria);
    }

    public Future<Long> countAsync(final Map<String, Object> condition) {
        return this.dbeAsync.countAsync(condition);
    }

    public Future<Long> countAsync(final JObject criteriaJ) {
        return this.dbeAsync.countAsync(criteriaJ);
    }

    // ---- SUM (single)
    public Future<BigDecimal> sumAsync(final String aggrField, final String field, final Object value) {
        return this.dbeAsync.sumAsync(aggrField, field, value);
    }

    public Future<BigDecimal> sumAsync(final String aggrField, final QTree criteria) {
        return this.dbeAsync.sumAsync(aggrField, criteria);
    }

    public Future<BigDecimal> sumAsync(final String aggrField, final Map<String, Object> condition) {
        return this.dbeAsync.sumAsync(aggrField, condition);
    }

    public Future<BigDecimal> sumAsync(final String aggrField) {
        return this.dbeAsync.sumAsync(aggrField);
    }

    public Future<BigDecimal> sumAsync(final String aggrField, final JObject criteriaJ) {
        return this.dbeAsync.sumAsync(aggrField, criteriaJ);
    }

    // ---- AVG (single)
    public Future<BigDecimal> avgAsync(final String aggrField, final String field, final Object value) {
        return this.dbeAsync.avgAsync(aggrField, field, value);
    }

    public Future<BigDecimal> avgAsync(final String aggrField, final QTree criteria) {
        return this.dbeAsync.avgAsync(aggrField, criteria);
    }

    public Future<BigDecimal> avgAsync(final String aggrField, final Map<String, Object> condition) {
        return this.dbeAsync.avgAsync(aggrField, condition);
    }

    public Future<BigDecimal> avgAsync(final String aggrField) {
        return this.dbeAsync.avgAsync(aggrField);
    }

    public Future<BigDecimal> avgAsync(final String aggrField, final JObject criteriaJ) {
        return this.dbeAsync.avgAsync(aggrField, criteriaJ);
    }

    // ---- MIN (single)
    public Future<BigDecimal> minAsync(final String aggrField, final String field, final Object value) {
        return this.dbeAsync.minAsync(aggrField, field, value);
    }

    public Future<BigDecimal> minAsync(final String aggrField, final QTree criteria) {
        return this.dbeAsync.minAsync(aggrField, criteria);
    }

    public Future<BigDecimal> minAsync(final String aggrField, final Map<String, Object> condition) {
        return this.dbeAsync.minAsync(aggrField, condition);
    }

    public Future<BigDecimal> minAsync(final String aggrField) {
        return this.dbeAsync.minAsync(aggrField);
    }

    public Future<BigDecimal> minAsync(final String aggrField, final JObject criteriaJ) {
        return this.dbeAsync.minAsync(aggrField, criteriaJ);
    }

    // ---- MAX (single)
    public Future<BigDecimal> maxAsync(final String aggrField, final String field, final Object value) {
        return this.dbeAsync.maxAsync(aggrField, field, value);
    }

    public Future<BigDecimal> maxAsync(final String aggrField, final QTree criteria) {
        return this.dbeAsync.maxAsync(aggrField, criteria);
    }

    public Future<BigDecimal> maxAsync(final String aggrField, final Map<String, Object> condition) {
        return this.dbeAsync.maxAsync(aggrField, condition);
    }

    public Future<BigDecimal> maxAsync(final String aggrField) {
        return this.dbeAsync.maxAsync(aggrField);
    }

    public Future<BigDecimal> maxAsync(final String aggrField, final JObject criteriaJ) {
        return this.dbeAsync.maxAsync(aggrField, criteriaJ);
    }

    // ---- COUNT By (grouped)
    public Future<ConcurrentMap<String, Long>> countByAsync(final String groupBy) {
        return this.dbeAsync.countByAsync(groupBy);
    }

    public Future<ConcurrentMap<String, Long>> countByAsync(final String field, final Object value, final String groupBy) {
        return this.dbeAsync.countByAsync(field, value, groupBy);
    }

    public Future<ConcurrentMap<String, Long>> countByAsync(final QTree criteria, final String groupBy) {
        return this.dbeAsync.countByAsync(criteria, groupBy);
    }

    public Future<ConcurrentMap<String, Long>> countByAsync(final Map<String, Object> condition, final String groupBy) {
        return this.dbeAsync.countByAsync(condition, groupBy);
    }

    public Future<ConcurrentMap<String, Long>> countByAsync(final JObject criteriaJ, final String groupBy) {
        return this.dbeAsync.countByAsync(criteriaJ, groupBy);
    }

    // ---- SUM By (grouped)
    public Future<ConcurrentMap<String, BigDecimal>> sumByAsync(final String aggrField, final String field, final Object value, final String groupBy) {
        return this.dbeAsync.sumByAsync(aggrField, field, value, groupBy);
    }

    public Future<ConcurrentMap<String, BigDecimal>> sumByAsync(final String aggrField, final QTree criteria, final String groupBy) {
        return this.dbeAsync.sumByAsync(aggrField, criteria, groupBy);
    }

    public Future<ConcurrentMap<String, BigDecimal>> sumByAsync(final String aggrField, final Map<String, Object> condition, final String groupBy) {
        return this.dbeAsync.sumByAsync(aggrField, condition, groupBy);
    }

    public Future<ConcurrentMap<String, BigDecimal>> sumByAsync(final String aggrField, final String groupBy) {
        return this.dbeAsync.sumByAsync(aggrField, groupBy);
    }

    public Future<ConcurrentMap<String, BigDecimal>> sumByAsync(final String aggrField, final JObject criteriaJ, final String groupBy) {
        return this.dbeAsync.sumByAsync(aggrField, criteriaJ, groupBy);
    }

    // ---- AVG By (grouped)
    public Future<ConcurrentMap<String, BigDecimal>> avgByAsync(final String aggrField, final String field, final Object value, final String groupBy) {
        return this.dbeAsync.avgByAsync(aggrField, field, value, groupBy);
    }

    public Future<ConcurrentMap<String, BigDecimal>> avgByAsync(final String aggrField, final QTree criteria, final String groupBy) {
        return this.dbeAsync.avgByAsync(aggrField, criteria, groupBy);
    }

    public Future<ConcurrentMap<String, BigDecimal>> avgByAsync(final String aggrField, final Map<String, Object> condition, final String groupBy) {
        return this.dbeAsync.avgByAsync(aggrField, condition, groupBy);
    }

    public Future<ConcurrentMap<String, BigDecimal>> avgByAsync(final String aggrField, final String groupBy) {
        return this.dbeAsync.avgByAsync(aggrField, groupBy);
    }

    public Future<ConcurrentMap<String, BigDecimal>> avgByAsync(final String aggrField, final JObject criteriaJ, final String groupBy) {
        return this.dbeAsync.avgByAsync(aggrField, criteriaJ, groupBy);
    }

    // ---- MIN By (grouped)
    public Future<ConcurrentMap<String, BigDecimal>> minByAsync(final String aggrField, final String field, final Object value, final String groupBy) {
        return this.dbeAsync.minByAsync(aggrField, field, value, groupBy);
    }

    public Future<ConcurrentMap<String, BigDecimal>> minByAsync(final String aggrField, final QTree criteria, final String groupBy) {
        return this.dbeAsync.minByAsync(aggrField, criteria, groupBy);
    }

    public Future<ConcurrentMap<String, BigDecimal>> minByAsync(final String aggrField, final Map<String, Object> condition, final String groupBy) {
        return this.dbeAsync.minByAsync(aggrField, condition, groupBy);
    }

    public Future<ConcurrentMap<String, BigDecimal>> minByAsync(final String aggrField, final String groupBy) {
        return this.dbeAsync.minByAsync(aggrField, groupBy);
    }

    public Future<ConcurrentMap<String, BigDecimal>> minByAsync(final String aggrField, final JObject criteriaJ, final String groupBy) {
        return this.dbeAsync.minByAsync(aggrField, criteriaJ, groupBy);
    }

    // ---- MAX By (grouped)
    public Future<ConcurrentMap<String, BigDecimal>> maxByAsync(final String aggrField, final String field, final Object value, final String groupBy) {
        return this.dbeAsync.maxByAsync(aggrField, field, value, groupBy);
    }

    public Future<ConcurrentMap<String, BigDecimal>> maxByAsync(final String aggrField, final QTree criteria, final String groupBy) {
        return this.dbeAsync.maxByAsync(aggrField, criteria, groupBy);
    }

    public Future<ConcurrentMap<String, BigDecimal>> maxByAsync(final String aggrField, final Map<String, Object> condition, final String groupBy) {
        return this.dbeAsync.maxByAsync(aggrField, condition, groupBy);
    }

    public Future<ConcurrentMap<String, BigDecimal>> maxByAsync(final String aggrField, final String groupBy) {
        return this.dbeAsync.maxByAsync(aggrField, groupBy);
    }

    public Future<ConcurrentMap<String, BigDecimal>> maxByAsync(final String aggrField, final JObject criteriaJ, final String groupBy) {
        return this.dbeAsync.maxByAsync(aggrField, criteriaJ, groupBy);
    }

    // ---- 快捷 By（aggrGroup = aggrField = groupBy）
    public Future<ConcurrentMap<String, BigDecimal>> sumByAsync(final String aggrGroup, final String field, final Object value) {
        return this.dbeAsync.sumByAsync(aggrGroup, field, value);
    }

    public Future<ConcurrentMap<String, BigDecimal>> sumByAsync(final String aggrGroup, final QTree criteria) {
        return this.dbeAsync.sumByAsync(aggrGroup, criteria);
    }

    public Future<ConcurrentMap<String, BigDecimal>> sumByAsync(final String aggrGroup, final Map<String, Object> condition) {
        return this.dbeAsync.sumByAsync(aggrGroup, condition);
    }

    public Future<ConcurrentMap<String, BigDecimal>> sumByAsync(final String aggrGroup, final JObject criteriaJ) {
        return this.dbeAsync.sumByAsync(aggrGroup, criteriaJ);
    }

    public Future<ConcurrentMap<String, BigDecimal>> avgByAsync(final String aggrGroup, final String field, final Object value) {
        return this.dbeAsync.avgByAsync(aggrGroup, field, value);
    }

    public Future<ConcurrentMap<String, BigDecimal>> avgByAsync(final String aggrGroup, final QTree criteria) {
        return this.dbeAsync.avgByAsync(aggrGroup, criteria);
    }

    public Future<ConcurrentMap<String, BigDecimal>> avgByAsync(final String aggrGroup, final Map<String, Object> condition) {
        return this.dbeAsync.avgByAsync(aggrGroup, condition);
    }

    public Future<ConcurrentMap<String, BigDecimal>> avgByAsync(final String aggrGroup, final JObject criteriaJ) {
        return this.dbeAsync.avgByAsync(aggrGroup, criteriaJ);
    }

    public Future<ConcurrentMap<String, BigDecimal>> minByAsync(final String aggrGroup, final String field, final Object value) {
        return this.dbeAsync.minByAsync(aggrGroup, field, value);
    }

    public Future<ConcurrentMap<String, BigDecimal>> minByAsync(final String aggrGroup, final QTree criteria) {
        return this.dbeAsync.minByAsync(aggrGroup, criteria);
    }

    public Future<ConcurrentMap<String, BigDecimal>> minByAsync(final String aggrGroup, final Map<String, Object> condition) {
        return this.dbeAsync.minByAsync(aggrGroup, condition);
    }

    public Future<ConcurrentMap<String, BigDecimal>> minByAsync(final String aggrGroup, final JObject criteriaJ) {
        return this.dbeAsync.minByAsync(aggrGroup, criteriaJ);
    }

    public Future<ConcurrentMap<String, BigDecimal>> maxByAsync(final String aggrGroup, final String field, final Object value) {
        return this.dbeAsync.maxByAsync(aggrGroup, field, value);
    }

    public Future<ConcurrentMap<String, BigDecimal>> maxByAsync(final String aggrGroup, final QTree criteria) {
        return this.dbeAsync.maxByAsync(aggrGroup, criteria);
    }

    public Future<ConcurrentMap<String, BigDecimal>> maxByAsync(final String aggrGroup, final Map<String, Object> condition) {
        return this.dbeAsync.maxByAsync(aggrGroup, condition);
    }

    public Future<ConcurrentMap<String, BigDecimal>> maxByAsync(final String aggrGroup, final JObject criteriaJ) {
        return this.dbeAsync.maxByAsync(aggrGroup, criteriaJ);
    }

    // ========== Queries ==========
    public Future<List<T>> findAllAsync() {
        return this.dbeAsync.findAllAsync();
    }

    public Future<T> findOneAsync(final Serializable id) {
        return this.dbeAsync.findOneAsync(id);
    }

    public Future<T> findOneAsync(final String field, final Object value) {
        return this.dbeAsync.findOneAsync(field, value);
    }

    public Future<T> findOneAsync(final QTree criteria) {
        return this.dbeAsync.findOneAsync(criteria);
    }

    public Future<T> findOneAsync(final JObject criteriaJ) {
        return this.dbeAsync.findOneAsync(criteriaJ);
    }

    public Future<T> findOneAsync(final Map<String, Object> condition) {
        return this.dbeAsync.findOneAsync(condition);
    }

    public Future<List<T>> findManyAsync(final Serializable... ids) {
        return this.dbeAsync.findManyAsync(ids);
    }

    public Future<List<T>> findManyAsync(final String field, final Object value) {
        return this.dbeAsync.findManyAsync(field, value);
    }

    public Future<List<T>> findManyAsync(final QTree criteria) {
        return this.dbeAsync.findManyAsync(criteria);
    }

    public Future<List<T>> findManyAsync(final JObject criteriaJ) {
        return this.dbeAsync.findManyAsync(criteriaJ);
    }

    public Future<List<T>> findManyAsync(final Map<String, Object> condition) {
        return this.dbeAsync.findManyAsync(condition);
    }

    public Future<List<T>> findFullAsync(final QQuery query) {
        return this.dbeAsync.findFullAsync(query);
    }

    public Future<List<T>> findFullAsync(final JObject queryJ) {
        return this.dbeAsync.findFullAsync(queryJ);
    }

    public Future<Pagination<T>> findPageAsync(final QQuery query) {
        return this.dbeAsync.findPageAsync(query);
    }

    public Future<Pagination<T>> findPageAsync(final JObject queryJ) {
        return this.dbeAsync.findPageAsync(queryJ);
    }

    // Java 侧分组
    public <K> Future<Map<K, List<T>>> findGroupByAsync(final String groupBy) {
        return this.dbeAsync.findGroupByAsync(groupBy);
    }

    public <K> Future<Map<K, List<T>>> findGroupByAsync(final QTree criteria, final String groupBy) {
        return this.dbeAsync.findGroupByAsync(criteria, groupBy);
    }

    public <K> Future<Map<K, List<T>>> findGroupByAsync(final Map<String, Object> map, final String groupBy) {
        return this.dbeAsync.findGroupByAsync(map, groupBy);
    }

    public <K> Future<Map<K, List<T>>> findGroupByAsync(final JObject criteriaJ, final String groupBy) {
        return this.dbeAsync.findGroupByAsync(criteriaJ, groupBy);
    }

    public <K> Future<Map<K, List<T>>> findGroupByAsync(final String field, final Object value, final String groupBy) {
        return this.dbeAsync.findGroupByAsync(field, value, groupBy);
    }

    public Future<List<T>> findManyByAsync(final JObject mapJ) {
        return this.dbeAsync.findManyByAsync(mapJ);
    }

    public Future<List<T>> findManyInAsync(final String field, final Object... values) {
        return this.dbeAsync.findManyInAsync(field, values);
    }

    public Future<List<T>> findManyInAsync(final String field, final List<?> values) {
        return this.dbeAsync.findManyInAsync(field, values);
    }

    // ========== CRUD ==========
    public Future<T> createAsync(final T entity) {
        return this.dbeAsync.createAsync(entity);
    }

    public Future<List<T>> createAsync(final List<T> entities) {
        return this.dbeAsync.createAsync(entities);
    }

    public Future<T> updateAsync(final T entity) {
        return this.dbeAsync.updateAsync(entity);
    }

    public Future<List<T>> updateAsync(final List<T> entities) {
        return this.dbeAsync.updateAsync(entities);
    }

    public Future<T> saveAsync(final T entity) {
        return this.dbeAsync.saveAsync(entity);
    }

    public Future<List<T>> saveAsync(final List<T> entities) {
        return this.dbeAsync.saveAsync(entities);
    }

    public Future<T> removeAsync(final T entity) {
        return this.dbeAsync.removeAsync(entity);
    }

    public Future<List<T>> removeAsync(final List<T> entities) {
        return this.dbeAsync.removeAsync(entities);
    }

    // ========== CRUD Advanced ==========
    public Future<Boolean> removeByAsync(final Serializable id) {
        return this.dbeAsync.removeByAsync(id);
    }

    public Future<Boolean> removeByAsync(final String field, final Object value) {
        return this.dbeAsync.removeByAsync(field, value);
    }

    public Future<Boolean> removeByAsync(final QTree criteria) {
        return this.dbeAsync.removeByAsync(criteria);
    }

    public Future<Boolean> removeByAsync(final Map<String, Object> map) {
        return this.dbeAsync.removeByAsync(map);
    }

    public Future<Boolean> removeByAsync(final JObject criteriaJ) {
        return this.dbeAsync.removeByAsync(criteriaJ);
    }

    public Future<T> updateByAsync(final Serializable id, final T latest) {
        return this.dbeAsync.updateByAsync(id, latest);
    }

    public Future<T> updateByAsync(final String field, final Object value, final T latest) {
        return this.dbeAsync.updateByAsync(field, value, latest);
    }

    public Future<T> updateByAsync(final QTree criteria, final T latest) {
        return this.dbeAsync.updateByAsync(criteria, latest);
    }

    public Future<T> updateByAsync(final Map<String, Object> map, final T latest) {
        return this.dbeAsync.updateByAsync(map, latest);
    }

    public Future<T> updateByAsync(final JObject criteriaJ, final T latest) {
        return this.dbeAsync.updateByAsync(criteriaJ, latest);
    }

    public Future<T> saveByAsync(final Serializable id, final T latest) {
        return this.dbeAsync.saveByAsync(id, latest);
    }

    public Future<T> saveByAsync(final String field, final Object value, final T latest) {
        return this.dbeAsync.saveByAsync(field, value, latest);
    }

    public Future<T> saveByAsync(final Map<String, Object> map, final T latest) {
        return this.dbeAsync.saveByAsync(map, latest);
    }

    public Future<T> saveByAsync(final QTree criteria, final T latest) {
        return this.dbeAsync.saveByAsync(criteria, latest);
    }

    public Future<T> saveByAsync(final JObject criteriaJ, final T latest) {
        return this.dbeAsync.saveByAsync(criteriaJ, latest);
    }
}

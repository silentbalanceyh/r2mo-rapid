package io.r2mo.vertx.dbe;

import io.r2mo.base.dbe.constant.OpType;
import io.r2mo.base.dbe.syntax.QQuery;
import io.r2mo.base.dbe.syntax.QTree;
import io.r2mo.base.util.R2MO;
import io.r2mo.typed.common.Pagination;
import io.r2mo.typed.json.JObject;
import io.vertx.core.Future;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author lang : 2025-10-19
 */
public abstract class AsyncDBE<QR, T, METADATA> extends AsyncDBEAggr<QR, T, METADATA> {

    protected AsyncDBE(final Class<T> entityCls, final METADATA meta) {
        super(entityCls, meta);
    }

    // ======================= findOne (Async) =======================
    public Future<T> findOneAsync(final Serializable id) {
        return this.qrOne.executeAsync(id);
    }

    public Future<T> findOneAsync(final String field, final Object value) {
        return this.qrOne.executeAsync(field, value);
    }

    public Future<T> findOneAsync(final QTree criteria) {
        return this.qrOne.executeAsync(criteria);
    }

    public Future<T> findOneAsync(final JObject criteriaJ) {
        return this.findOneAsync(QTree.of(criteriaJ));
    }

    public Future<T> findOneAsync(final Map<String, Object> condition) {
        return this.qrOne.executeAsync(condition);
    }

    // ======================= findMany (Async) =======================
    public Future<List<T>> findManyAsync(final Serializable... ids) {
        return this.qrMany.executeAsync(ids);
    }

    public Future<List<T>> findManyAsync(final String field, final Object value) {
        return this.qrMany.executeAsync(field, value);
    }

    public Future<List<T>> findManyAsync(final QTree criteria) {
        return this.qrMany.executeAsync(criteria);
    }

    public Future<List<T>> findManyAsync(final JObject criteriaJ) {
        return this.findManyAsync(QTree.of(criteriaJ));
    }

    public Future<List<T>> findManyAsync(final Map<String, Object> condition) {
        return this.qrMany.executeAsync(condition);
    }

    // ======================= findFull / findPage (Async) =======================
    public Future<List<T>> findFullAsync(final QQuery query) {
        return this.qrMany.executeAsync(query);
    }

    public Future<List<T>> findFullAsync(final JObject queryJ) {
        return this.findFullAsync(QQuery.of(queryJ));
    }

    public Future<Pagination<T>> findPageAsync(final QQuery query) {
        return this.opVary.findPageAsync(query);
    }

    public Future<Pagination<T>> findPageAsync(final JObject queryJ) {
        return this.findPageAsync(QQuery.of(queryJ));
    }

    // ======================= findAll (Async) =======================
    public Future<List<T>> findAllAsync() {
        return this.opVary.findAllAsync();
    }

    // ======================= findGroupBy (Async, Java 侧分组) =======================
    public <K> Future<Map<K, List<T>>> findGroupByAsync(final String groupBy) {
        return this.findAllAsync().map(list -> R2MO.elementGroupBy(list, groupBy, this.entityCls));
    }

    public <K> Future<Map<K, List<T>>> findGroupByAsync(final QTree criteria, final String groupBy) {
        return this.findManyAsync(criteria).map(list -> R2MO.elementGroupBy(list, groupBy, this.entityCls));
    }

    public <K> Future<Map<K, List<T>>> findGroupByAsync(final Map<String, Object> map, final String groupBy) {
        return this.findManyAsync(map).map(list -> R2MO.elementGroupBy(list, groupBy, this.entityCls));
    }

    public <K> Future<Map<K, List<T>>> findGroupByAsync(final JObject criteriaJ, final String groupBy) {
        return this.findGroupByAsync(QTree.of(criteriaJ), groupBy);
    }

    public <K> Future<Map<K, List<T>>> findGroupByAsync(final String field, final Object value, final String groupBy) {
        return this.findManyAsync(field, value).map(list -> R2MO.elementGroupBy(list, groupBy, this.entityCls));
    }

    // ======================= findManyBy / findManyIn (Async) =======================
    public Future<List<T>> findManyByAsync(final JObject mapJ) {
        final QR condition = this.qrAnalyzer.where(mapJ.toMap());
        return this.opVary.findManyAsync(condition);
    }

    public Future<List<T>> findManyInAsync(final String field, final Object... values) {
        final QR condition = this.qrAnalyzer.whereIn(field, values);
        return this.opVary.findManyAsync(condition);
    }

    public Future<List<T>> findManyInAsync(final String field, final List<?> values) {
        return this.findManyInAsync(field, values.toArray());
    }

    // ======================= CRUD (Async) =======================
    public Future<T> createAsync(final T entity) {
        return this.opDb.executeAsync(entity, OpType.CREATE);
    }

    public Future<List<T>> createAsync(final List<T> entities) {
        return this.opDb.executeAsync(entities, OpType.CREATE); // 无 pBatchSize()
    }

    public Future<T> updateAsync(final T entity) {
        return this.opDb.executeAsync(entity, OpType.UPDATE);
    }

    public Future<List<T>> updateAsync(final List<T> entities) {
        return this.opDb.executeAsync(entities, OpType.UPDATE); // 无 pBatchSize()
    }

    public Future<T> saveAsync(final T entity) {
        return this.opDb.executeAsync(entity, OpType.SAVE);
    }

    public Future<List<T>> saveAsync(final List<T> entities) {
        return this.opDb.executeAsync(entities, OpType.SAVE); // 无 pBatchSize()
    }

    public Future<T> removeAsync(final T entity) {
        return this.opDb.executeAsync(entity, OpType.REMOVE);
    }

    public Future<List<T>> removeAsync(final List<T> entities) {
        return this.opDb.executeAsync(entities, OpType.REMOVE); // 无 pBatchSize()
    }

    // ======================= CRUD Advanced (Async) =======================
    public Future<Boolean> removeByAsync(final Serializable id) {
        return this.opVary.removeByIdAsync(id);
    }

    public Future<Boolean> removeByAsync(final String field, final Object value) {
        final QR condition = this.qrAnalyzer.where(field, value);
        return this.opVary.removeByAsync(condition);
    }

    public Future<Boolean> removeByAsync(final QTree criteria) {
        final QR condition = this.qrAnalyzer.where(criteria);
        return this.opVary.removeByAsync(condition);
    }

    public Future<Boolean> removeByAsync(final Map<String, Object> map) {
        final QR condition = this.qrAnalyzer.where(map);
        return this.opVary.removeByAsync(condition);
    }

    public Future<Boolean> removeByAsync(final JObject criteriaJ) {
        return this.removeByAsync(QTree.of(criteriaJ));
    }

    public Future<T> updateByAsync(final Serializable id, final T latest) {
        return this.opVary.updateAsync(this.findOneAsync(id), latest);
    }

    public Future<T> updateByAsync(final String field, final Object value, final T latest) {
        return this.opVary.updateAsync(this.findOneAsync(field, value), latest);
    }

    public Future<T> updateByAsync(final QTree criteria, final T latest) {
        return this.opVary.updateAsync(this.findOneAsync(criteria), latest);
    }

    public Future<T> updateByAsync(final Map<String, Object> map, final T latest) {
        return this.opVary.updateAsync(this.findOneAsync(map), latest);
    }

    public Future<T> updateByAsync(final JObject criteriaJ, final T latest) {
        return this.updateByAsync(QTree.of(criteriaJ), latest);
    }

    public Future<T> saveByAsync(final Serializable id, final T latest) {
        return this.opVary.saveAsync(this.findOneAsync(id), latest);
    }

    public Future<T> saveByAsync(final String field, final Object value, final T latest) {
        return this.opVary.saveAsync(this.findOneAsync(field, value), latest);
    }

    public Future<T> saveByAsync(final Map<String, Object> map, final T latest) {
        return this.opVary.saveAsync(this.findOneAsync(map), latest);
    }

    public Future<T> saveByAsync(final QTree criteria, final T latest) {
        return this.opVary.saveAsync(this.findOneAsync(criteria), latest);
    }

    public Future<T> saveByAsync(final JObject criteriaJ, final T latest) {
        return this.saveByAsync(QTree.of(criteriaJ), latest);
    }
}

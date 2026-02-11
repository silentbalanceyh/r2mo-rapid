package io.r2mo.vertx.jooq;

import io.r2mo.base.dbe.DBS;
import io.r2mo.base.dbe.syntax.QSorter;
import io.r2mo.typed.common.Pagination;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentMap;

/**
 * 综合同异步双模型，实现前置和后置的 JSON 映射处理
 * <pre>
 *     1. 后置使用日志：After 注释
 *     2. 前置使用日志：Before 注释
 * </pre>
 * 此处的参数查询对象没有高阶对象，都是位于底层的
 * <pre>
 *     1. {@link JsonArray}
 *     2. {@link JsonObject}
 * </pre>
 *
 * @author lang : 2025-10-20
 */
class DBExJson<T> extends DBExFuture<T> {
    private final DBVector<T> mapped;

    protected DBExJson(final Class<T> daoCls, final DBS dbs) {
        super(daoCls, dbs);
        this.mapped = DBVector.of(this.metadata());
    }

    @SuppressWarnings("all")
    protected DBVector<T> mapped() {
        return (DBVector<T>) this.mapped;
    }

    // 没有输入，所以只有两种形态
    public JsonArray findAllJ() {
        return this.mapped().many(this.dbe.findAll());
    }

    public Future<JsonArray> findAllJAsync() {
        return this.findAllAsync().compose(this.mapped()::manyTo);
    }

    public Future<JsonObject> findPageAsyncJ(final JsonObject query) {
        final JsonObject mappedQuery = this.mapped().mapQuery(query);
        return this.findPageAsync(this.wrap(mappedQuery)).compose(this::mapPageAsync);
    }

    public Future<Pagination<T>> findPageAsync(final JsonObject query) {
        final JsonObject mappedQuery = this.mapped().mapQuery(query);
        return this.findPageAsync(this.wrap(mappedQuery));
    }

    public JsonObject findPageJ(final JsonObject query) {
        final JsonObject mappedQuery = this.mapped().mapQuery(query);
        return this.mapPage(this.findPage(this.wrap(mappedQuery)));
    }

    public Pagination<T> findPage(final JsonObject query) {
        final JsonObject mappedQuery = this.mapped().mapQuery(query);
        return this.findPage(this.wrap(mappedQuery));
    }

    /**
     * 父类新方法，用于重写 Page 追加 mapped() 组件的新逻辑，用于解决带有 pojo/xxx.yaml 映射文件的场景处理
     * 正常逻辑下 {@link DBVector} 会在执行过程中做前置和后置的处理，确保输入输出都能正确映射到 JsonObject/JsonArray 上，但 mapPage 比较特殊，
     * 它在上层会直接返回 {@link List<T>} 的对象，若不重写此方法则无法执行映射逻辑
     * <pre>
     *     mapping:
     *       dbField: jsonField
     * </pre>
     * 上述格式中
     * <pre>
     *     dbField - 数据库实体字段
     *     jsonField - 外部接口字段（即 JsonObject 中的字段）
     * </pre>
     *
     * @param pageList 分页列表
     * @return 映射后的 JsonArray
     */
    @Override
    protected JsonArray mapPage(final List<T> pageList) {
        return this.mapped().many(pageList);
    }

    public JsonArray findManyJ(final String field, final Object value) {
        return this.mapped().many(this.findMany(field, value));
    }

    public Future<JsonArray> findManyAsyncJ(final String field, final Object value) {
        return this.findManyAsync(field, value).compose(this.mapped()::manyTo);
    }

    public JsonArray findManyInJ(final String field, final Collection<?> values) {
        return this.mapped().many(this.findManyIn(field, values));
    }

    public JsonArray findManyInJ(final String field, final Object... values) {
        return this.mapped().many(this.findManyIn(field, values));
    }

    public Future<JsonArray> findManyInAsyncJ(final String field, final Collection<?> values) {
        return this.findManyInAsync(field, values).compose(this.mapped()::manyTo);
    }

    public Future<JsonArray> findManyInAsyncJ(final String field, final Object... values) {
        return this.findManyInAsync(field, values).compose(this.mapped()::manyTo);
    }

    public Future<List<T>> findManyAsync(final JsonObject criteria) {
        final JsonObject mappedCriteria = this.mapped().mapCriteria(criteria);
        return this.findManyAsync(this.wrapTree(mappedCriteria));
    }

    public Future<JsonArray> findManyAsyncJ(final JsonObject criteria) {
        final JsonObject mappedCriteria = this.mapped().mapCriteria(criteria);
        return this.findManyAsync(this.wrapTree(mappedCriteria)).compose(this.mapped()::manyTo);
    }

    public Future<List<T>> findManyAsync(final JsonObject criteria, final QSorter sorter) {
        final JsonObject mappedCriteria = this.mapped().mapCriteria(criteria);
        return this.findManyAsync(this.wrapTree(mappedCriteria, sorter));
    }

    public Future<JsonArray> findManyAsyncJ(final JsonObject criteria, final QSorter sorter) {
        final JsonObject mappedCriteria = this.mapped().mapCriteria(criteria);
        return this.findManyAsync(this.wrapTree(mappedCriteria, sorter)).compose(this.mapped()::manyTo);
    }

    public List<T> findMany(final JsonObject criteria) {
        final JsonObject mappedCriteria = this.mapped().mapCriteria(criteria);
        return this.findMany(this.wrapTree(mappedCriteria));
    }

    public List<T> findMany(final JsonObject criteria, final QSorter sorter) {
        final JsonObject mappedCriteria = this.mapped().mapCriteria(criteria);
        return this.findMany(this.wrapTree(mappedCriteria, sorter));
    }

    public JsonArray findManyJ(final JsonObject criteria) {
        final JsonObject mappedCriteria = this.mapped().mapCriteria(criteria);
        return this.mapped().many(this.findMany(this.wrapTree(mappedCriteria)));
    }

    public JsonArray findManyJ(final JsonObject criteria, final QSorter sorter) {
        final JsonObject mappedCriteria = this.mapped().mapCriteria(criteria);
        return this.mapped().many(this.findMany(this.wrapTree(mappedCriteria, sorter)));
    }

    public Future<JsonObject> findOneAsyncJ(final Serializable id) {
        return this.findOneAsync(id).compose(this.mapped()::oneTo);
    }

    public JsonObject findOneJ(final Serializable id) {
        return this.mapped().one(this.findOne(id).orElse(null));
    }

    public Future<JsonObject> findOneAsyncJ(final String field, final Object value) {
        return this.findOneAsync(field, value).compose(this.mapped()::oneTo);
    }

    public JsonObject findOneJ(final String field, final Object value) {
        return this.mapped().one(this.findOne(field, value).orElse(null));
    }

    public Future<T> findOneAsync(final JsonObject criteria) {
        final JsonObject mappedCriteria = this.mapped().mapCriteria(criteria);
        return this.findOneAsync(this.wrapTree(mappedCriteria));
    }

    public Future<JsonObject> findOneAsyncJ(final JsonObject criteria) {
        final JsonObject mappedCriteria = this.mapped().mapCriteria(criteria);
        return this.findOneAsync(this.wrapTree(mappedCriteria)).compose(this.mapped()::oneTo);
    }

    public T findOne(final JsonObject criteria) {
        final JsonObject mappedCriteria = this.mapped().mapCriteria(criteria);
        return this.findOne(this.wrapTree(mappedCriteria)).orElse(null);
    }

    public JsonObject findOneJ(final JsonObject criteria) {
        final JsonObject mappedCriteria = this.mapped().mapCriteria(criteria);
        return this.mapped().one(this.findOne(this.wrapTree(mappedCriteria)).orElse(null));
    }

    // 加上基类的 T -> T 的形态，会包含额外的十二种
    public Future<T> createAsync(final JsonObject data) {
        return this.mapped().<T>oneTo(data).compose(this::createAsync);
    }

    public T create(final JsonObject data) {
        return this.create(this.mapped().<T>one(data));
    }

    public JsonObject createJ(final T entity) {
        return this.mapped().one(this.create(entity));
    }

    public Future<JsonObject> createAsyncJ(final T entity) {
        return this.createAsync(entity).compose(this.mapped()::oneTo);
    }

    public JsonObject createJ(final JsonObject data) {
        return this.mapped().one(this.create(data));
    }

    public Future<JsonObject> createAsyncJ(final JsonObject data) {
        return this.createAsync(data).compose(this.mapped()::oneTo);
    }

    public Future<JsonArray> createAsyncJ(final List<T> entities) {
        return this.createAsync(entities).compose(this.mapped()::manyTo);
    }

    public Future<List<T>> createAsync(final JsonArray data) {
        return this.createAsync(this.mapped().many(data));
    }

    public Future<JsonArray> createAsyncJ(final JsonArray data) {
        return this.createAsync(data).compose(this.mapped()::manyTo);
    }

    public List<T> create(final JsonArray data) {
        return this.create(this.mapped().many(data));
    }

    public JsonArray createJ(final List<T> entities) {
        return this.mapped().many(this.create(entities));
    }

    public JsonArray createJ(final JsonArray data) {
        return this.mapped().many(this.create(data));
    }

    public T update(final JsonObject data) {
        return this.update(this.mapped().<T>one(data));
    }

    public JsonObject updateJ(final T entity) {
        return this.mapped().one(this.update(entity));
    }

    public JsonObject updateJ(final JsonObject data) {
        return this.mapped().one(this.update(data));
    }

    public Future<T> updateAsync(final JsonObject data) {
        return this.mapped().<T>oneTo(data).compose(this::updateAsync);
    }

    public Future<JsonObject> updateAsyncJ(final T entity) {
        return this.updateAsync(entity).compose(this.mapped()::oneTo);
    }

    public Future<JsonObject> updateAsyncJ(final JsonObject data) {
        return this.updateAsync(data).compose(this.mapped()::oneTo);
    }

    public List<T> update(final JsonArray data) {
        return this.update(this.mapped().many(data));
    }

    public JsonArray updateJ(final List<T> entities) {
        return this.mapped().many(this.update(entities));
    }

    public JsonArray updateJ(final JsonArray data) {
        return this.mapped().many(this.update(data));
    }

    public Future<List<T>> updateAsync(final JsonArray data) {
        return this.updateAsync(this.mapped().many(data));
    }

    public Future<JsonArray> updateAsyncJ(final List<T> entities) {
        return this.updateAsync(entities).compose(this.mapped()::manyTo);
    }

    public Future<JsonArray> updateAsyncJ(final JsonArray data) {
        return this.updateAsync(data).compose(this.mapped()::manyTo);
    }

    public T updateBy(final Serializable id, final JsonObject data) {
        return this.updateBy(id, this.mapped().<T>one(data));
    }

    public JsonObject updateByJ(final Serializable id, final T entity) {
        return this.mapped().one(this.updateBy(id, entity));
    }

    public JsonObject updateByJ(final Serializable id, final JsonObject data) {
        return this.mapped().one(this.updateBy(id, data));
    }

    public Future<T> updateByAsync(final Serializable id, final JsonObject data) {
        return this.mapped().<T>oneTo(data).compose(entity -> this.updateByAsync(id, entity));
    }

    public Future<JsonObject> updateByAsyncJ(final Serializable id, final T entity) {
        return this.updateByAsync(id, entity).compose(this.mapped()::oneTo);
    }

    public Future<JsonObject> updateByAsyncJ(final Serializable id, final JsonObject data) {
        return this.updateByAsync(id, data).compose(this.mapped()::oneTo);
    }

    public T updateBy(final JsonObject criteria, final T updated) {
        final JsonObject mappedCriteria = this.mapped().mapCriteria(criteria);
        return this.updateBy(this.wrapTree(mappedCriteria), updated);
    }

    public T updateBy(final JsonObject criteria, final JsonObject updated) {
        final JsonObject mappedCriteria = this.mapped().mapCriteria(criteria);
        return this.updateBy(this.wrapTree(mappedCriteria), this.mapped().one(updated));
    }

    public JsonObject updateByJ(final JsonObject criteria, final T updated) {
        return this.mapped().one(this.updateBy(criteria, updated));
    }

    public JsonObject updateByJ(final JsonObject criteria, final JsonObject updated) {
        return this.mapped().one(this.updateBy(criteria, updated));
    }

    public Future<T> updateByAsync(final JsonObject criteria, final T updated) {
        final JsonObject mappedCriteria = this.mapped().mapCriteria(criteria);
        return this.updateByAsync(this.wrapTree(mappedCriteria), updated);
    }

    public Future<T> updateByAsync(final JsonObject criteria, final JsonObject updated) {
        final JsonObject mappedCriteria = this.mapped().mapCriteria(criteria);
        return this.updateByAsync(this.wrapTree(mappedCriteria), this.mapped().one(updated));
    }

    public Future<JsonObject> updateByAsyncJ(final JsonObject criteria, final T updated) {
        return this.updateByAsync(criteria, updated).compose(this.mapped()::oneTo);
    }

    public Future<JsonObject> updateByAsyncJ(final JsonObject criteria, final JsonObject updated) {
        return this.updateByAsync(criteria, updated).compose(this.mapped()::oneTo);
    }

    public T saveBy(final Serializable id, final JsonObject data) {
        return this.saveBy(id, this.mapped().<T>one(data));
    }

    public JsonObject saveByJ(final Serializable id, final T entity) {
        return this.mapped().one(this.saveBy(id, entity));
    }

    public JsonObject saveByJ(final Serializable id, final JsonObject data) {
        return this.mapped().one(this.saveBy(id, data));
    }

    public Future<T> saveByAsync(final Serializable id, final JsonObject data) {
        return this.mapped().<T>oneTo(data).compose(entity -> this.saveByAsync(id, entity));
    }

    public Future<JsonObject> saveByAsyncJ(final Serializable id, final T entity) {
        return this.saveByAsync(id, entity).compose(this.mapped()::oneTo);
    }

    public Future<JsonObject> saveByAsyncJ(final Serializable id, final JsonObject data) {
        return this.saveByAsync(id, data).compose(this.mapped()::oneTo);
    }

    public T saveBy(final JsonObject criteria, final T entity) {
        final JsonObject mappedCriteria = this.mapped().mapCriteria(criteria);
        return this.saveBy(this.wrapTree(mappedCriteria), entity);
    }

    public T saveBy(final JsonObject criteria, final JsonObject data) {
        final JsonObject mappedCriteria = this.mapped().mapCriteria(criteria);
        return this.saveBy(this.wrapTree(mappedCriteria), this.mapped().one(data));
    }

    public JsonObject saveByJ(final JsonObject criteria, final T entity) {
        return this.mapped().one(this.saveBy(criteria, entity));
    }

    public JsonObject saveByJ(final JsonObject criteria, final JsonObject data) {
        return this.mapped().one(this.saveBy(criteria, data));
    }

    public Future<T> saveByAsync(final JsonObject criteria, final T entity) {
        final JsonObject mappedCriteria = this.mapped().mapCriteria(criteria);
        return this.saveByAsync(this.wrapTree(mappedCriteria), entity);
    }

    public Future<T> saveByAsync(final JsonObject criteria, final JsonObject data) {
        final JsonObject mappedCriteria = this.mapped().mapCriteria(criteria);
        return this.saveByAsync(this.wrapTree(mappedCriteria), this.mapped().one(data));
    }

    public Future<JsonObject> saveByAsyncJ(final JsonObject criteria, final T entity) {
        return this.saveByAsync(criteria, entity).compose(this.mapped()::oneTo);
    }

    public Future<JsonObject> saveByAsyncJ(final JsonObject criteria, final JsonObject data) {
        return this.saveByAsync(criteria, data).compose(this.mapped()::oneTo);
    }

    public T remove(final JsonObject data) {
        return this.remove(this.mapped().<T>one(data));
    }

    public JsonObject removeJ(final T entity) {
        return this.mapped().one(this.remove(entity));
    }

    public JsonObject removeJ(final JsonObject data) {
        return this.mapped().one(this.remove(data));
    }

    public Future<T> removeAsync(final JsonObject data) {
        return this.mapped().<T>oneTo(data).compose(this::removeAsync);
    }

    public Future<JsonObject> removeAsyncJ(final T entity) {
        return this.removeAsync(entity).compose(this.mapped()::oneTo);
    }

    public Future<JsonObject> removeAsyncJ(final JsonObject data) {
        return this.removeAsync(data).compose(this.mapped()::oneTo);
    }

    public List<T> remove(final JsonArray data) {
        return this.remove(this.mapped().many(data));
    }

    public JsonArray removeJ(final List<T> entities) {
        return this.mapped().many(this.remove(entities));
    }

    public JsonArray removeJ(final JsonArray data) {
        return this.mapped().many(this.remove(data));
    }

    public Future<List<T>> removeAsync(final JsonArray data) {
        return this.removeAsync(this.mapped().many(data));
    }

    public Future<JsonArray> removeAsyncJ(final List<T> entities) {
        return this.removeAsync(entities).compose(this.mapped()::manyTo);
    }

    public Future<JsonArray> removeAsyncJ(final JsonArray data) {
        return this.removeAsync(data).compose(this.mapped()::manyTo);
    }

    public Boolean removeBy(final Collection<?> ids) {
        this.remove(this.findMany(ids.toArray()));
        return true;
    }

    public Boolean removeBy(final JsonObject criteria) {
        final JsonObject mappedCriteria = this.mapped().mapCriteria(criteria);
        return this.removeBy(this.wrapTree(mappedCriteria));
    }

    public Future<Boolean> removeByAsync(final Collection<?> ids) {
        return this.findManyAsync(ids.toArray()).compose(this::removeAsync).map(true);
    }

    public Future<Boolean> removeByAsync(final JsonObject criteria) {
        final JsonObject mappedCriteria = this.mapped().mapCriteria(criteria);
        return this.removeByAsync(this.wrapTree(mappedCriteria));
    }

    public boolean findExist(final JsonObject criteria) {
        final JsonObject mappedCriteria = this.mapped().mapCriteria(criteria);
        return this.findExist(this.wrapTree(mappedCriteria));
    }

    public Future<Boolean> findExistAsync(final Serializable id) {
        return this.findOneAsync(id)
            .compose(exist -> Future.succeededFuture(Objects.nonNull(exist)));
    }

    public Future<Boolean> findExistAsync(final JsonObject criteria) {
        final JsonObject mappedCriteria = this.mapped().mapCriteria(criteria);
        return this.findOneAsync(this.wrapTree(mappedCriteria))
            .compose(exist -> Future.succeededFuture(Objects.nonNull(exist)));
    }

    public Map<String, JsonArray> findGroupByJ(final String field) {
        return this.mapResult(this.findGroupBy(field), this.mapped()::many);
    }

    public Future<Map<String, JsonArray>> findGroupByAsyncJ(final String field) {
        return this.<String>findGroupByAsync(field)
            .compose(list -> this.mapResultAsync(list, this.mapped()::many));
    }

    public Map<String, List<T>> findGroupBy(final JsonObject criteria, final String field) {
        // FIX-DBE: 必须先做Pojo映射转换,否则在后续流程无法分组
        final JsonObject mappedCriteria = this.mapped().mapCriteria(criteria);
        return this.findGroupBy(this.wrapTree(mappedCriteria), this.toPojoField(field));
    }

    private String toPojoField(final String fieldName) {
        // 构造一个临时的 JsonObject 来利用现有的 mapCriteria 机制
        final JsonObject tempCriteria = new JsonObject().put(fieldName, true);
        final JsonObject mappedCriteria = this.mapped().mapCriteria(tempCriteria);

        // 使用更清晰的方式获取映射后的字段名
        if (!mappedCriteria.isEmpty()) {
            return mappedCriteria.getMap().keySet().iterator().next();
        }
        // fallback 返回原始字段名（防御性编程）
        return fieldName;
    }

    public Map<String, JsonArray> findGroupByJ(final JsonObject criteria, final String field) {
        final JsonObject mappedCriteria = this.mapped().mapCriteria(criteria);
        return this.mapResult(this.findGroupBy(this.wrapTree(mappedCriteria), field), this.mapped()::many);
    }

    public Future<Map<String, List<T>>> findGroupByAsync(final JsonObject criteria, final String field) {
        final JsonObject mappedCriteria = this.mapped().mapCriteria(criteria);
        return this.findGroupByAsync(this.wrapTree(mappedCriteria), field);
    }

    public Future<Map<String, JsonArray>> findGroupByAsyncJ(final JsonObject criteria, final String field) {
        return this.findGroupByAsync(criteria, field)
            .compose(list -> this.mapResultAsync(list, this.mapped()::many));
    }

    public Long count(final JsonObject criteria) {
        final JsonObject mappedCriteria = this.mapped().mapCriteria(criteria);
        return this.count(this.wrapTree(mappedCriteria)).orElse(0L);
    }

    public Future<Long> countAsync(final JsonObject criteria) {
        final JsonObject mappedCriteria = this.mapped().mapCriteria(criteria);
        return this.countAsync(this.wrapTree(mappedCriteria));
    }

    public ConcurrentMap<String, Long> countBy(final JsonObject criteria, final String field) {
        final JsonObject mappedCriteria = this.mapped().mapCriteria(criteria);
        return this.countBy(this.wrapTree(mappedCriteria), field);
    }

    public Future<ConcurrentMap<String, Long>> countByAsync(final JsonObject criteria, final String field) {
        final JsonObject mappedCriteria = this.mapped().mapCriteria(criteria);
        return this.countByAsync(this.wrapTree(mappedCriteria), field);
    }

    public BigDecimal sum(final String field, final JsonObject criteria) {
        final JsonObject mappedCriteria = this.mapped().mapCriteria(criteria);
        return this.sum(field, this.wrapTree(mappedCriteria)).orElse(BigDecimal.ZERO);
    }

    public Future<BigDecimal> sumAsync(final String field, final JsonObject criteria) {
        final JsonObject mappedCriteria = this.mapped().mapCriteria(criteria);
        return this.sumAsync(field, this.wrapTree(mappedCriteria));
    }

    public ConcurrentMap<String, BigDecimal> sumBy(final String field, final JsonObject criteria, final String groupBy) {
        final JsonObject mappedCriteria = this.mapped().mapCriteria(criteria);
        return this.sumBy(field, this.wrapTree(mappedCriteria), groupBy);
    }

    public Future<ConcurrentMap<String, BigDecimal>> sumByAsync(final String field, final JsonObject criteria, final String groupBy) {
        final JsonObject mappedCriteria = this.mapped().mapCriteria(criteria);
        return this.sumByAsync(field, this.wrapTree(mappedCriteria), groupBy);
    }

    // ---- AVG
    public BigDecimal avg(final String field, final JsonObject criteria) {
        final JsonObject mappedCriteria = this.mapped().mapCriteria(criteria);
        return this.avg(field, this.wrapTree(mappedCriteria)).orElse(BigDecimal.ZERO);
    }

    public Future<BigDecimal> avgAsync(final String field, final JsonObject criteria) {
        final JsonObject mappedCriteria = this.mapped().mapCriteria(criteria);
        return this.avgAsync(field, this.wrapTree(mappedCriteria));
    }

    public ConcurrentMap<String, BigDecimal> avgBy(final String field, final JsonObject criteria, final String groupBy) {
        final JsonObject mappedCriteria = this.mapped().mapCriteria(criteria);
        return this.avgBy(field, this.wrapTree(mappedCriteria), groupBy);
    }

    public Future<ConcurrentMap<String, BigDecimal>> avgByAsync(final String field, final JsonObject criteria, final String groupBy) {
        final JsonObject mappedCriteria = this.mapped().mapCriteria(criteria);
        return this.avgByAsync(field, this.wrapTree(mappedCriteria), groupBy);
    }

    // ---- MIN
    public BigDecimal min(final String field, final JsonObject criteria) {
        final JsonObject mappedCriteria = this.mapped().mapCriteria(criteria);
        return this.min(field, this.wrapTree(mappedCriteria)).orElse(BigDecimal.ZERO);
    }

    public Future<BigDecimal> minAsync(final String field, final JsonObject criteria) {
        final JsonObject mappedCriteria = this.mapped().mapCriteria(criteria);
        return this.minAsync(field, this.wrapTree(mappedCriteria));
    }

    public ConcurrentMap<String, BigDecimal> minBy(final String field, final JsonObject criteria, final String groupBy) {
        final JsonObject mappedCriteria = this.mapped().mapCriteria(criteria);
        return this.minBy(field, this.wrapTree(mappedCriteria), groupBy);
    }

    public Future<ConcurrentMap<String, BigDecimal>> minByAsync(final String field, final JsonObject criteria, final String groupBy) {
        final JsonObject mappedCriteria = this.mapped().mapCriteria(criteria);
        return this.minByAsync(field, this.wrapTree(mappedCriteria), groupBy);
    }

    // ---- MAX
    public BigDecimal max(final String field, final JsonObject criteria) {
        final JsonObject mappedCriteria = this.mapped().mapCriteria(criteria);
        return this.max(field, this.wrapTree(mappedCriteria)).orElse(BigDecimal.ZERO);
    }

    public Future<BigDecimal> maxAsync(final String field, final JsonObject criteria) {
        final JsonObject mappedCriteria = this.mapped().mapCriteria(criteria);
        return this.maxAsync(field, this.wrapTree(mappedCriteria));
    }

    public ConcurrentMap<String, BigDecimal> maxBy(final String field, final JsonObject criteria, final String groupBy) {
        final JsonObject mappedCriteria = this.mapped().mapCriteria(criteria);
        return this.maxBy(field, this.wrapTree(mappedCriteria), groupBy);
    }

    public Future<ConcurrentMap<String, BigDecimal>> maxByAsync(final String field, final JsonObject criteria, final String groupBy) {
        final JsonObject mappedCriteria = this.mapped().mapCriteria(criteria);
        return this.maxByAsync(field, this.wrapTree(mappedCriteria), groupBy);
    }

}

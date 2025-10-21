package io.r2mo.vertx.jooq;

import io.r2mo.base.dbe.DBS;
import io.r2mo.base.dbe.syntax.QSorter;
import io.r2mo.typed.common.Pagination;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * 综合同异步双模型，实现前置和后置的 JSON 映射处理
 * <pre>
 *     1. 后置使用日志：After 注释
 *     2. 前置使用日志：Before 注释
 * </pre>
 *
 * @author lang : 2025-10-20
 */
class DBExJson<T> extends DBExFuture<T> {
    protected DBExJson(final Class<T> daoCls, final DBS dbs) {
        super(daoCls, dbs);
        this.mapped = DBVector.of(this.metadata());
    }

    private final DBVector<T> mapped;

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
}

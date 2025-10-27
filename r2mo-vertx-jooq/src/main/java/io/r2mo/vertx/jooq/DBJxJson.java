package io.r2mo.vertx.jooq;

import io.r2mo.base.dbe.DBS;
import io.r2mo.base.dbe.Join;
import io.r2mo.base.dbe.common.DBRef;
import io.r2mo.spi.SPI;
import io.r2mo.typed.json.JBase;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author lang : 2025-10-26
 */
class DBJxJson extends DBJxFuture {
    protected DBJxJson(final DBRef ref, final DBS dbs) {
        super(ref, dbs);
    }

    protected DBJxJson(final Join join, final DBS dbs) {
        super(join, dbs);
    }

    public Future<Long> countAsyncJ(final JsonObject criteria) {
        return this.countAsync(SPI.J(criteria));
    }

    public Future<Long> countAsyncJ(final Map<String, Object> criteria) {
        return this.countAsync(criteria);
    }

    public Future<Long> countAsyncJ(final String field, final Object value) {
        return this.countAsync(field, value);
    }

    public Future<Long> countAsyncJ() {
        return this.countAsync();
    }

    public Future<JsonArray> findAllAsyncJ() {
        return this.findAllAsync().map(JBase::data);
    }

    public Future<Boolean> findExistAsyncJ(final JsonObject tree) {
        return this.findExistAsync(SPI.J(tree));
    }

    public Future<JsonArray> findFullAsyncJ(final JsonObject query) {
        return this.findFullAsync(SPI.J(query)).map(JBase::data);
    }

    public Future<JsonArray> findManyAsyncJ(final JsonObject tree) {
        return this.findManyAsync(SPI.J(tree)).map(JBase::data);
    }

    public Future<JsonArray> findManyAsyncJ(final Map<String, Object> map) {
        return this.findManyAsync(map).map(JBase::data);
    }

    public Future<JsonArray> findManyAsyncJ(final String field, final Object value) {
        return this.findManyAsync(field, value).map(JBase::data);
    }

    public Future<JsonArray> findManyAsyncJ() {
        return this.findManyAsync().map(JBase::data);
    }

    public Future<JsonArray> findManyByAsyncJ(final JsonObject mapJ) {
        return this.findManyByAsync(SPI.J(mapJ)).map(JBase::data);
    }

    public Future<JsonArray> findManyInAsyncJ(final String field, final List<?> values) {
        return this.findManyInAsync(field, values).map(JBase::data);
    }

    public Future<JsonArray> findManyInAsyncJ(final String field, final Object... values) {
        return this.findManyInAsync(field, values).map(JBase::data);
    }

    public Future<JsonArray> findManyInAsyncJ(final String field, final JsonArray values) {
        return this.findManyInAsync(field, values.getList()).map(JBase::data);
    }

    public Future<JsonObject> findOneAsyncJ(final JsonObject treeJ) {
        return this.findOneAsync(SPI.J(treeJ)).map(JBase::data);
    }

    public Future<JsonObject> findOneAsyncJ(final Map<String, Object> map) {
        return this.findOneAsync(map).map(JBase::data);
    }

    public Future<JsonObject> findOneAsyncJ(final String field, final Object value) {
        return this.findOneAsync(field, value).map(JBase::data);
    }

    public Future<JsonObject> findOneAsyncJ(final Serializable id) {
        return this.findOneAsync(id).map(JBase::data);
    }

    public Future<JsonObject> findOneByAsyncJ(final JsonObject mapJ) {
        return this.findOneByAsync(SPI.J(mapJ)).map(JBase::data);
    }

    public Future<JsonObject> findPageAsyncJ(final JsonObject queryJ) {
        return this.findPageAsync(SPI.J(queryJ)).map(JBase::data);
    }

    public Future<Boolean> removeByAsyncJ(final JsonObject criteriaJ) {
        return this.removeByAsync(SPI.J(criteriaJ));
    }

    public Future<Boolean> removeByAsyncJ(final Map<String, Object> criteria) {
        return this.removeByAsync(criteria);
    }

    public Future<Boolean> removeByAsyncJ(final String field, final Object value) {
        return this.removeByAsync(field, value);
    }

    public Future<Boolean> removeByAsyncJ(final Serializable id) {
        return this.removeByAsync(id);
    }

    public Future<JsonObject> updateByAsyncJ(final JsonObject criteriaJ, final JsonObject updateJ) {
        return this.updateByAsync(SPI.J(criteriaJ), SPI.J(updateJ)).map(JBase::data);
    }

    public Future<JsonObject> updateByAsyncJ(final Map<String, Object> criteria, final JsonObject updateJ) {
        return this.updateByAsync(criteria, SPI.J(updateJ)).map(JBase::data);
    }

    public Future<JsonObject> updateByAsyncJ(final String field, final Object value, final JsonObject updateJ) {
        return this.updateByAsync(field, value, SPI.J(updateJ)).map(JBase::data);
    }

    public Future<JsonObject> updateByAsyncJ(final Serializable id, final JsonObject updateJ) {
        return this.updateByAsync(id, SPI.J(updateJ)).map(JBase::data);
    }

    public Future<JsonObject> createAsyncJ(final JsonObject insertJ) {
        return this.createAsync(SPI.J(insertJ)).map(JBase::data);
    }

    // ========== 同步版（不使用 JBase.data，直接 .data()） ==========

    public Long countJ(final JsonObject criteria) {
        return this.count(SPI.J(criteria)).orElse(0L);
    }

    public Long countJ(final Map<String, Object> criteria) {
        return this.count(criteria).orElse(0L);
    }

    public Long countJ(final String field, final Object value) {
        return this.count(field, value).orElse(0L);
    }

    public Long countJ() {
        return this.count().orElse(0L);
    }

    public JsonArray findAllJ() {
        return this.findAll().data();
    }

    public Boolean findExistJ(final JsonObject tree) {
        return this.findExist(SPI.J(tree));
    }

    public JsonArray findFullJ(final JsonObject query) {
        return this.findFull(SPI.J(query)).data();
    }

    public JsonArray findManyJ(final JsonObject tree) {
        return this.findMany(SPI.J(tree)).data();
    }

    public JsonArray findManyJ(final Map<String, Object> map) {
        return this.findMany(map).data();
    }

    public JsonArray findManyJ(final String field, final Object value) {
        return this.findMany(field, value).data();
    }

    public JsonArray findManyJ() {
        return this.findMany().data();
    }

    public JsonArray findManyByJ(final JsonObject mapJ) {
        return this.findManyBy(SPI.J(mapJ)).data();
    }

    public JsonArray findManyInJ(final String field, final List<?> values) {
        return this.findManyIn(field, values).data();
    }

    public JsonArray findManyInJ(final String field, final Object... values) {
        return this.findManyIn(field, values).data();
    }

    public JsonArray findManyInJ(final String field, final JsonArray values) {
        return this.findManyIn(field, values.getList()).data();
    }

    public JsonObject findOneJ(final JsonObject treeJ) {
        return this.findOne(SPI.J(treeJ)).data();
    }

    public JsonObject findOneJ(final Map<String, Object> map) {
        return this.findOne(map).data();
    }

    public JsonObject findOneJ(final String field, final Object value) {
        return this.findOne(field, value).data();
    }

    public JsonObject findOneJ(final Serializable id) {
        return this.findOne(id).data();
    }

    public JsonObject findOneByJ(final JsonObject mapJ) {
        return this.findOneBy(SPI.J(mapJ)).data();
    }

    public JsonObject findPageJ(final JsonObject queryJ) {
        return this.findPage(SPI.J(queryJ)).data();
    }

    public Boolean removeByJ(final JsonObject criteriaJ) {
        return this.removeBy(SPI.J(criteriaJ));
    }

    public Boolean removeByJ(final Map<String, Object> criteria) {
        return this.removeBy(criteria);
    }

    public Boolean removeByJ(final String field, final Object value) {
        return this.removeBy(field, value);
    }

    public Boolean removeByJ(final Serializable id) {
        return this.removeBy(id);
    }

    public JsonObject updateByJ(final JsonObject criteriaJ, final JsonObject updateJ) {
        return this.updateBy(SPI.J(criteriaJ), SPI.J(updateJ)).data();
    }

    public JsonObject updateByJ(final Map<String, Object> criteria, final JsonObject updateJ) {
        return this.updateBy(criteria, SPI.J(updateJ)).data();
    }

    public JsonObject updateByJ(final String field, final Object value, final JsonObject updateJ) {
        return this.updateBy(field, value, SPI.J(updateJ)).data();
    }

    public JsonObject updateByJ(final Serializable id, final JsonObject updateJ) {
        return this.updateBy(id, SPI.J(updateJ)).data();
    }

    public JsonObject createJ(final JsonObject insertJ) {
        return this.create(SPI.J(insertJ)).data();
    }
}

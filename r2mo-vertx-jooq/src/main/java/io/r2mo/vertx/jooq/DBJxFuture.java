package io.r2mo.vertx.jooq;

import io.r2mo.base.dbe.DBS;
import io.r2mo.base.dbe.Join;
import io.r2mo.base.dbe.common.DBRef;
import io.r2mo.base.dbe.syntax.QQuery;
import io.r2mo.base.dbe.syntax.QTree;
import io.r2mo.typed.json.JArray;
import io.r2mo.typed.json.JObject;
import io.vertx.core.Future;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author lang : 2025-10-26
 */
class DBJxFuture extends DBJxCommon {
    protected DBJxFuture(final DBRef ref, final DBS dbs) {
        super(ref, dbs);
    }

    protected DBJxFuture(final Join join, final DBS dbs) {
        super(join, dbs);
    }

    // 需要：import io.vertx.core.Future;

    public Future<Long> countAsync() {
        return Future.succeededFuture(this.count().orElse(0L));
    }

    public Future<Long> countAsync(final JObject criteria) {
        return Future.succeededFuture(this.count(criteria).orElse(0L));
    }

    public Future<Long> countAsync(final Map<String, Object> criteria) {
        return Future.succeededFuture(this.count(criteria).orElse(0L));
    }

    public Future<Long> countAsync(final QTree criteria) {
        return Future.succeededFuture(this.count(criteria).orElse(0L));
    }

    public Future<Long> countAsync(final String field, final Object value) {
        return Future.succeededFuture(this.count(field, value).orElse(0L));
    }

    public Future<JArray> findAllAsync() {
        return Future.succeededFuture(this.findAll());
    }

    public Future<Boolean> findExistAsync(final JObject tree) {
        return Future.succeededFuture(this.findExist(tree));
    }

    public Future<Boolean> findExistAsync(final QTree tree) {
        return Future.succeededFuture(this.findExist(tree));
    }

    public Future<JArray> findFullAsync(final JObject query) {
        return Future.succeededFuture(this.findFull(query));
    }

    public Future<JArray> findFullAsync(final QQuery query) {
        return Future.succeededFuture(this.findFull(query));
    }
    // 需要：import io.vertx.core.Future;

    public Future<JArray> findManyAsync(final JObject tree) {
        return Future.succeededFuture(this.findMany(tree));
    }

    public Future<JArray> findManyAsync(final Map<String, Object> map) {
        return Future.succeededFuture(this.findMany(map));
    }

    public Future<JArray> findManyAsync(final QTree tree) {
        return Future.succeededFuture(this.findMany(tree));
    }

    public Future<JArray> findManyAsync(final String field, final Object value) {
        return Future.succeededFuture(this.findMany(field, value));
    }

    public Future<JArray> findManyAsync() {
        return Future.succeededFuture(this.findMany());
    }

    public Future<JArray> findManyByAsync(final JObject mapJ) {
        return Future.succeededFuture(this.findManyBy(mapJ));
    }

    public Future<JArray> findManyInAsync(final String field, final List<?> values) {
        return Future.succeededFuture(this.findManyIn(field, values));
    }

    public Future<JArray> findManyInAsync(final String field, final Object... values) {
        return Future.succeededFuture(this.findManyIn(field, values));
    }

    // 需要：import io.vertx.core.Future;

    public Future<JObject> findOneAsync(final JObject tree) {
        return Future.succeededFuture(this.findOne(tree));
    }

    public Future<JObject> findOneAsync(final Map<String, Object> map) {
        return Future.succeededFuture(this.findOne(map));
    }

    public Future<JObject> findOneAsync(final String field, final Object value) {
        return Future.succeededFuture(this.findOne(field, value));
    }

    public Future<JObject> findOneAsync(final Serializable id) {
        return Future.succeededFuture(this.findOne(id));
    }

    public Future<JObject> findOneAsync(final QTree tree) {
        return Future.succeededFuture(this.findOne(tree));
    }

    public Future<JObject> findOneByAsync(final JObject mapJ) {
        return Future.succeededFuture(this.findOneBy(mapJ));
    }

    public Future<JObject> findPageAsync(final JObject query) {
        return Future.succeededFuture(this.findPage(query));
    }

    public Future<JObject> findPageAsync(final QQuery query) {
        return Future.succeededFuture(this.findPage(query));
    }


    public Future<Boolean> removeByAsync(final JObject criteria) {
        return Future.succeededFuture(this.removeBy(criteria));
    }

    public Future<Boolean> removeByAsync(final Map<String, Object> criteria) {
        return Future.succeededFuture(this.removeBy(criteria));
    }

    public Future<Boolean> removeByAsync(final QTree criteria) {
        return Future.succeededFuture(this.removeBy(criteria));
    }

    public Future<Boolean> removeByAsync(final String field, final Object value) {
        return Future.succeededFuture(this.removeBy(field, value));
    }

    public Future<Boolean> removeByAsync(final Serializable id) {
        return Future.succeededFuture(this.removeBy(id));
    }
    // 需要：import io.vertx.core.Future;

    public Future<JObject> updateByAsync(final JObject criteria, final JObject latest) {
        return Future.succeededFuture(this.updateBy(criteria, latest));
    }

    public Future<JObject> updateByAsync(final Map<String, Object> criteria, final JObject latest) {
        return Future.succeededFuture(this.updateBy(criteria, latest));
    }

    public Future<JObject> updateByAsync(final QTree criteria, final JObject latest) {
        return Future.succeededFuture(this.updateBy(criteria, latest));
    }

    public Future<JObject> updateByAsync(final String field, final Object value, final JObject latest) {
        return Future.succeededFuture(this.updateBy(field, value, latest));
    }

    public Future<JObject> updateByAsync(final Serializable id, final JObject latest) {
        return Future.succeededFuture(this.updateBy(id, latest));
    }

    public Future<JObject> createAsync(final JObject data) {
        return Future.succeededFuture(this.create(data));
    }
}

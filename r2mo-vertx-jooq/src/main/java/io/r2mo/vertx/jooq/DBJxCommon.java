package io.r2mo.vertx.jooq;

import io.r2mo.base.dbe.DBS;
import io.r2mo.base.dbe.Join;
import io.r2mo.base.dbe.common.DBRef;
import io.r2mo.base.dbe.syntax.QQuery;
import io.r2mo.base.dbe.syntax.QTree;
import io.r2mo.typed.json.JArray;
import io.r2mo.typed.json.JObject;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author lang : 2025-10-26
 */
class DBJxCommon extends DBJxBase {
    protected DBJxCommon(final DBRef ref, final DBS dbs) {
        super(ref, dbs);
    }

    protected DBJxCommon(final Join join, final DBS dbs) {
        super(join, dbs);
    }

    public Optional<Long> count() {
        return this.refDBJ().count(Map.of());
    }

    public Optional<Long> count(final JObject criteria) {
        return this.refDBJ().count(criteria);
    }

    public Optional<Long> count(final Map<String, Object> criteria) {
        return this.refDBJ().count(criteria);
    }

    public Optional<Long> count(final QTree criteria) {
        return this.refDBJ().count(criteria);
    }

    public Optional<Long> count(final String field, final Object value) {
        return this.refDBJ().count(field, value);
    }

    public JArray findAll() {
        return this.refDBJ().findAll();
    }

    public boolean findExist(final JObject tree) {
        return this.refDBJ().findExist(tree);
    }

    public boolean findExist(final QTree tree) {
        return this.refDBJ().findExist(tree);
    }

    public JArray findFull(final JObject query) {
        return this.refDBJ().findFull(query);
    }

    public JArray findFull(final QQuery query) {
        return this.refDBJ().findFull(query);
    }

    public JArray findMany(final JObject tree) {
        return this.refDBJ().findMany(tree);
    }

    public JArray findMany(final Map<String, Object> map) {
        return this.refDBJ().findMany(map);
    }

    public JArray findMany(final QTree tree) {
        return this.refDBJ().findMany(tree);
    }

    public JArray findMany(final String field, final Object value) {
        return this.refDBJ().findMany(field, value);
    }

    public JArray findMany() {
        return this.refDBJ().findMany(Map.of());
    }

    public JArray findManyBy(final JObject mapJ) {
        return this.refDBJ().findManyBy(mapJ);
    }

    public JArray findManyIn(final String field, final List<?> values) {
        return this.refDBJ().findManyIn(field, values);
    }

    public JArray findManyIn(final String field, final Object... values) {
        return this.refDBJ().findManyIn(field, values);
    }

    public JObject findOne(final JObject tree) {
        return this.refDBJ().findOne(tree);
    }

    public JObject findOne(final Map<String, Object> map) {
        return this.refDBJ().findOne(map);
    }

    public JObject findOne(final String field, final Object value) {
        return this.refDBJ().findOne(field, value);
    }

    public JObject findOne(final Serializable id) {
        return this.refDBJ().findOne(id);
    }

    public JObject findOne(final QTree tree) {
        return this.refDBJ().findOne(tree);
    }

    public JObject findOneBy(final JObject mapJ) {
        return this.refDBJ().findOneBy(mapJ);
    }

    public JObject findPage(final JObject query) {
        return this.refDBJ().findPage(query);
    }

    public JObject findPage(final QQuery query) {
        return this.refDBJ().findPage(query);
    }

    public Boolean removeBy(final JObject criteria) {
        return this.refDBJ().removeBy(criteria);
    }

    public Boolean removeBy(final Map<String, Object> criteria) {
        return this.refDBJ().removeBy(criteria);
    }

    public Boolean removeBy(final QTree criteria) {
        return this.refDBJ().removeBy(criteria);
    }

    public Boolean removeBy(final String field, final Object value) {
        return this.refDBJ().removeBy(field, value);
    }

    public Boolean removeBy(final Serializable id) {
        return this.refDBJ().removeBy(id);
    }

    public JObject updateBy(final JObject criteria, final JObject latest) {
        return this.refDBJ().updateBy(criteria, latest);
    }

    public JObject updateBy(final Map<String, Object> criteria, final JObject latest) {
        return this.refDBJ().updateBy(criteria, latest);
    }

    public JObject updateBy(final QTree criteria, final JObject latest) {
        return this.refDBJ().updateBy(criteria, latest);
    }

    public JObject updateBy(final String field, final Object value, final JObject latest) {
        return this.refDBJ().updateBy(field, value, latest);
    }

    public JObject updateBy(final Serializable id, final JObject latest) {
        return this.refDBJ().updateBy(id, latest);
    }

    public JObject create(final JObject insert) {
        return this.refDBJ().create(insert);
    }
}

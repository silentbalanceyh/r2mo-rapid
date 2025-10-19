package io.r2mo.vertx.jooq;

import io.r2mo.base.dbe.DBS;
import io.r2mo.base.dbe.Database;
import io.r2mo.base.dbe.syntax.QQuery;
import io.r2mo.base.dbe.syntax.QTree;
import io.r2mo.base.program.R2Vector;
import io.r2mo.dbe.jooq.DBE;
import io.r2mo.dbe.jooq.core.domain.JooqDatabase;
import io.r2mo.typed.cc.Cc;
import io.r2mo.typed.common.Pagination;
import io.r2mo.typed.exception.web._501NotSupportException;
import io.r2mo.typed.json.JObject;
import io.r2mo.vertx.dbe.AsyncDBE;
import io.vertx.core.Vertx;
import org.jooq.Condition;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * 异步模式的 DBE -> Async Database Engine, x - Extension（扩展）
 *
 * @author lang : 2025-10-18
 */
@SuppressWarnings("all")
public class DBEx<T> {
    private static final Cc<String, DBEx> CC_DBEX = Cc.openThread();
    private final DBS dbs;
    private final Vertx vertxRef;
    private final AsyncMeta metadata;
    // Bridge
    private final DBE<T> dbe;
    private final AsyncDBE<Condition, T, AsyncMeta> dbeAsync;

    public DBEx vector(final R2Vector vector) {
        metadata.vector(vector);
        return this;
    }

    public AsyncMeta metadata() {
        return this.metadata;
    }

    private DBEx(final Class<T> daoCls, final DBS dbs) {
        // 提取 Database 引用，构造同步专用的 DSLContext
        final Database database = dbs.getDatabase();
        if (!(database instanceof final JooqDatabase jooqDatabase)) {
            throw new _501NotSupportException("[ R2MO ] JOOQ 模式仅支持 JooqDatabase 类型的数据库引用！");
        }

        this.dbs = dbs;
        // 内部直接访问 Context 中的引用
        this.vertxRef = JooqContext.vertxStatic(dbs);
        Objects.requireNonNull(vertxRef, "[ R2MO ] 关键步骤 DBS 无法初始化 Vertx 引用！");


        final AsyncMeta metaAsync = AsyncMeta.of(daoCls, jooqDatabase.getContext(), this.vertxRef);
        this.metadata = metaAsync;


        // 同步初始化
        this.dbe = DBE.of((Class<T>) metaAsync.metaEntity(), jooqDatabase.getContext());
        // 异步初始化
        this.dbeAsync = AsyncDBE.of((Class<T>) metaAsync.metaEntity(), metaAsync);
    }

    // -------------------- 静态创建方法 ----------------------
    public static DBEx of(final Class<?> daoCls, final DBS dbs) {
        final String cached = JooqContext.cached(daoCls, dbs);
        return CC_DBEX.pick(() -> new DBEx(daoCls, dbs), cached);
    }

    // ==================== COUNT 方法 ====================
    // region DBE 同步模式下的所有方法调用
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

    public Optional<BigDecimal> max(final String aggrField, final JObject criteriaJ) {
        return this.dbe.max(aggrField, criteriaJ);
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

package io.r2mo.dbe.common;

import io.r2mo.base.dbe.join.DBRef;
import io.r2mo.base.dbe.operation.OpJoin;
import io.r2mo.base.dbe.operation.QrAnalyzer;
import io.r2mo.base.dbe.syntax.QQuery;
import io.r2mo.base.dbe.syntax.QTree;
import io.r2mo.spi.SPI;
import io.r2mo.typed.json.JArray;
import io.r2mo.typed.json.JObject;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 *
 * @author lang : 2025-10-23
 */
public abstract class DBEJ<QR, T, EXECUTOR> {
    private final EXECUTOR executor;

    protected final OpJoin<T, QR> opJoin;

    protected final DBRef ref;

    /**
     * 此处的 DBRef 必须是完整的
     *
     * @param ref      DBRef
     * @param executor EXECUTOR
     */
    protected DBEJ(final DBRef ref, final EXECUTOR executor) {
        this.ref = ref;
        this.executor = executor;
        this.opJoin = SPI.SPI_DB.opJoin(ref, executor);
        /*
         * 反向设置，绑定第二执行器、一直往后完成 OpJoin 的核心初始化，父子流程设置，这种模式和方法在 Jooq 中无所谓，但
         * 由于 MybatisPlus 的 Mapper 是绑定了实体的，需要预先构建完成，所以这里需要做一个后置配置
         */
    }

    protected EXECUTOR executor() {
        return this.executor;
    }

    protected abstract QrAnalyzer<QR> analyzer();

    public JArray findAll() {
        return this.opJoin.findMany(this.analyzer().where(Map.of()));
    }

    public boolean findExist(final JObject tree) {
        return this.findExist(QTree.of(tree));
    }

    public boolean findExist(final QTree tree) {
        return this.opJoin.count(this.analyzer().where(tree)).orElse(0L) > 0;
    }

    public JArray findFull(final QQuery query) {
        return this.opJoin.findMany(this.analyzer().where(query));
    }

    public JArray findFull(final JObject queryJ) {
        return this.findFull(QQuery.of(queryJ));
    }

    public JArray findMany(final QTree tree) {
        return this.opJoin.findMany(this.analyzer().where(tree));
    }

    public JArray findMany(final Map<String, Object> map) {
        return this.opJoin.findMany(this.analyzer().where(map));
    }

    public JArray findMany(final JObject treeJ) {
        return this.findMany(QTree.of(treeJ));
    }

    public JArray findMany(final String field, final Object value) {
        return this.opJoin.findMany(this.analyzer().where(field, value));
    }

    public JArray findManyBy(final JObject mapJ) {
        return this.findMany(mapJ.toMap());
    }

    public JArray findManyIn(final String field, final List<?> values) {
        return this.opJoin.findMany(this.analyzer().whereIn(field, values.toArray()));
    }

    public JArray findManyIn(final String field, final Object... values) {
        return this.opJoin.findMany(this.analyzer().whereIn(field, values));
    }

    public JObject findOne(final QTree tree) {
        return this.opJoin.findOne(this.analyzer().where(tree));
    }

    public JObject findOne(final JObject treeJ) {
        return this.findOne(QTree.of(treeJ));
    }

    public JObject findOne(final String field, final Object value) {
        return this.opJoin.findOne(this.analyzer().where(field, value));
    }

    public JObject findOne(final Map<String, Object> map) {
        return this.opJoin.findOne(this.analyzer().where(map));
    }

    public JObject findOneBy(final JObject mapJ) {
        return this.findOne(mapJ.toMap());
    }

    public JObject findOne(final Serializable id) {
        return this.opJoin.findById(id);
    }

    public JObject findPage(final QQuery query) {
        return this.opJoin.findPage(query);
    }

    public JObject findPage(final JObject queryJ) {
        return this.findPage(QQuery.of(queryJ));
    }

    public Optional<Long> count(final QTree tree) {
        return this.opJoin.count(this.analyzer().where(tree));
    }

    public Optional<Long> count(final JObject treeJ) {
        return this.count(QTree.of(treeJ));
    }

    public Optional<Long> count(final Map<String, Object> map) {
        return this.opJoin.count(this.analyzer().where(map));
    }

    public Optional<Long> count(final String field, final Object value) {
        return this.opJoin.count(this.analyzer().where(field, value));
    }

    public JObject create(final JObject latest) {
        return this.opJoin.create(latest);
    }

    public boolean removeBy(final Serializable id) {
        return this.opJoin.removeById(id);
    }

    public boolean removeBy(final QTree tree) {
        return this.opJoin.removeBy(this.analyzer().where(tree));
    }

    public boolean removeBy(final JObject treeJ) {
        return this.removeBy(QTree.of(treeJ));
    }

    public boolean removeBy(final Map<String, Object> map) {
        return this.opJoin.removeBy(this.analyzer().where(map));
    }

    public boolean removeBy(final String field, final Object value) {
        return this.opJoin.removeBy(this.analyzer().where(field, value));
    }

    public JObject updateBy(final Serializable id, final JObject latest) {
        return this.opJoin.updateById(id, latest);
    }

    public JObject updateBy(final QTree tree, final JObject latest) {
        return this.opJoin.update(this.analyzer().where(tree), latest);
    }

    public JObject updateBy(final JObject treeJ, final JObject latest) {
        return this.updateBy(QTree.of(treeJ), latest);
    }

    public JObject updateBy(final Map<String, Object> map, final JObject latest) {
        return this.opJoin.update(this.analyzer().where(map), latest);
    }

    public JObject updateBy(final String field, final Object value, final JObject latest) {
        return this.opJoin.update(this.analyzer().where(field, value), latest);
    }
}

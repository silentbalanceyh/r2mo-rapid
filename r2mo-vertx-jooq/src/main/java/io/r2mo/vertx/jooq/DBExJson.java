package io.r2mo.vertx.jooq;

import io.r2mo.base.dbe.DBS;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;

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

    // region After: 后置类型映射处理
    public JsonArray findAllJ() {
        return this.mapped().outMany(this.dbe.findAll());
    }

    public Future<JsonArray> findAllJAsync() {
        return this.mapped().outMany(this.findAllAsync());
    }
    // endregion
}

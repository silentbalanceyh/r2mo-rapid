package io.r2mo.vertx.jooq;

import io.r2mo.base.dbe.DBS;
import io.r2mo.base.dbe.Database;
import io.r2mo.base.dbe.syntax.QSorter;
import io.r2mo.base.dbe.syntax.QTree;
import io.r2mo.base.util.R2MO;
import io.r2mo.dbe.jooq.DBE;
import io.r2mo.dbe.jooq.core.domain.JooqDatabase;
import io.r2mo.spi.SPI;
import io.r2mo.typed.common.Pagination;
import io.r2mo.typed.exception.web._501NotSupportException;
import io.r2mo.typed.json.JArray;
import io.r2mo.typed.json.JObject;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * @author lang : 2025-10-20
 */
@SuppressWarnings("all")
class DBExBase<T> {
    // 桥接：同步和异步
    protected final DBE<T> dbe;
    protected final AsyncDBE<T> dbeAsync;
    private final DBS dbs;
    private final Vertx vertxRef;
    // 映射和元数据
    @Accessors(fluent = true, chain = true)
    private final AsyncMeta metadata;

    protected DBExBase(final Class<T> daoCls, final DBS dbs) {
        // 提取 Database 引用，构造同步专用的 DSLContext
        final Database database = dbs.getDatabase();
        if (!(database instanceof final JooqDatabase jooqDatabase)) {
            throw new _501NotSupportException("[ R2MO ] JOOQ 模式仅支持 JooqDatabase 类型的数据库引用！");
        }

        this.dbs = dbs;
        // 内部直接访问 Context 中的引用
        this.vertxRef = AsyncDBContext.vertxStatic(dbs);
        Objects.requireNonNull(this.vertxRef, "[ R2MO ] (ADB) 关键步骤 DBS 无法初始化 Vertx 引用！");


        final AsyncMeta metaAsync = AsyncMeta.of(daoCls, jooqDatabase.getContext(), this.vertxRef);
        this.metadata = metaAsync;


        // 同步初始化
        this.dbe = DBE.of((Class<T>) metaAsync.metaEntity(), jooqDatabase.getContext());
        // 异步初始化
        this.dbeAsync = AsyncDBE.of((Class<T>) metaAsync.metaEntity(), metaAsync);
    }

    public DBS refDBS() {
        return this.dbs;
    }

    public Vertx refVertx() {
        return this.vertxRef;
    }

    public AsyncMeta metadata() {
        return this.metadata;
    }

    protected JsonObject mapPage(final Pagination<T> page) {
        final JsonObject response = new JsonObject();
        response.put("count", page.getCount());
        response.put("list", this.mapPage(page.getList()));
        return response;
    }

    protected Future<JsonObject> mapPageAsync(final Pagination<T> page) {
        return Future.succeededFuture(mapPage(page));
    }

    protected JsonArray mapPage(final List<T> result) {
        return R2MO.<T, JsonArray>serializeA(result);
    }

    protected JObject wrap(final JsonObject data) {
        return SPI.J(data);
    }

    protected JArray wrap(final JsonArray data) {
        return SPI.A(data);
    }

    protected QTree wrapTree(final JsonObject criteria) {
        return QTree.of(this.wrap(criteria));
    }

    protected QTree wrapTree(final JsonObject criteria, final QSorter sorter) {
        return this.wrapTree(criteria).sortBy(sorter);
    }

    protected <T> Map<String, JsonArray> mapResult(Map<String, List<T>> source, Function<List<T>, JsonArray> mapper) {
        Map<String, JsonArray> result = new ConcurrentHashMap<>();
        if (source != null) {
            for (Map.Entry<String, List<T>> entry : source.entrySet()) {
                String key = entry.getKey();
                List<T> valueList = entry.getValue();

                if (valueList != null) {
                    JsonArray jsonArray = Objects.nonNull(mapper) ? mapper.apply(valueList) : R2MO.serializeA(valueList);
                    result.put(key, jsonArray);
                } else {
                    result.put(key, new JsonArray()); // 空数组
                }
            }
        }
        return result;
    }

    protected <T> Future<Map<String, JsonArray>> mapResultAsync(final Map<String, List<T>> source, Function<List<T>, JsonArray> mapper) {
        return Future.succeededFuture(this.mapResult(source, mapper));
    }
}

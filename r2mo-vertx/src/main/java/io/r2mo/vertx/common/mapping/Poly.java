package io.r2mo.vertx.common.mapping;

import io.r2mo.base.program.R2Vector;
import io.r2mo.typed.cc.Cc;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.List;

/**
 * 单向通道接口，主要针对 {@link R2Vector} 中的数据进行解析，有几种实现：
 * <pre>
 *     1. 输入过程中的 Qr 解析
 *        -> JsonObject -> JsonObject
 *     2. 输入过程中的字段解析
 *        -> JsonObject -> T ( JsonObject )
 *     3. 输出过程中的呈现解析
 *        -> T -> JsonObject
 *     4. Join 过程中的字段解析（复杂模式，连接，辅助连接实现）
 * </pre>
 *
 * @author lang : 2025-10-19
 */
public interface Poly<T, C> {
    Cc<String, Poly<?, ?>> CC_SKELETON = Cc.openThread();

    // 数据库中实体 -> JsonObject/JsonArray 的映射
    @SuppressWarnings("unchecked")
    static <E> Poly<E, List<E>> ofDB(final Class<?> entityCls, final R2Vector vector) {
        return (Poly<E, List<E>>) CC_SKELETON.pick(() -> new PolyDB<>(vector, entityCls), PolyDB.class.getName() + "@" + entityCls.getName());
    }

    // JsonObject/JsonArray -> 数据库中实体
    @SuppressWarnings("unchecked")
    static Poly<JsonObject, JsonArray> ofWeb(final Class<?> entityCls, final R2Vector vector) {
        return (Poly<JsonObject, JsonArray>) CC_SKELETON.pick(() -> new PolyWeb(vector, entityCls), PolyWeb.class.getName() + "@" + entityCls.getName());
    }

    static Poly.Qr ofQr(final Class<?> entityCls, final R2Vector vector) {
        return Qr.CC_SKELETON.pick(() -> new PolyQr(vector, entityCls), PolyQr.class.getName() + "@" + entityCls.getName());
    }

    PolyPhase phase();

    JsonObject mapOne(T input);

    JsonArray mapMany(C input);

    interface Qr {

        Cc<String, Qr> CC_SKELETON = Cc.openThread();

        JsonObject map(JsonObject query);

        JsonObject mapTree(JsonObject tree);

        JsonArray mapSort(JsonArray sorter);

        JsonArray mapProjection(JsonArray projection);
    }
}

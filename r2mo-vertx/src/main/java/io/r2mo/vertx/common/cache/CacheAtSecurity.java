package io.r2mo.vertx.common.cache;

import io.r2mo.jaas.auth.CaptchaArgs;
import io.r2mo.jaas.session.UserAt;
import io.r2mo.jaas.session.UserContext;
import io.r2mo.typed.cc.CacheAt;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;

/**
 * 此处接口结构和 {@see io.r2mo.spring.security.extension.cache.CacheAtSecurity} 保持一致，目的是为了让开发人员在不同框架下使用
 * 同样的类名会更方便记忆，区别：
 * <pre>
 *     1. 返回值类型由同步改为异步 {@link Future} 而不使用同步
 *     2. Key 类型由 {@link java.util.UUID} 改为 {@link String}，这种设计在于Vertx 中的 {@link JsonObject} 数据结构有特殊限制，
 *        它虽然是键值对，但对 {@link java.util.UUID} 做值类型时并不友好，所以这种模式下最好是采用 {@link JsonObject} 能支持的原生
 *        数据类型作为 Key 和 Value。
 *     3. Vertx 中的值类型支持如：
 *        1). 基础 JSON 类型：
 *            - {@link java.lang.String}
 *            - {@link java.lang.Boolean}
 *            - null
 *        2). 数值类型
 *            - {@link java.lang.Integer}
 *            - {@link java.lang.Long}
 *            - {@link java.lang.Float}
 *            - {@link java.lang.Double}
 *        3). 复杂类型
 *            - {@link io.vertx.core.json.JsonObject}
 *            - {@link io.vertx.core.json.JsonArray}
 *        4). 其他类型
 *            - {@link io.vertx.core.buffer.Buffer}
 *            - byte[]
 *            - {@link java.lang.Enum}
 *            - {@link java.time.Instant}
 * </pre>
 * 由于 {@link java.util.UUID} 在 Vertx 中原生支持不太友好，所以统一改为 {@link String} 作为 Key 类型。
 */
public interface CacheAtSecurity {

    Future<CacheAt<String, UserAt>> userAt();

    Future<CacheAt<String, UserContext>> userContext();

    Future<CacheAt<String, String>> userVector();

    Future<CacheAt<String, String>> ofToken();

    Future<CacheAt<String, String>> ofRefresh();

    Future<CacheAt<String, String>> ofAuthorize(CaptchaArgs configuration);
}

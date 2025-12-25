package io.r2mo.vertx.function;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.r2mo.typed.exception.WebException;
import io.r2mo.typed.webflow.WebState;
import io.vertx.core.json.JsonObject;

import java.util.Objects;

/**
 * @author lang : 2025-09-30
 */
class FnAdaptor {

    static JsonObject failJson(final WebException error) {
        final JsonObject failure = new JsonObject();
        final WebState status = error.getStatus();
        final HttpResponseStatus statusValue = status.value();
        failure.put("reason", statusValue.reasonPhrase());      // HTTP 原始原因
        failure.put("status", statusValue.code());              // HTTP 状态码
        failure.put("message", error.getMessage());             // 系统异常消息
        failure.put("code", error.getCode());                   // 系统错误码
        if (Objects.nonNull(error.getInfo())) {
            // 业务数据信息，存在 info 时才提供
            failure.put("info", error.getInfo());
        }
        return failure;
    }
}

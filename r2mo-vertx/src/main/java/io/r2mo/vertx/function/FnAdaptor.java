package io.r2mo.vertx.function;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.r2mo.typed.exception.WebException;
import io.r2mo.typed.webflow.WebState;
import io.vertx.core.json.JsonObject;

/**
 * @author lang : 2025-09-30
 */
class FnAdaptor {

    static JsonObject adapt(final WebException error) {
        final JsonObject failure = new JsonObject();
        final WebState status = error.getStatus();
        final HttpResponseStatus statusValue = status.value();
        failure.put("reason", statusValue.reasonPhrase());
        failure.put("status", statusValue.code());
        failure.put("message", error.getMessage());
        failure.put("code", error.getCode());
        failure.put("info", error.getInfo());
        return failure;
    }
}

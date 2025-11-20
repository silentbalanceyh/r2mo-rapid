package io.github.silentbalanceyh.r2mo.vertx;

import io.github.silentbalanceyh.r2mo.core.HttpRequest;
import io.vertx.ext.web.RoutingContext;

import java.util.HashMap;
import java.util.Map;

/**
 * Vert.x implementation of HttpRequest.
 */
public class VertxHttpRequest implements HttpRequest {

    private final RoutingContext context;

    public VertxHttpRequest(RoutingContext context) {
        this.context = context;
    }

    @Override
    public String getMethod() {
        return context.request().method().name();
    }

    @Override
    public String getPath() {
        return context.request().path();
    }

    @Override
    public String getHeader(String name) {
        return context.request().getHeader(name);
    }

    @Override
    public Map<String, String> getHeaders() {
        Map<String, String> headers = new HashMap<>();
        context.request().headers().forEach(entry -> 
            headers.put(entry.getKey(), entry.getValue())
        );
        return headers;
    }

    @Override
    public String getBody() {
        return context.body().asString();
    }

    @Override
    public String getQueryParam(String name) {
        return context.request().getParam(name);
    }

    @Override
    public Map<String, String> getQueryParams() {
        Map<String, String> params = new HashMap<>();
        context.request().params().forEach(entry -> 
            params.put(entry.getKey(), entry.getValue())
        );
        return params;
    }
}

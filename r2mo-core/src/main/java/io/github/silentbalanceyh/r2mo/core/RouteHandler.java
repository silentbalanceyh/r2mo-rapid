package io.github.silentbalanceyh.r2mo.core;

/**
 * Core interface for route handler abstraction.
 * Provides a unified API for handling HTTP requests.
 */
public interface RouteHandler {

    /**
     * Handle the incoming request and produce a response.
     * 
     * @param request the HTTP request
     * @return the HTTP response
     */
    HttpResponse handle(HttpRequest request);
}

package io.github.silentbalanceyh.r2mo.core;

import java.util.Map;

/**
 * Core interface for HTTP request abstraction.
 */
public interface HttpRequest {

    /**
     * Get the HTTP method (GET, POST, etc.).
     * 
     * @return the HTTP method
     */
    String getMethod();

    /**
     * Get the request path.
     * 
     * @return the path
     */
    String getPath();

    /**
     * Get a header value.
     * 
     * @param name the header name
     * @return the header value, or null if not found
     */
    String getHeader(String name);

    /**
     * Get all headers.
     * 
     * @return map of all headers
     */
    Map<String, String> getHeaders();

    /**
     * Get the request body as string.
     * 
     * @return the body content
     */
    String getBody();

    /**
     * Get a query parameter value.
     * 
     * @param name the parameter name
     * @return the parameter value, or null if not found
     */
    String getQueryParam(String name);

    /**
     * Get all query parameters.
     * 
     * @return map of all query parameters
     */
    Map<String, String> getQueryParams();
}

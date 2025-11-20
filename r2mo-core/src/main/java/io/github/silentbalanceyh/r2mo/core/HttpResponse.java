package io.github.silentbalanceyh.r2mo.core;

import java.util.HashMap;
import java.util.Map;

/**
 * Core interface for HTTP response abstraction.
 */
public interface HttpResponse {

    /**
     * Get the HTTP status code.
     * 
     * @return the status code
     */
    int getStatusCode();

    /**
     * Set the HTTP status code.
     * 
     * @param statusCode the status code
     * @return this response for chaining
     */
    HttpResponse setStatusCode(int statusCode);

    /**
     * Get a header value.
     * 
     * @param name the header name
     * @return the header value, or null if not found
     */
    String getHeader(String name);

    /**
     * Set a header value.
     * 
     * @param name the header name
     * @param value the header value
     * @return this response for chaining
     */
    HttpResponse setHeader(String name, String value);

    /**
     * Get all headers.
     * 
     * @return map of all headers
     */
    Map<String, String> getHeaders();

    /**
     * Get the response body.
     * 
     * @return the body content
     */
    String getBody();

    /**
     * Set the response body.
     * 
     * @param body the body content
     * @return this response for chaining
     */
    HttpResponse setBody(String body);

    /**
     * Create a simple text response.
     * 
     * @param statusCode the HTTP status code
     * @param body the response body
     * @return a new HttpResponse
     */
    static HttpResponse of(int statusCode, String body) {
        return new SimpleHttpResponse(statusCode, body);
    }

    /**
     * Create a 200 OK response.
     * 
     * @param body the response body
     * @return a new HttpResponse
     */
    static HttpResponse ok(String body) {
        return of(200, body);
    }

    /**
     * Create a 404 Not Found response.
     * 
     * @return a new HttpResponse
     */
    static HttpResponse notFound() {
        return of(404, "Not Found");
    }

    /**
     * Create a 500 Internal Server Error response.
     * 
     * @param message the error message
     * @return a new HttpResponse
     */
    static HttpResponse error(String message) {
        return of(500, message);
    }
}

/**
 * Simple implementation of HttpResponse.
 */
class SimpleHttpResponse implements HttpResponse {
    private int statusCode;
    private final Map<String, String> headers;
    private String body;

    public SimpleHttpResponse(int statusCode, String body) {
        this.statusCode = statusCode;
        this.body = body;
        this.headers = new HashMap<>();
        this.headers.put("Content-Type", "text/plain");
    }

    @Override
    public int getStatusCode() {
        return statusCode;
    }

    @Override
    public HttpResponse setStatusCode(int statusCode) {
        this.statusCode = statusCode;
        return this;
    }

    @Override
    public String getHeader(String name) {
        return headers.get(name);
    }

    @Override
    public HttpResponse setHeader(String name, String value) {
        headers.put(name, value);
        return this;
    }

    @Override
    public Map<String, String> getHeaders() {
        return new HashMap<>(headers);
    }

    @Override
    public String getBody() {
        return body;
    }

    @Override
    public HttpResponse setBody(String body) {
        this.body = body;
        return this;
    }
}

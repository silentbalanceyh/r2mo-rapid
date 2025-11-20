package io.github.silentbalanceyh.r2mo.spring;

import io.github.silentbalanceyh.r2mo.core.HttpRequest;
import jakarta.servlet.http.HttpServletRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * Spring implementation of HttpRequest.
 */
public class SpringHttpRequest implements HttpRequest {

    private final HttpServletRequest request;
    private String cachedBody;

    public SpringHttpRequest(HttpServletRequest request) {
        this.request = request;
    }

    @Override
    public String getMethod() {
        return request.getMethod();
    }

    @Override
    public String getPath() {
        return request.getRequestURI();
    }

    @Override
    public String getHeader(String name) {
        return request.getHeader(name);
    }

    @Override
    public Map<String, String> getHeaders() {
        Map<String, String> headers = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            headers.put(name, request.getHeader(name));
        }
        return headers;
    }

    @Override
    public String getBody() {
        if (cachedBody != null) {
            return cachedBody;
        }

        StringBuilder body = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                body.append(line);
            }
        } catch (IOException e) {
            // Return empty string on error
        }
        cachedBody = body.toString();
        return cachedBody;
    }

    @Override
    public String getQueryParam(String name) {
        return request.getParameter(name);
    }

    @Override
    public Map<String, String> getQueryParams() {
        Map<String, String> params = new HashMap<>();
        request.getParameterMap().forEach((name, values) -> {
            if (values.length > 0) {
                params.put(name, values[0]);
            }
        });
        return params;
    }
}

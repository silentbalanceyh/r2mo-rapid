package io.r2mo.spring.common.config;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 全局过滤器：对 application/json 类型的 POST/PUT 请求，
 * 使用 SecurityBodyHttpServletRequest 包装原始请求，
 * 使得请求体可被多次读取（例如在切面和 Controller 中同时使用）。
 * 注意：必须在 Spring Security 和 DispatcherServlet 之前执行。
 *
 * @author lang : 2025-11-13
 */
@Component
@Order(-101) // 确保早于 Spring Security（通常为 -100）和 DispatcherServlet
public class WebBodyFilter implements Filter {

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
        throws IOException, ServletException {

        if (!(request instanceof final HttpServletRequest httpReq)) {
            chain.doFilter(request, response);
            return;
        }

        final String contentType = httpReq.getContentType();
        final String method = httpReq.getMethod();

        // 仅缓存 JSON 格式的写操作请求体（POST/PUT/PATCH）
        final boolean shouldCache = "POST".equalsIgnoreCase(method) ||
            "PUT".equalsIgnoreCase(method) ||
            "PATCH".equalsIgnoreCase(method);

        if (shouldCache && contentType != null && contentType.toLowerCase().contains("application/json")) {
            final WebBodyHttpServletRequest wrappedRequest = new WebBodyHttpServletRequest(httpReq);
            chain.doFilter(wrappedRequest, response);
        } else {
            chain.doFilter(request, response);
        }
    }
}
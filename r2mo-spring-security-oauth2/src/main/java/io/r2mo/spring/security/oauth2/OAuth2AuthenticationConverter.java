package io.r2mo.spring.security.oauth2;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationConverter;

/**
 * OAuth2 自定义 AuthenticationConverter 基类
 *
 * 通过 SPI 扩展，自动发现并注册到 Spring Security
 *
 * @author lang : 2025-11-13
 */
public interface OAuth2AuthenticationConverter extends AuthenticationConverter {

    /**
     * 从 HTTP 请求中提取认证信息
     *
     * @param request HTTP 请求
     *
     * @return 认证对象，如果无法提取则返回 null
     */
    @Override
    Authentication convert(HttpServletRequest request);

    /**
     * 转换器优先级（数字越小优先级越高）
     * 默认为 100
     *
     * @return 优先级
     */
    default int getOrder() {
        return 100;
    }

    /**
     * 是否支持该请求
     *
     * @param request HTTP 请求
     *
     * @return 是否支持
     */
    default boolean supports(final HttpServletRequest request) {
        return true;
    }
}


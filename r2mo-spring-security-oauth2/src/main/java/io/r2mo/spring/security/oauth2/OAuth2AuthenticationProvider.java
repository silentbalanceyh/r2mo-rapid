package io.r2mo.spring.security.oauth2;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

/**
 * OAuth2 自定义 AuthenticationProvider 基类
 *
 * 通过 SPI 扩展，自动发现并注册到 Spring Security
 *
 * @author lang : 2025-11-13
 */
public interface OAuth2AuthenticationProvider extends AuthenticationProvider {

    /**
     * 执行认证
     *
     * @param authentication 认证对象
     *
     * @return 认证后的对象
     * @throws AuthenticationException 认证异常
     */
    @Override
    Authentication authenticate(Authentication authentication) throws AuthenticationException;

    /**
     * 是否支持该认证类型
     *
     * @param authentication 认证类型
     *
     * @return 是否支持
     */
    @Override
    boolean supports(Class<?> authentication);

    /**
     * 提供者优先级（数字越小优先级越高）
     * 默认为 100
     *
     * @return 优先级
     */
    default int getOrder() {
        return 100;
    }
}


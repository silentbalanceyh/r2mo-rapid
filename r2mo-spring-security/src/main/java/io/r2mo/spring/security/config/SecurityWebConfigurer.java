package io.r2mo.spring.security.config;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

/**
 * @author lang : 2025-11-12
 */
public interface SecurityWebConfigurer {

    void configure(HttpSecurity security, final HandlerMappingIntrospector introspector);

    /**
     * 资源服务器如果和认证授权服务器分离，则考虑添加此处的配置，可假设此方法是前置配置，前置配置用于比较高的优先级处理，用来解决
     * OAuth2 中两个 {@link SecurityFilterChain} 的冲突问题，对 OAuth2 而言此处是必须是两个{@link SecurityFilterChain}
     * 同时存在
     * <pre>
     *     1. 一个负责认证授权服务器，且和主干道上的 {@link SecurityFilterChain} 要分离开
     *     2. 一个负责资源服务器，合并到主干道上实现彻底的分离流程
     * </pre>
     *
     * @param security 资源服务器配置
     */
    default void configureHighPriority(final HttpSecurity security, final HandlerMappingIntrospector introspector) {

    }
}

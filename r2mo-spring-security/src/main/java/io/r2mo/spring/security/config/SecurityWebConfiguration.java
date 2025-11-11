package io.r2mo.spring.security.config;

import io.r2mo.spring.security.auth.basic.SpringAuthenticatorBasic;
import io.r2mo.spring.security.extension.SpringAuthenticator;
import io.r2mo.spring.security.extension.valve.RequestValve;
import io.r2mo.spring.security.extension.valve.RequestValveAuth;
import io.r2mo.spring.security.extension.valve.RequestValveIgnore;
import io.r2mo.spring.security.extension.valve.RequestValveStatic;
import io.r2mo.spring.security.extension.valve.RequestValveSwagger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

/**
 * 核心安全配置，基于 Spring Security 实现的基础配置
 * <pre>
 *     1. 认证配置
 *     2. 授权配置
 *     3. 会话管理配置：本地、Redis 扩展
 *        - Token 可选择 Basic / JWT
 *        - 可开启 OAuth2 或可选
 * </pre>
 *
 * @author lang : 2025-11-10
 */
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
@Slf4j
@EnableConfigurationProperties({ConfigSecurity.class, ConfigUser.class})
public class SecurityWebConfiguration {

    private final AccessDeniedHandler deniedHandler;
    private final AuthenticationEntryPoint entryPoint;
    private final ConfigSecurity config;
    // Spring Boot 自动装配
    private final CorsConfigurationSource configCors;

    @Bean
    public SecurityFilterChain resourceFilter(final HttpSecurity http,
                                              final HandlerMappingIntrospector introspector)
        throws Exception {
        // 基础安全配置
        http
            /*
             * - 无状态 Session
             * - 表单模式
             * - CSRF
             * 以上三种方式在 RESTful Api 中没有意义，均予以禁用
             */
            // ---- 使用自动 CORS
            .cors(cors -> cors.configurationSource(this.configCors))
            // ---- 禁用 CSRF
            .csrf(CsrfConfigurer::disable)
            // ---- 禁用表单模式
            .formLogin(AbstractHttpConfigurer::disable)
            // ---- 禁用 Session
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // ---- 自定义异常处理
            .exceptionHandling(exception -> {
                exception.accessDeniedHandler(this.deniedHandler);
                exception.authenticationEntryPoint(this.entryPoint);
            });

        // 加载不同模式的认证器
        if (this.config.isBasic()) {
            // 加载 Basic 认证器
            final SpringAuthenticator authenticator = SpringAuthenticator.of(this.config, SpringAuthenticatorBasic::new);
            authenticator.configure(http, introspector);
            log.info("[ R2MO ] 启用 Basic 认证器");
        }

        // 请求执行链式处理
        http.authorizeHttpRequests(request -> {

            // ignore-uris 处理，第三参为 MvcRequestMatcher.Builder
            final RequestValve valveIgnore = RequestValve.of(RequestValveIgnore::new);
            valveIgnore.execute(request, this.config, this.mvc(introspector));

            // static 静态资源处理，没有第三参
            final RequestValve valveStatic = RequestValve.of(RequestValveStatic::new);
            valveStatic.execute(request, this.config);

            // swagger 资源处理
            final RequestValve valveSwagger = RequestValve.of(RequestValveSwagger::new);
            valveSwagger.execute(request, this.config);

            // auth 资源处理
            final RequestValve valueAuth = RequestValve.of(RequestValveAuth::new);
            valueAuth.execute(request, this.config, this.mvc(introspector));

            // 其他请求都需要执行认证
            request.anyRequest().authenticated();
        }).csrf(AbstractHttpConfigurer::disable);
        return http.build();
    }

    @Bean
    MvcRequestMatcher.Builder mvc(final HandlerMappingIntrospector introspector) {
        return new MvcRequestMatcher.Builder(introspector);
    }

    // 密码编解码器
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}

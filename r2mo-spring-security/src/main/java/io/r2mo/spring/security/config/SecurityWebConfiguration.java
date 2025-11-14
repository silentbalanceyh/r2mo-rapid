package io.r2mo.spring.security.config;

import io.r2mo.spi.SPI;
import io.r2mo.spring.security.auth.UserDetailsCommon;
import io.r2mo.spring.security.basic.BasicSpringAuthenticator;
import io.r2mo.spring.security.extension.AuthSwitcher;
import io.r2mo.spring.security.extension.SpringAuthenticator;
import io.r2mo.spring.security.extension.handler.SecurityHandler;
import io.r2mo.spring.security.extension.valve.RequestValve;
import io.r2mo.spring.security.extension.valve.RequestValveAuth;
import io.r2mo.spring.security.extension.valve.RequestValveIgnore;
import io.r2mo.spring.security.extension.valve.RequestValveStatic;
import io.r2mo.spring.security.extension.valve.RequestValveSwagger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import java.util.List;
import java.util.Objects;

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

    private final SecurityHandler failure;
    private final ConfigSecurity config;

    @Bean
    public SecurityFilterChain resourceFilter(final HttpSecurity http,
                                              final HandlerMappingIntrospector introspector)
        throws Exception {
        // 基础安全配置
        http
            /*
             * 以下配置适用于 RESTful API：
             * - 无状态 Session: RESTful API 通常不需要会话管理，因此禁用它以提高性能和简化架构。
             * - 表单模式: 由于我们正在构建的是 RESTful API 而不是传统的 Web 应用程序，表单登录在这里没有意义，因此被禁用。
             * - CSRF: Cross-Site Request Forgery 防护对于非浏览器发起的请求（如来自移动应用或第三方服务）来说是不必要的，并且可能会导致问题，所以这里也被禁用了。
             *
             * 关于 CORS 配置：
             * - 我们使用了 Spring Security 的默认方式来注册 `CorsConfigurationSource`，通过调用 `Customizer.withDefaults()`，
             *   这将自动查找并使用由 `@Bean` 定义的 `CorsConfigurationSource` 实例。
             *   之前的方式 `cors(cors -> cors.configurationSource(this.configCors))` 是手动指定一个特定的 `CorsConfigurationSource`，
             *   但这可能绕过了 Spring 容器对 Bean 的管理，导致潜在的问题，比如配置未正确加载。
             * - 现在的方法确保了我们的 `CorsConfigurationSource` Bean 能够被 Spring 正确识别和使用，从而保证跨域资源共享策略能够按照预期工作。
             * 旧代码：cors(cors -> cors.configurationSource(this.configCors))
             */
            // ---- 使用自动 CORS
            .cors(Customizer.withDefaults())
            // ---- 禁用 CSRF
            .csrf(CsrfConfigurer::disable)
            // ---- 禁用表单模式
            .formLogin(AbstractHttpConfigurer::disable)
            // ---- 禁用 Session
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // ---- 自定义异常处理
            .exceptionHandling(this.failure.handler());


        final AuthSwitcher switcher = SPI.findOneOf(AuthSwitcher.class);
        final boolean isOAuth2 = Objects.nonNull(switcher) && switcher.hasOAuth2();
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
            if (!isOAuth2) {
                request.anyRequest().authenticated();
            }
        });


        // 加载不同模式的认证器
        if (this.config.isBasic()) {
            // 加载 Basic 认证器
            log.info("[ R2MO ] ----> 执行 `Basic` 配置器");
            final SpringAuthenticator authenticator = SpringAuthenticator.of(this.config, BasicSpringAuthenticator::new);
            authenticator.configure(http, this.failure);
        }


        final List<SecurityWebConfigurer> configurerList = SPI.findMany(SecurityWebConfigurer.class);
        for (final SecurityWebConfigurer configurer : configurerList) {
            log.info("[ R2MO ] ----> 执行 `{}` 配置器", configurer.getClass().getName());
            configurer.configure(http, introspector);
        }

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

    // 用户服务管理器
    @Bean
    public UserDetailsService userService() {
        return new UserDetailsCommon();
    }

    // ✅ 新增：基于 config 动态构建 CorsConfigurationSource 的 Bean
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        final CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(this.config.getCors().getAllowedOriginPatterns());
        configuration.setAllowedMethods(this.config.getCors().getAllowedMethods());
        configuration.setAllowedHeaders(this.config.getCors().getAllowedHeaders());
        configuration.setAllowCredentials(this.config.getCors().isAllowCredentials());

        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // 应用到所有路径（你也可以根据 config.getCors().getPathPatterns() 动态注册）
        for (final String path : this.config.getCors().getPathPatterns()) {
            source.registerCorsConfiguration(path, configuration);
        }
        return source;
    }
}

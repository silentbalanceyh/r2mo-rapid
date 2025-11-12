// io/r2mo/spring/security/extension/valve/RequestValveAuth.java
package io.r2mo.spring.security.extension.valve;

import io.r2mo.spi.SPI;
import io.r2mo.spring.security.config.ConfigSecurity;
import io.r2mo.spring.security.extension.RequestSkip;
import io.r2mo.typed.common.Kv;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * @author lang : 2025-11-11
 */
@Slf4j
public class RequestValveAuth implements RequestValve {
    @Override
    public void execute(final AuthorizeHttpRequestsConfigurer<?>.AuthorizationManagerRequestMatcherRegistry registry,
                        final ConfigSecurity config, final Object attached) {

        if (!(attached instanceof final MvcRequestMatcher.Builder builder)) {
            log.warn("[ R2MO ] 无法处理公开认证 URI，附加对象类型错误：{} / MvcRequestMatcher.Builder", attached.getClass().getName());
            return;
        }


        // 定义需要公开访问的认证相关路径（默认为 POST 方法）
        final List<String> authUris = new ArrayList<>();


        // 限定 SPI 注册的 URI 路径，只要加载则忽略
        final List<RequestSkip> found = SPI.findMany(RequestSkip.class);
        for (final RequestSkip item : found) {
            final Set<String> ignoreUris = item.openApi(config);
            log.info("[ R2MO ] SPI 组件：{} 注册公开访问URI: {}", item.getClass().getName(), ignoreUris);
            authUris.addAll(ignoreUris);
        }


        final List<Kv<String, HttpMethod>> ignoreUris = ConfigSecurity.ignoreUris(authUris, HttpMethod.POST);
        // 配置这些路径为 permitAll，并且只匹配 POST 方法
        for (final Kv<String, HttpMethod> kv : ignoreUris) {
            final String uri = kv.key();
            final MvcRequestMatcher matcher = builder.pattern(uri);
            final HttpMethod method = kv.value();
            if (Objects.nonNull(method)) {
                matcher.setMethod(method);
            }
            log.info("[ R2MO ] 公开访问的 URI: `{} {}`", Objects.isNull(method) ? "*" : method, uri);
            registry.requestMatchers(matcher).permitAll();
        }


        /*
         * 所有方法都包含，主要是为了支持 OPTIONS 预检请求以及
         * - GET /auth/captcha: 获取验证码
         */
        final MvcRequestMatcher matcher = builder.pattern("/auth/**");
        registry.requestMatchers(matcher).permitAll();
        log.info("[ R2MO ] 公开访问的 URI: `* /auth/**`");
    }
}
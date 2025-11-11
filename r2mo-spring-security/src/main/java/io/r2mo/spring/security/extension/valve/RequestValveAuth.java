// io/r2mo/spring/security/extension/valve/RequestValveAuth.java
package io.r2mo.spring.security.extension.valve;

import io.r2mo.spi.SPI;
import io.r2mo.spring.security.config.ConfigSecurity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;

import java.util.ArrayList;
import java.util.List;
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
            log.warn("Attached object is not MvcRequestMatcher.Builder, skipping RequestValveAuth execution.");
            return;
        }

        // 定义需要公开访问的认证相关路径（默认为 POST 方法）
        final List<String> authUris = new ArrayList<>();
        authUris.add("/auth/**");

        // 限定 SPI 注册的 URI 路径，只要加载则忽略
        final List<RequestSkip> found = SPI.findMany(RequestSkip.class);
        for (final RequestSkip item : found) {
            final Set<String> ignoreUris = item.openApi(config);
            log.info("[ R2MO ] SPI 组件：{} 注册公开访问URI: {}", item.getClass().getName(), ignoreUris);
            authUris.addAll(ignoreUris);
        }

        // 配置这些路径为 permitAll，并且只匹配 POST 方法
        for (final String uri : authUris) {
            final MvcRequestMatcher matcher = builder.pattern(HttpMethod.POST, uri);
            log.info("[ R2MO ] 公开访问的认证 URI (POST): `{}`", uri);
            registry.requestMatchers(matcher).permitAll();
        }
    }
}
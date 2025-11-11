package io.r2mo.spring.security.extension.valve;

import cn.hutool.core.util.StrUtil;
import io.r2mo.spring.security.config.ConfigSecurity;
import io.r2mo.typed.common.Kv;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;

import java.util.List;
import java.util.Objects;

/**
 * @author lang : 2025-11-11
 */
@Slf4j
public class RequestValveIgnore implements RequestValve {
    @Override
    public void execute(final AuthorizeHttpRequestsConfigurer<?>.AuthorizationManagerRequestMatcherRegistry registry,
                        final ConfigSecurity config, final Object attached) {
        if (!(attached instanceof final MvcRequestMatcher.Builder builder)) {
            return;
        }
        final List<Kv<String, HttpMethod>> ignoreUris = config.loadIgnoreUris();
        ignoreUris.stream()
            .filter(item -> StrUtil.isNotEmpty(item.key()))
            .forEach(item -> {
                final String uri = item.key();
                final HttpMethod method = item.value();
                final MvcRequestMatcher pattern = builder.pattern(uri);
                if (Objects.nonNull(method)) {
                    pattern.setMethod(method);
                }

                // {METHOD} {URI} 忽略认证相关信息
                log.info("[ R2MO ] 忽略认证的 URI：`{} {}`", Objects.isNull(method) ? "*" : method, uri);
                registry.requestMatchers(pattern).permitAll();
            });
    }
}

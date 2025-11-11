package io.r2mo.spring.security.extension.valve;

import io.r2mo.spring.security.config.ConfigSecurity;
import io.r2mo.typed.cc.Cc;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * 规则阀，用于处理不同规则之下的请求允许或禁止，细化到每种规则的核心逻辑
 *
 * @author lang : 2025-11-11
 */
public interface RequestValve {
    Cc<String, RequestValve> CCT_VALVE = Cc.openThread();

    static RequestValve of(final Supplier<RequestValve> constructorFn) {
        Objects.requireNonNull(constructorFn, "[ R2MO ] - constructorFn 不能为 null");
        return CCT_VALVE.pick(constructorFn, String.valueOf(constructorFn.hashCode()));
    }

    void execute(AuthorizeHttpRequestsConfigurer<?>.AuthorizationManagerRequestMatcherRegistry registry,
                 final ConfigSecurity config, final Object attached);

    default void execute(final AuthorizeHttpRequestsConfigurer<?>.AuthorizationManagerRequestMatcherRegistry registry,
                         final ConfigSecurity config) {
        this.execute(registry, config, null);
    }
}

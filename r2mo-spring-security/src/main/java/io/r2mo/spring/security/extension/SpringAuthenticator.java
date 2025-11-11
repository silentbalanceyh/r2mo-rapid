package io.r2mo.spring.security.extension;

import io.r2mo.spring.security.config.ConfigSecurity;
import io.r2mo.typed.cc.Cc;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import java.util.function.Function;

/**
 * 加载不同的认证模式处理 {@link HttpSecurity} 配置
 *
 * @author lang : 2025-11-11
 */
public interface SpringAuthenticator {

    Cc<String, SpringAuthenticator> CCT_AUTHENTICATOR = Cc.openThread();

    static SpringAuthenticator of(final ConfigSecurity config, final Function<ConfigSecurity, SpringAuthenticator> constructorFn) {
        final String cacheKey = config.hashCode() + "@" + constructorFn.hashCode();
        return CCT_AUTHENTICATOR.pick(() -> constructorFn.apply(config), cacheKey);
    }

    void configure(HttpSecurity security, Object attached);
}

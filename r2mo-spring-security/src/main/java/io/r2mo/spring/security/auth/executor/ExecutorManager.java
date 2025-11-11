package io.r2mo.spring.security.auth.executor;

import cn.hutool.extra.spring.SpringUtil;
import io.r2mo.jaas.auth.LoginRequest;
import io.r2mo.spring.security.config.ConfigSecurity;

/**
 * @author lang : 2025-11-11
 */
public class ExecutorManager {
    private static ExecutorManager MANAGER;
    private final ConfigSecurity configuration;

    private ExecutorManager() {
        this.configuration = SpringUtil.getBean(ConfigSecurity.class);
    }

    public static ExecutorManager of() {
        if (MANAGER == null) {
            MANAGER = new ExecutorManager();
        }
        return MANAGER;
    }

    public PreAuthService authorizeProvider(final LoginRequest request) {
        return null;
    }

    public UserAtService userProvider(final LoginRequest request) {
        return null;
    }
}

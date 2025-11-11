package io.r2mo.spring.security.auth.executor;

import cn.hutool.extra.spring.SpringUtil;
import io.r2mo.jaas.enums.UserIDType;
import io.r2mo.typed.exception.web._404NotFoundException;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * @author lang : 2025-11-11
 */
@Slf4j
public class ExecutorManager {
    private static ExecutorManager MANAGER;

    private ExecutorManager() {
    }

    public static ExecutorManager of() {
        if (MANAGER == null) {
            MANAGER = new ExecutorManager();
        }
        return MANAGER;
    }

    public ServicePreAuth authorizeProvider(final UserIDType type) {
        // 查找匹配的认证提供者
        final String providerName = "PreAuth/" + type.name();
        final ServicePreAuth found = SpringUtil.getBean(providerName, ServicePreAuth.class);
        if (Objects.isNull(found)) {
            throw new _404NotFoundException("[ R2MO ] 未找到匹配的验证码提供者：BeanName = " + providerName);
        }
        log.info("[ R2MO ] 验证码提供者：BeanName = {}, provider = {}", providerName, found.getClass().getName());
        return found;
    }

    public ServiceUserAt userProvider(final UserIDType type) {
        // 查找匹配的用户提供者
        final String providerName = "UserAt/" + type;
        final ServiceUserAt found = SpringUtil.getBean(providerName, ServiceUserAt.class);
        if (Objects.isNull(found)) {
            throw new _404NotFoundException("[ R2MO ] 未找到匹配的用户提供者：BeanName = " + providerName);
        }
        log.info("[ R2MO ] 用户提供者：BeanName = {}, provider = {}", providerName, found.getClass().getName());
        return found;
    }
}

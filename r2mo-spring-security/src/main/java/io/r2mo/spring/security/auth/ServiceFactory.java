package io.r2mo.spring.security.auth;

import cn.hutool.extra.spring.SpringUtil;
import io.r2mo.typed.cc.Cc;
import io.r2mo.typed.enums.TypeLogin;
import io.r2mo.typed.exception.web._404NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;

/**
 * @author lang : 2025-11-11
 */
@Slf4j
public class ServiceFactory {
    private static final Cc<String, ServicePreAuth> CC_PRE_AUTH = Cc.open();
    private static final Cc<String, ServiceUserAt> CC_USER_AT = Cc.open();
    private static ServiceFactory MANAGER;

    private ServiceFactory() {
    }

    public static ServiceFactory of() {
        if (MANAGER == null) {
            MANAGER = new ServiceFactory();
        }
        return MANAGER;
    }

    public ServicePreAuth authorizeProvider(final TypeLogin type) {
        // 查找匹配的认证提供者
        final String providerName = "PreAuth/" + type.name();
        return CC_PRE_AUTH.pick(() -> {
            try {
                final ServicePreAuth found = SpringUtil.getBean(providerName, ServicePreAuth.class);
                log.info("[ R2MO ] 验证码提供者：BeanName = {}, provider = {}", providerName, found.getClass().getName());
                return found;
            } catch (final NoSuchBeanDefinitionException ex) {
                throw new _404NotFoundException(
                    "[ R2MO ] 验证码提供者未配置：BeanName = " + providerName + ", details = " + ex.getMessage()
                );
            }
        }, providerName);
    }

    public ServiceUserAt userProvider(final TypeLogin type) {
        // 查找匹配的用户提供者
        final String providerName = "UserAt/" + type;
        return CC_USER_AT.pick(() -> {
            try {
                final ServiceUserAt found = SpringUtil.getBean(providerName, ServiceUserAt.class);
                log.info("[ R2MO ] 用户提供者：BeanName = {}, provider = {}", providerName, found.getClass().getName());
                return found;
            } catch (final NoSuchBeanDefinitionException ex) {
                throw new _404NotFoundException(
                    "[ R2MO ] 用户提供者未配置：BeanName = " + providerName + ", details = " + ex.getMessage()
                );
            }
        }, providerName);
    }
}

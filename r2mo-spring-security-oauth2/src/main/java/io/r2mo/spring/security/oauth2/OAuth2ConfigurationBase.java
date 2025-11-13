package io.r2mo.spring.security.oauth2;

import io.r2mo.spring.security.oauth2.config.ConfigSecurityOAuth2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * OAuth2 配置基类
 * <p>
 * 提供通用的配置检查逻辑，避免每个配置类重复检查
 *
 * @author lang : 2025-11-13
 */
@Slf4j
public abstract class OAuth2ConfigurationBase {

    @Autowired(required = false)
    protected ConfigSecurityOAuth2 oauth2Config;

    /**
     * 检查 OAuth2 是否启用
     *
     * @return true 如果 OAuth2 已启用
     */
    protected boolean isOAuth2Enabled() {
        return this.oauth2Config != null && this.oauth2Config.isOn();
    }

    /**
     * 如果 OAuth2 未启用则返回 null，否则执行提供的逻辑
     *
     * @param supplier Bean 构建逻辑
     * @param <T>      Bean 类型
     *
     * @return Bean 实例或 null
     */
    protected <T> T configureIfEnabled(final BeanSupplier<T> supplier) {
        if (!this.isOAuth2Enabled()) {
            return null;
        }
        return supplier.get();
    }

    /**
     * Bean 供应商函数式接口
     */
    @FunctionalInterface
    protected interface BeanSupplier<T> {
        T get();
    }
}


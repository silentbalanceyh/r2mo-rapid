package io.r2mo.spring.security.auth.basic;

import io.r2mo.spring.security.config.ConfigSecurity;
import io.r2mo.spring.security.config.ConfigSecurityBasic;
import io.r2mo.spring.security.extension.SpringAuthenticatorBase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;

import java.util.Objects;

/**
 * Basic认证实现类，根据ConfigSecurity中的Basic配置启用Basic认证
 * 实现自定义的用户认证逻辑，并在认证成功后将会话信息存储到UserSession中
 *
 * @author lang : 2025-11-11
 */
@Slf4j
public class BasicSpringAuthenticator extends SpringAuthenticatorBase {

    public BasicSpringAuthenticator(final ConfigSecurity configuration) {
        super(configuration);
    }

    @Override
    public void configure(final HttpSecurity security, final Object attached) {
        // 检查 Basic 是否存在或启用（双重检查）
        if (!this.config().isBasic()) {
            log.warn("[ R2MO ] Basic 认证未启用，跳过 Basic 认证配置");
            return;
        }

        // 创建 Basic 认证入口
        final ConfigSecurityBasic basic = this.config().getBasic();
        final BasicAuthenticationEntryPoint entryPoint = new BasicAuthenticationEntryPoint();
        entryPoint.setRealmName(basic.getRealm());

        // 提取 UserDetailsService
        final UserDetailsService service = this.userService();
        if (Objects.isNull(service)) {
            log.error("[ R2MO ] 未找到 UserDetailsService 实现类，无法启用 Basic 认证");
            return;
        }

        try {
            security
                .httpBasic(basicSecurity -> basicSecurity.authenticationEntryPoint(entryPoint));
        } catch (final Exception ex) {
            log.error("[ R2MO ] Basic 认证配置失败", ex);
        }
    }
}
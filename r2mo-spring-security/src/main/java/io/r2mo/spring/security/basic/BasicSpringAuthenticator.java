package io.r2mo.spring.security.basic;

import io.r2mo.jaas.enums.TypeToken;
import io.r2mo.spring.security.config.ConfigSecurity;
import io.r2mo.spring.security.config.ConfigSecurityBasic;
import io.r2mo.spring.security.extension.SpringAuthenticatorBase;
import io.r2mo.spring.security.extension.handler.SecurityEntryPoint;
import io.r2mo.spring.security.extension.handler.SecurityHandler;
import io.r2mo.spring.security.token.TokenBuilderManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;

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
        // 附加对象检查
        if (!(attached instanceof final SecurityHandler handler)) {
            log.error("[ R2MO ] 期望 SecurityHandler 类型错误，无法启用 Basic 认证");
            return;
        }

        // 创建 Basic 认证入口
        final ConfigSecurityBasic basic = this.config().getBasic();
        final SecurityEntryPoint entryPoint = SecurityEntryPoint.of(
            // 此处直接参考 BasicAuthenticateEntryPoint 实现
            basicResponse -> basicResponse.setHeader("WWW-Authenticate", "Basic realm=\"" + basic.getRealm() + "\"")
        );

        // 提取 UserDetailsService
        final UserDetailsService service = this.userService();
        if (Objects.isNull(service)) {
            log.error("[ R2MO ] 未找到 UserDetailsService 实现类，无法启用 Basic 认证");
            return;
        }

        // 注册 Basic 的 Token 配置
        TokenBuilderManager.of().registry(TypeToken.BASIC, BasicTokenBuilder::new);

        try {
            security
                .httpBasic(basicSecurity -> basicSecurity.authenticationEntryPoint(entryPoint))
                .userDetailsService(service)
                .authenticationProvider(new BasicAuthenticateProvider(service));
            log.info("[ R2MO ] ( Auth ) Basic 认证配置完成！");
        } catch (final Exception ex) {
            log.error("[ R2MO ] Basic 认证配置失败", ex);
        }
    }
}
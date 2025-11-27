package io.r2mo.spring.security.oauth2.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;

/**
 * @author lang : 2025-11-27
 */
@Configuration
public class ConfigOAuth2Debugger {
    @Bean
    public CommandLineRunner printAuthSettings(final AuthorizationServerSettings settings) {
        return args -> {
            System.out.println("=========================================");
            System.out.println("🕵️‍♂️ [R2MO 调试] 当前生效的配置信息：");
            System.out.println("-----------------------------------------");
            System.out.println("👉 授权端点 (Authorize): " + settings.getAuthorizationEndpoint());
            System.out.println("👉 令牌端点 (Token):     " + settings.getTokenEndpoint());
            System.out.println("👉 发行者 URL (Issuer):  " + settings.getIssuer());
            System.out.println("=========================================");
        };
    }
}

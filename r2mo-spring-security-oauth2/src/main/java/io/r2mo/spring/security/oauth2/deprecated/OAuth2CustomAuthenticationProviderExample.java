package io.r2mo.spring.security.oauth2.deprecated;

import io.r2mo.spring.security.oauth2.OAuth2AuthenticationProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

/**
 * OAuth2 自定义 AuthenticationProvider 示例（仅做参考）
 *
 * 通过 SPI 扩展机制，可以创建自定义的 AuthenticationProvider
 * 并自动注册到 Spring Security 的 OAuth2 配置中
 *
 * 使用步骤：
 * 1. 创建自定义的 Authentication Token 类
 * 2. 实现此接口，处理特定类型的 Authentication
 * 3. 在 META-INF/services/io.r2mo.spring.security.oauth2.OAuth2AuthenticationProvider 中注册
 *
 * 注意：
 * - 此类仅作为示例模板，不应直接使用
 * - 实际使用需要根据业务需求实现具体的认证逻辑
 * - 需要配合自定义的 AuthenticationConverter 使用
 *
 * @author lang : 2025-11-13
 */
@Slf4j
public class OAuth2CustomAuthenticationProviderExample implements OAuth2AuthenticationProvider {

    /**
     * 示例构造函数
     * 可以注入需要的 Bean，如 UserDetailsService, PasswordEncoder 等
     */
    public OAuth2CustomAuthenticationProviderExample() {
        log.info("[ R2MO ] OAuth2 自定义 Provider 示例已加载（此为模板，请勿直接使用）");
    }

    @Override
    public Authentication authenticate(final Authentication authentication) throws AuthenticationException {
        // TODO: 实现自定义的认证逻辑
        // 1. 检查 authentication 类型是否为自己支持的类型
        // 2. 提取认证信息（用户名、密码等）
        // 3. 验证凭证
        // 4. 返回认证成功的 Authentication 对象

        log.debug("[ R2MO ] OAuth2 自定义 Provider 示例 - authenticate 方法被调用");

        // 如果不支持该类型，返回 null，让其他 Provider 处理
        return null;
    }

    @Override
    public boolean supports(final Class<?> authentication) {
        // TODO: 指定支持的 Authentication 类型
        // 例如：return MyCustomAuthenticationToken.class.isAssignableFrom(authentication);

        return false;  // 示例默认不支持任何类型
    }

    @Override
    public int getOrder() {
        // 返回优先级，数字越小优先级越高
        return 100;
    }
}


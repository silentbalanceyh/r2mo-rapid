package io.r2mo.spring.security.oauth2.deprecated;

import io.r2mo.spring.security.oauth2.OAuth2AuthenticationConverter;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;

/**
 * OAuth2 自定义 AuthenticationConverter 示例（仅做参考）
 *
 * 通过 SPI 扩展机制，可以创建自定义的 AuthenticationConverter
 * 从 HTTP 请求中提取认证信息并转换为 Authentication 对象
 *
 * 使用步骤：
 * 1. 创建自定义的 Authentication Token 类
 * 2. 实现此接口，从 HttpServletRequest 中提取认证参数
 * 3. 在 META-INF/services/io.r2mo.spring.security.oauth2.OAuth2AuthenticationConverter 中注册
 *
 * 注意：
 * - 此类仅作为示例模板，不应直接使用
 * - 实际使用需要根据业务需求实现具体的转换逻辑
 * - 需要配合自定义的 AuthenticationProvider 使用
 *
 * @author lang : 2025-11-13
 */
@Slf4j
public class OAuth2CustomAuthenticationConverterExample implements OAuth2AuthenticationConverter {

    /**
     * 示例构造函数
     */
    public OAuth2CustomAuthenticationConverterExample() {
        log.info("[ R2MO ] OAuth2 自定义 Converter 示例已加载（此为模板，请勿直接使用）");
    }

    @Override
    public Authentication convert(final HttpServletRequest request) {
        // TODO: 实现自定义的转换逻辑
        // 1. 检查请求是否符合自己处理的条件（如特定的 grant_type）
        // 2. 从 request 中提取认证参数
        // 3. 创建并返回自定义的 Authentication Token

        log.debug("[ R2MO ] OAuth2 自定义 Converter 示例 - convert 方法被调用");

        // 示例：从请求中获取参数
        // final String grantType = request.getParameter("grant_type");
        // final String username = request.getParameter("username");
        // final String password = request.getParameter("password");

        // 示例：创建自定义 Token
        // return new MyCustomAuthenticationToken(username, password);

        // 如果不支持该请求，返回 null，让其他 Converter 处理
        return null;
    }

    @Override
    public int getOrder() {
        // 返回优先级，数字越小优先级越高
        return 100;
    }

    @Override
    public boolean supports(final HttpServletRequest request) {
        // TODO: 判断是否支持该请求
        // 例如：检查特定的 grant_type 或其他参数

        // final String grantType = request.getParameter("grant_type");
        // return "my_custom_grant".equals(grantType);

        return false;  // 示例默认不支持任何请求
    }
}


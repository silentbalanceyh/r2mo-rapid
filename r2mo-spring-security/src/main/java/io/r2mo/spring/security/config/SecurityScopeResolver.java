package io.r2mo.spring.security.config;

import io.r2mo.spi.SPI;
import io.r2mo.typed.domain.BaseScope;
import io.r2mo.typed.json.JObject;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.nio.charset.StandardCharsets;

/**
 * @author lang : 2025-11-11
 */
@Component
public class SecurityScopeResolver implements HandlerMethodArgumentResolver {

    @Autowired
    private ConfigSecurity configSecurity;

    @Override
    public boolean supportsParameter(final MethodParameter parameter) {
        return JObject.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(@NonNull final MethodParameter parameter,
                                  final ModelAndViewContainer mavContainer,
                                  final NativeWebRequest webRequest,
                                  final WebDataBinderFactory binderFactory) throws Exception {

        final HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        if (request == null) {
            throw new IllegalStateException("[R2MO] 缺少 HttpServletRequest 上下文");
        }

        // 1. 从 Header 读取 Scope
        final String appId = request.getHeader(BaseScope.X_APP_ID);
        final String tenantId = request.getHeader(BaseScope.X_TENANT_ID);

        // 2. 根据 security.scope 配置决定是否校验
        final ConfigSecurityScope scopeConfig = this.configSecurity.getScope();
        if (scopeConfig != null) {
            if (scopeConfig.isApp() && appId == null) {
                throw new IllegalArgumentException("[R2MO] 缺少必需的请求头：X-App-Id");
            }
            if (scopeConfig.isTenant() && tenantId == null) {
                throw new IllegalArgumentException("[R2MO] 缺少必需的请求头：X-Tenant-Id");
            }
        }

        // 3. 读取 Body（仅包含 id, credential, captcha 等）
        final String body = new String(request.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        final JObject bodyData = SPI.J(body);
        bodyData.put("appId", appId);
        bodyData.put("tenantId", tenantId);
        return bodyData;
    }
}

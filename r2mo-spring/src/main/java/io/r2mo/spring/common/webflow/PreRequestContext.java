package io.r2mo.spring.common.webflow;

import io.r2mo.base.web.entity.BaseScope;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Objects;
import java.util.UUID;

/**
 * @author lang : 2025-09-09
 */
class PreRequestContext {
    private final ServletRequestAttributes attributes;
    private final HttpServletRequest request;

    PreRequestContext() {
        this.attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        Objects.requireNonNull(this.attributes, "[ R2MO ] 无法获取请求上下文！");
        this.request = this.attributes.getRequest();
    }

    boolean isOk() {
        return Objects.nonNull(this.request);
    }

    HttpServletRequest request() {
        return this.request;
    }

    String sessionId() {
        return this.attributes.getSessionId();
    }

    @SuppressWarnings("unchecked")
    <T> T appId(final boolean isUuid) {
        final String appId = this.request().getHeader(BaseScope.X_APP_ID);
        if (Objects.nonNull(appId)) {
            return isUuid ? (T) UUID.fromString(appId) : (T) appId;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    <T> T tenantId(final boolean isUuid) {
        final String tenantId = this.request().getHeader(BaseScope.X_TENANT_ID);
        if (Objects.nonNull(tenantId)) {
            return isUuid ? (T) UUID.fromString(tenantId) : (T) tenantId;
        }
        return null;
    }

    UUID userId() {
        return null;
    }
}

package io.r2mo.spring.common.webflow;

import io.r2mo.typed.domain.BaseScope;
import io.r2mo.typed.domain.ContextOr;
import io.r2mo.typed.domain.ContextWeb;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Objects;
import java.util.UUID;

/**
 * @author lang : 2025-09-09
 */
class PreRequestContext implements
    // idApp, idUser, idTenant
    ContextOr,
    // webOk, webSession
    // webRequest, webResponse
    ContextWeb {
    private final ServletRequestAttributes attributes;
    private final HttpServletRequest request;

    PreRequestContext() {
        this.attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        Objects.requireNonNull(this.attributes, "[ R2MO ] 无法获取请求上下文！");
        this.request = this.attributes.getRequest();
    }

    @Override
    public boolean webOk() {
        return Objects.nonNull(this.request);
    }

    @Override
    public HttpServletRequest webRequest() {
        return this.request;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T webSession(final boolean isObj) {
        if (isObj) {
            return (T) this.request.getSession(true);
        } else {
            return (T) this.attributes.getSessionId();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T idApp(final boolean isUuid) {
        final String appId = this.webRequest().getHeader(BaseScope.X_APP_ID);
        if (Objects.nonNull(appId)) {
            return isUuid ? (T) UUID.fromString(appId) : (T) appId;
        }
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T idTenant(final boolean isUuid) {
        final String tenantId = this.webRequest().getHeader(BaseScope.X_TENANT_ID);
        if (Objects.nonNull(tenantId)) {
            return isUuid ? (T) UUID.fromString(tenantId) : (T) tenantId;
        }
        return null;
    }

    @Override
    public <T> T idUser(final boolean isUuid) {
        return null;
    }
}

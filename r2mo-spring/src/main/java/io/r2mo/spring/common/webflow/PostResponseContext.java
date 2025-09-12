package io.r2mo.spring.common.webflow;

import io.r2mo.typed.domain.ContextWeb;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Objects;

/**
 * @author lang : 2025-09-09
 */
class PostResponseContext implements ContextWeb {
    private final ServletRequestAttributes attributes;
    private final HttpServletResponse response;

    PostResponseContext() {
        this.attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        Objects.requireNonNull(this.attributes, "[ R2MO ] 无法获取响应上下文！");
        this.response = this.attributes.getResponse();
    }

    @Override
    public boolean webOk() {
        return Objects.nonNull(this.response);
    }

    @Override
    public HttpServletResponse webResponse() {
        return this.response;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T webSession(final boolean isObj) {
        if (isObj) {
            final HttpServletRequest request = this.attributes.getRequest();
            return (T) request.getSession(true);
        } else {
            return (T) this.attributes.getSessionId();
        }
    }
}

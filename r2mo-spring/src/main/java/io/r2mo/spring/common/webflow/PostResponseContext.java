package io.r2mo.spring.common.webflow;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Objects;

/**
 * @author lang : 2025-09-09
 */
class PostResponseContext {
    private final ServletRequestAttributes attributes;
    private final HttpServletResponse response;

    PostResponseContext() {
        this.attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        Objects.requireNonNull(this.attributes, "[ R2MO ] 无法获取响应上下文！");
        this.response = this.attributes.getResponse();
    }

    boolean isOk() {
        return Objects.nonNull(this.response);
    }

    HttpServletResponse response() {
        return this.response;
    }

    String sessionId() {
        return this.attributes.getSessionId();
    }
}

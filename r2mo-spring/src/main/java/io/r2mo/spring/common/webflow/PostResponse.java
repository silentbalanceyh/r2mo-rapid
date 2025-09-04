package io.r2mo.spring.common.webflow;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author lang : 2025-09-04
 */
public class PostResponse implements Serializable {

    @Schema(hidden = true)
    @Getter
    @Setter(AccessLevel.NONE)
    private HttpServletResponse response;

    @Schema(description = "会话ID", hidden = true)
    @Getter
    private String sessionId;

    public PostResponse() {
        final ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (Objects.nonNull(attributes)) {
            this.response = attributes.getResponse();
            this.sessionId = attributes.getSessionId();
        }
    }

    protected void readFrom(final Object source) {
        final CopyOptions copyOptions = new CopyOptions()
            .ignoreNullValue()
            .ignoreError();
        BeanUtil.copyProperties(source, this, copyOptions);
    }
}

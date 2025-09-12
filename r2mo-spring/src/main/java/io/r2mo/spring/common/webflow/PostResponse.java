package io.r2mo.spring.common.webflow;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author lang : 2025-09-04
 */
@Data
public class PostResponse implements Serializable {

    @Schema(hidden = true)
    @JsonIgnore
    private final HttpServletResponse response;
    @Schema(hidden = true)
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @JsonIgnore
    private PostResponseContext context = new PostResponseContext();
    @Schema(description = "会话ID", hidden = true)
    @JsonIgnore
    private String sessionId;

    public PostResponse() {
        this.response = this.context.webResponse();
        this.sessionId = this.context.webSession(false);
    }

    protected void readFrom(final Object source) {
        final CopyOptions copyOptions = new CopyOptions()
            .ignoreNullValue()
            .ignoreError();
        BeanUtil.copyProperties(source, this, copyOptions);
    }
}

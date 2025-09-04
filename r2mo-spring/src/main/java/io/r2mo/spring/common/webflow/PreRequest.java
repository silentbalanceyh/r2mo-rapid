package io.r2mo.spring.common.webflow;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import io.r2mo.base.web.entity.BaseAudit;
import io.r2mo.base.web.entity.BaseScope;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * @author lang : 2025-09-04
 */
@Data
public class PreRequest implements Serializable {

    @Schema(description = "关联App", hidden = true)
    private UUID appId;     // X-App-Id

    @Schema(description = "关联租户", hidden = true)
    private UUID tenantId;  // X-Tenant-Id

    @Schema(hidden = true)
    @Getter
    @Setter(AccessLevel.NONE)
    private HttpServletRequest request;

    @Schema(description = "会话ID", hidden = true)
    private String sessionId;

    public PreRequest() {
        final ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (Objects.nonNull(attributes)) {
            this.request = attributes.getRequest();
            this.sessionId = attributes.getSessionId();
        }
    }

    protected void writeAudit(final BaseAudit entity, final boolean created) {
        Objects.requireNonNull(entity, "[ R2MO ] -> 传入实体不可为 null");
        final LocalDateTime processedAt = LocalDateTime.now();
        entity.setUpdatedAt(processedAt);
        entity.setUpdatedBy(this.userId());
        if (created) {
            entity.setCreatedAt(processedAt);
            entity.setCreatedBy(this.userId());
        }
    }

    protected void writeScope(final BaseScope entity) {
        if (Objects.nonNull(this.request)) {
            // appId
            final String appId = this.request.getHeader(BaseScope.X_APP_ID);
            entity.app(appId);
            // tenantId
            final String tenantId = this.request.getHeader(BaseScope.X_TENANT_ID);
            entity.tenant(tenantId);
        }
    }

    protected void writeTo(final Object target) {
        final CopyOptions copyOptions = new CopyOptions()
            .ignoreNullValue()
            .ignoreError();
        BeanUtil.copyProperties(this, target, copyOptions);
    }

    private UUID userId() {
        return null;
    }
}

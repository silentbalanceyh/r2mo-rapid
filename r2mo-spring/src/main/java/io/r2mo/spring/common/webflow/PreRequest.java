package io.r2mo.spring.common.webflow;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.util.StrUtil;
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
            // Fix issue: 默认请求中不带 appId 和 tenantId
            // 构造时填充 this.appId
            final String appId = this.request.getHeader(BaseScope.X_APP_ID);
            if (StrUtil.isNotEmpty(appId)) {
                this.appId = UUID.fromString(appId);
            }
            // 构造时填充 this.tenantId
            final String tenantId = this.request.getHeader(BaseScope.X_TENANT_ID);
            if (StrUtil.isNotEmpty(tenantId)) {
                this.tenantId = UUID.fromString(tenantId);
            }
        }
    }

    protected void writeAudit(final Object entityObj, final boolean created) {
        Objects.requireNonNull(entityObj, "[ R2MO ] -> 传入实体不可为 null");
        if (entityObj instanceof final BaseAudit entity) {

            final LocalDateTime processedAt = LocalDateTime.now();
            entity.setUpdatedAt(processedAt);
            entity.setUpdatedBy(this.userId());
            if (created) {
                entity.setCreatedAt(processedAt);
                entity.setCreatedBy(this.userId());
            }
        }
    }

    protected void writeScope(final Object entityObj) {
        if (Objects.nonNull(this.request) && entityObj instanceof final BaseScope entity) {
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

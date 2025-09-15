package io.r2mo.typed.domain.extension;

import cn.hutool.core.util.StrUtil;
import io.r2mo.typed.domain.BaseAudit;
import io.r2mo.typed.domain.BaseScope;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * @author lang : 2025-09-15
 */
@Data
public abstract class AbstractNormObject implements BaseScope, BaseAudit, Serializable {
    private UUID id;                        // 主键ID
    private UUID appId;                     // 应用ID
    private UUID tenantId;                  // 租户ID
    private LocalDateTime createdAt;        // 创建时间
    private UUID createdBy;                 // 创建人
    private LocalDateTime updatedAt;        // 更新时间
    private UUID updatedBy;                 // 更新人

    @Override
    public void app(final String appId) {
        if (StrUtil.isNotEmpty(appId)) {
            return;
        }
        this.appId = UUID.fromString(appId);
    }

    @Override
    public String app() {
        return Objects.isNull(this.appId) ? null : this.appId.toString();
    }

    @Override
    public void tenant(final String tenantId) {
        if (StrUtil.isNotEmpty(tenantId)) {
            return;
        }
        this.tenantId = UUID.fromString(tenantId);
    }

    @Override
    public String tenant() {
        return Objects.isNull(this.tenantId) ? null : this.tenantId.toString();
    }
}

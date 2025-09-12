package io.r2mo.typed.domain;

import java.util.Objects;
import java.util.UUID;

/**
 * 特殊场景之下的相关数据处理，执行逻辑上会有所区别
 *
 * @author lang : 2025-09-12
 */
class ContextDefault implements ContextOr {
    private final UUID appId;
    private final UUID tenantId;
    private UUID userId;

    ContextDefault(final UUID appId, final UUID tenantId) {
        this.appId = appId;
        this.tenantId = tenantId;
    }

    ContextDefault idUser(final UUID userId) {
        this.userId = userId;
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T idApp(final boolean isUuid) {
        if (Objects.isNull(this.appId)) {
            return null;
        }
        return isUuid ? (T) this.appId : (T) this.appId.toString();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T idUser(final boolean isUuid) {
        if (Objects.isNull(this.userId)) {
            return null;
        }
        return isUuid ? (T) this.userId : (T) this.userId.toString();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T idTenant(final boolean isUuid) {
        if (Objects.isNull(this.tenantId)) {
            return null;
        }
        return isUuid ? (T) this.tenantId : (T) this.tenantId.toString();
    }
}

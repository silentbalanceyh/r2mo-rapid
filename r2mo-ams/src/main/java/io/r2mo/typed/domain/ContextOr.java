package io.r2mo.typed.domain;

import java.util.UUID;

/**
 * 上下文核心信息，会包含上下文的提取方式，主要是包含
 * <pre>
 *     1. appId
 *     2. tenantId
 *     3. userId
 * </pre>
 * 在网页环境中可直接使用 Request 提取，非网页环境中考虑其他方式提取，注意方法本身就是双格式提取
 *
 * @author lang : 2025-09-12
 */
public interface ContextOr {

    static ContextOr of(final UUID appId, final UUID tenantId) {
        return new ContextDefault(appId, tenantId);
    }

    static ContextOr of(final UUID appId, final UUID tenantId, final UUID userId) {
        return new ContextDefault(appId, tenantId).idUser(userId);
    }

    <T> T idApp(boolean isUuid);

    <T> T idUser(boolean isUuid);

    <T> T idTenant(boolean isUuid);
}

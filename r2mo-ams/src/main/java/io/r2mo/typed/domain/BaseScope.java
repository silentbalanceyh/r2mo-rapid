package io.r2mo.typed.domain;

/**
 * 为 appId / tenantId 提供的统一作用域接口
 *
 * @author lang : 2025-09-04
 */
public interface BaseScope {

    String X_APP_ID = "X-App-Id";

    String X_TENANT_ID = "X-Tenant-Id";

    String F_APP_ID = "appId";
    String F_TENANT_ID = "tenantId";

    void app(String appId);

    String app();

    void tenant(String tenantId);

    String tenant();
}

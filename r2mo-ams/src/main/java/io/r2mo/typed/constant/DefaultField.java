package io.r2mo.typed.constant;

/**
 * @author lang : 2025-09-11
 */
public interface DefaultField {
    // BaseEntity 专用
    String ID = "id";
    String CODE = "code";
    String APP_ID = "appId";
    String TENANT_ID = "tenantId";
    String CREATED_BY = "createdBy";
    String CREATED_AT = "createdAt";
    String UPDATED_BY = "updatedBy";
    String UPDATED_AT = "updatedAt";
    String PARENT_ID = "parentId";
    String LANGUAGE = "language";
    String VERSION = "version";
    String IS_ENABLED = "enabled";

    String STATUS = "status";
    String NAME = "name";
    String TYPE = "type";
    String DESCRIPTION = "description";

    // Security 专用
    String USER_ID = "userId";
    String ROLE_ID = "roleId";
    String GROUP_ID = "groupId";

    // 配置字段
    String C_METADATA = "cMetadata";
    String C_CONFIG = "cConfig";
    String C_SECURITY = "cSecurity";
}

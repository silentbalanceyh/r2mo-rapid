-- ============================================================
-- OAuth2 数据库结构（MySQL 8.x）
-- 包含：
--   1. oauth2_registered_client        - 注册客户端信息
--   2. oauth2_authorization            - 授权 & Token 信息
--   3. oauth2_authorization_consent    - 授权同意记录
-- ============================================================

-- 可选：设置字符集
SET NAMES utf8mb4;

-- =========================
-- 1. 注册客户端表
-- =========================
CREATE TABLE IF NOT EXISTS oauth2_registered_client
(
    -- 主键
    id                            VARCHAR(100)  NOT NULL COMMENT '主键 ID（内部标识）',

    -- 客户端基本信息
    client_id                     VARCHAR(100)  NOT NULL COMMENT '客户端 ID',
    client_id_issued_at           TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '客户端 ID 签发时间',
    client_secret                 VARCHAR(200)  NULL COMMENT '客户端密钥（可为空，例如 public client）',
    client_secret_expires_at      TIMESTAMP     NULL COMMENT '客户端密钥过期时间',
    client_name                   VARCHAR(200)  NOT NULL COMMENT '客户端显示名称',

    -- 认证方式 & 授权模式
    client_authentication_methods VARCHAR(1000) NOT NULL COMMENT '客户端认证方式，逗号分隔',
    authorization_grant_types     VARCHAR(1000) NOT NULL COMMENT '授权类型，逗号分隔',

    -- 回调地址
    redirect_uris                 VARCHAR(2000) NULL COMMENT '授权回调地址列表，逗号分隔',
    post_logout_redirect_uris     VARCHAR(2000) NULL COMMENT '登出回调地址列表，逗号分隔',

    -- Scope & 设置
    scopes                        VARCHAR(1000) NOT NULL COMMENT 'Scope 列表，逗号分隔',
    client_settings               VARCHAR(2000) NOT NULL COMMENT 'Client 级别设置（JSON 字符串）',
    token_settings                VARCHAR(2000) NOT NULL COMMENT 'Token 级别设置（JSON 字符串）',

    -- 预留扩展（可选）
    tenant_id                     VARCHAR(64)   NULL COMMENT '租户 ID（多租户场景可用）',
    ext                           JSON          NULL COMMENT '扩展字段（业务自定义）',

    CONSTRAINT pk_oauth2_registered_client PRIMARY KEY (id),
    CONSTRAINT uk_oauth2_registered_client_client_id UNIQUE (client_id)
)
    ENGINE = InnoDB
    DEFAULT CHARSET = utf8mb4
    COLLATE = utf8mb4_unicode_ci
    COMMENT = 'OAuth2 注册客户端表';

-- 索引：多租户场景常用
CREATE INDEX idx_oauth2_registered_client_tenant
    ON oauth2_registered_client (tenant_id);


-- =========================
-- 2. 授权 / Token 信息表
-- =========================
CREATE TABLE IF NOT EXISTS oauth2_authorization
(
    id                            VARCHAR(100)  NOT NULL COMMENT '主键 ID',

    -- 客户端 & 用户
    registered_client_id          VARCHAR(100)  NOT NULL COMMENT '关联客户端 ID（oauth2_registered_client.id）',
    principal_name                VARCHAR(200)  NOT NULL COMMENT '主体名称（通常为用户名 / userId）',
    authorization_grant_type      VARCHAR(100)  NOT NULL COMMENT '授权类型',
    authorized_scopes             VARCHAR(1000) NULL COMMENT '授权范围（Scope 列表）',
    attributes                    VARCHAR(4000) NULL COMMENT '附加属性（JSON 字符串）',

    -- state
    state                         VARCHAR(500)  NULL COMMENT 'state 参数值',

    -- 授权码
    authorization_code_value      LONGBLOB      NULL COMMENT '授权码值',
    authorization_code_issued_at  TIMESTAMP     NULL COMMENT '授权码签发时间',
    authorization_code_expires_at TIMESTAMP     NULL COMMENT '授权码过期时间',
    authorization_code_metadata   VARCHAR(2000) NULL COMMENT '授权码元数据（JSON 字符串）',

    -- 访问令牌（Access Token）
    access_token_value            LONGBLOB      NULL COMMENT '访问令牌值',
    access_token_issued_at        TIMESTAMP     NULL COMMENT '访问令牌签发时间',
    access_token_expires_at       TIMESTAMP     NULL COMMENT '访问令牌过期时间',
    access_token_metadata         VARCHAR(2000) NULL COMMENT '访问令牌元数据（JSON 字符串）',
    access_token_type             VARCHAR(100)  NULL COMMENT '访问令牌类型（如 Bearer）',
    access_token_scopes           VARCHAR(1000) NULL COMMENT '访问令牌 Scope 列表',

    -- OIDC ID Token
    oidc_id_token_value           LONGBLOB      NULL COMMENT 'ID Token 值',
    oidc_id_token_issued_at       TIMESTAMP     NULL COMMENT 'ID Token 签发时间',
    oidc_id_token_expires_at      TIMESTAMP     NULL COMMENT 'ID Token 过期时间',
    oidc_id_token_metadata        VARCHAR(2000) NULL COMMENT 'ID Token 元数据（JSON 字符串）',
    oidc_id_token_claims          VARCHAR(2000) NULL COMMENT 'ID Token Claims（JSON 字符串）',

    -- 刷新令牌（Refresh Token）
    refresh_token_value           LONGBLOB      NULL COMMENT '刷新令牌值',
    refresh_token_issued_at       TIMESTAMP     NULL COMMENT '刷新令牌签发时间',
    refresh_token_expires_at      TIMESTAMP     NULL COMMENT '刷新令牌过期时间',
    refresh_token_metadata        VARCHAR(2000) NULL COMMENT '刷新令牌元数据（JSON 字符串）',

    -- 用户码 / 设备码（Device Authorization 场景，可选）
    user_code_value               LONGBLOB      NULL COMMENT 'User Code 值',
    user_code_issued_at           TIMESTAMP     NULL COMMENT 'User Code 签发时间',
    user_code_expires_at          TIMESTAMP     NULL COMMENT 'User Code 过期时间',
    user_code_metadata            VARCHAR(2000) NULL COMMENT 'User Code 元数据（JSON 字符串）',

    device_code_value             LONGBLOB      NULL COMMENT 'Device Code 值',
    device_code_issued_at         TIMESTAMP     NULL COMMENT 'Device Code 签发时间',
    device_code_expires_at        TIMESTAMP     NULL COMMENT 'Device Code 过期时间',
    device_code_metadata          VARCHAR(2000) NULL COMMENT 'Device Code 元数据（JSON 字符串）',

    -- 扩展
    tenant_id                     VARCHAR(64)   NULL COMMENT '租户 ID（可选）',
    ext                           JSON          NULL COMMENT '扩展字段（业务自定义）',

    CONSTRAINT pk_oauth2_authorization PRIMARY KEY (id)
)
    ENGINE = InnoDB
    DEFAULT CHARSET = utf8mb4
    COLLATE = utf8mb4_unicode_ci
    COMMENT = 'OAuth2 授权与 Token 信息表';

-- 常用查询索引：按 client + principal
CREATE INDEX idx_oauth2_authorization_client_principal
    ON oauth2_authorization (registered_client_id, principal_name);

-- 按授权类型查询
CREATE INDEX idx_oauth2_authorization_grant_type
    ON oauth2_authorization (authorization_grant_type);

-- 按 principal 查询（统计 / 清理）
CREATE INDEX idx_oauth2_authorization_principal
    ON oauth2_authorization (principal_name);

-- 多租户索引（可选）
CREATE INDEX idx_oauth2_authorization_tenant
    ON oauth2_authorization (tenant_id);


-- =========================
-- 3. 授权同意（Consent）表
-- =========================
CREATE TABLE IF NOT EXISTS oauth2_authorization_consent
(
    registered_client_id VARCHAR(100)  NOT NULL COMMENT '客户端 ID（oauth2_registered_client.id）',
    principal_name       VARCHAR(200)  NOT NULL COMMENT '主体名称（用户名 / userId）',
    authorities          VARCHAR(1000) NOT NULL COMMENT '已同意的权限集合（逗号分隔）',

    tenant_id            VARCHAR(64)   NULL COMMENT '租户 ID（可选）',
    ext                  JSON          NULL COMMENT '扩展字段（业务自定义）',

    CONSTRAINT pk_oauth2_authorization_consent
        PRIMARY KEY (registered_client_id, principal_name)
)
    ENGINE = InnoDB
    DEFAULT CHARSET = utf8mb4
    COLLATE = utf8mb4_unicode_ci
    COMMENT = 'OAuth2 授权同意（Consent）记录表';

CREATE INDEX idx_oauth2_authorization_consent_principal
    ON oauth2_authorization_consent (principal_name);

CREATE INDEX idx_oauth2_authorization_consent_tenant
    ON oauth2_authorization_consent (tenant_id);

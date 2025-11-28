-- ============================================================
-- OAuth2 数据库结构（兼容 MySQL 5.7+ / 8.0+）
-- 使用存储过程方式安全删除索引（避免语法错误）
-- ============================================================

SET NAMES utf8mb4;

-- =========================
-- 1. 注册客户端表
-- =========================

-- 删除表（安全）
DROP TABLE IF EXISTS oauth2_registered_client;

CREATE TABLE oauth2_registered_client
(
    id                            VARCHAR(100)  NOT NULL COMMENT '主键 ID（内部标识）',
    client_id                     VARCHAR(100)  NOT NULL COMMENT '客户端 ID',
    client_id_issued_at           TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '客户端 ID 签发时间',
    client_secret                 VARCHAR(200)  NULL COMMENT '客户端密钥（可为空，例如 public client）',
    client_secret_expires_at      TIMESTAMP     NULL COMMENT '客户端密钥过期时间',
    client_name                   VARCHAR(200)  NOT NULL COMMENT '客户端显示名称',
    client_authentication_methods VARCHAR(1000) NOT NULL COMMENT '客户端认证方式，逗号分隔',
    authorization_grant_types     VARCHAR(1000) NOT NULL COMMENT '授权类型，逗号分隔',
    redirect_uris                 VARCHAR(2000) NULL COMMENT '授权回调地址列表，逗号分隔',
    post_logout_redirect_uris     VARCHAR(2000) NULL COMMENT '登出回调地址列表，逗号分隔',
    scopes                        VARCHAR(1000) NOT NULL COMMENT 'Scope 列表，逗号分隔',
    client_settings               LONGTEXT COMMENT 'Client 级别设置（JSON 字符串）',
    token_settings                LONGTEXT COMMENT 'Token 级别设置（JSON 字符串）',
    tenant_id                     VARCHAR(64)   NULL COMMENT '租户 ID（多租户场景可用）',
    app_id                        VARCHAR(64)   NULL COMMENT '应用 ID（多应用场景可用）',
    ext                           LONGTEXT      NULL COMMENT '扩展字段（业务自定义）',
    CONSTRAINT pk_oauth2_registered_client PRIMARY KEY (id),
    CONSTRAINT uk_oauth2_registered_client_client_id UNIQUE (client_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT = 'OAuth2 注册客户端表';

-- 安全创建索引：先删除（如果存在），再创建
DELIMITER ;;
DROP PROCEDURE IF EXISTS drop_index_if_exists;;
CREATE PROCEDURE drop_index_if_exists()
BEGIN
    IF (SELECT COUNT(*)
        FROM information_schema.statistics
        WHERE table_schema = DATABASE()
          AND table_name = 'oauth2_registered_client'
          AND index_name = 'idx_oauth2_registered_client_tenant') > 0
    THEN
        SET @sql = 'DROP INDEX idx_oauth2_registered_client_tenant ON oauth2_registered_client';
        PREPARE stmt FROM @sql;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
    END IF;
END;;
DELIMITER ;

CALL drop_index_if_exists();
DROP PROCEDURE drop_index_if_exists;

CREATE INDEX idx_oauth2_registered_client_tenant ON oauth2_registered_client (tenant_id);


-- =========================
-- 2. 授权 / Token 信息表
-- =========================

DROP TABLE IF EXISTS oauth2_authorization;

CREATE TABLE oauth2_authorization
(
    id                            VARCHAR(100)  NOT NULL COMMENT '主键 ID',
    registered_client_id          VARCHAR(100)  NOT NULL COMMENT '关联客户端 ID（oauth2_registered_client.id）',
    principal_name                VARCHAR(200)  NOT NULL COMMENT '主体名称（通常为用户名 / userId）',
    authorization_grant_type      VARCHAR(100)  NOT NULL COMMENT '授权类型',
    authorized_scopes             VARCHAR(1000) NULL COMMENT '授权范围（Scope 列表）',
    attributes                    TEXT          NULL COMMENT '附加属性（JSON 字符串）',
    state                         VARCHAR(500)  NULL COMMENT 'state 参数值',
    authorization_code_value      LONGTEXT      NULL COMMENT '授权码值',
    authorization_code_issued_at  TIMESTAMP     NULL COMMENT '授权码签发时间',
    authorization_code_expires_at TIMESTAMP     NULL COMMENT '授权码过期时间',
    authorization_code_metadata   TEXT          NULL COMMENT '授权码元数据（JSON 字符串）',
    access_token_value            LONGTEXT      NULL COMMENT '访问令牌值',
    access_token_issued_at        TIMESTAMP     NULL COMMENT '访问令牌签发时间',
    access_token_expires_at       TIMESTAMP     NULL COMMENT '访问令牌过期时间',
    access_token_metadata         TEXT          NULL COMMENT '访问令牌元数据（JSON 字符串）',
    access_token_type             VARCHAR(100)  NULL COMMENT '访问令牌类型（如 Bearer）',
    access_token_scopes           VARCHAR(1000) NULL COMMENT '访问令牌 Scope 列表',
    oidc_id_token_value           LONGTEXT      NULL COMMENT 'ID Token 值',
    oidc_id_token_issued_at       TIMESTAMP     NULL COMMENT 'ID Token 签发时间',
    oidc_id_token_expires_at      TIMESTAMP     NULL COMMENT 'ID Token 过期时间',
    oidc_id_token_metadata        TEXT          NULL COMMENT 'ID Token 元数据（JSON 字符串）',
    oidc_id_token_claims          TEXT          NULL COMMENT 'ID Token Claims（JSON 字符串）',
    refresh_token_value           LONGTEXT      NULL COMMENT '刷新令牌值',
    refresh_token_issued_at       TIMESTAMP     NULL COMMENT '刷新令牌签发时间',
    refresh_token_expires_at      TIMESTAMP     NULL COMMENT '刷新令牌过期时间',
    refresh_token_metadata        TEXT          NULL COMMENT '刷新令牌元数据（JSON 字符串）',
    user_code_value               LONGTEXT      NULL COMMENT 'User Code 值',
    user_code_issued_at           TIMESTAMP     NULL COMMENT 'User Code 签发时间',
    user_code_expires_at          TIMESTAMP     NULL COMMENT 'User Code 过期时间',
    user_code_metadata            TEXT          NULL COMMENT 'User Code 元数据（JSON 字符串）',
    device_code_value             LONGTEXT      NULL COMMENT 'Device Code 值',
    device_code_issued_at         TIMESTAMP     NULL COMMENT 'Device Code 签发时间',
    device_code_expires_at        TIMESTAMP     NULL COMMENT 'Device Code 过期时间',
    device_code_metadata          TEXT          NULL COMMENT 'Device Code 元数据（JSON 字符串）',
    tenant_id                     VARCHAR(64)   NULL COMMENT '租户 ID（可选）',
    app_id                        VARCHAR(64)   NULL COMMENT '应用 ID（多应用场景可用）',
    ext                           JSON          NULL COMMENT '扩展字段（业务自定义）',
    CONSTRAINT pk_oauth2_authorization PRIMARY KEY (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT = 'OAuth2 授权与 Token 信息表';

-- 辅助过程：删除指定索引（如果存在）
DELIMITER ;;
DROP PROCEDURE IF EXISTS drop_authz_index;;
CREATE PROCEDURE drop_authz_index(IN idx_name VARCHAR(128))
BEGIN
    IF (SELECT COUNT(*)
        FROM information_schema.statistics
        WHERE table_schema = DATABASE()
          AND table_name = 'oauth2_authorization'
          AND index_name = idx_name) > 0
    THEN
        SET @sql = CONCAT('DROP INDEX ', idx_name, ' ON oauth2_authorization');
        PREPARE stmt FROM @sql;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
    END IF;
END;;
DELIMITER ;

CALL drop_authz_index('idx_oauth2_authorization_client_principal');
CALL drop_authz_index('idx_oauth2_authorization_grant_type');
CALL drop_authz_index('idx_oauth2_authorization_principal');
CALL drop_authz_index('idx_oauth2_authorization_tenant');
DROP PROCEDURE drop_authz_index;

CREATE INDEX idx_oauth2_authorization_client_principal ON oauth2_authorization (registered_client_id, principal_name);
CREATE INDEX idx_oauth2_authorization_grant_type ON oauth2_authorization (authorization_grant_type);
CREATE INDEX idx_oauth2_authorization_principal ON oauth2_authorization (principal_name);
CREATE INDEX idx_oauth2_authorization_tenant ON oauth2_authorization (tenant_id);


-- =========================
-- 3. 授权同意（Consent）表
-- =========================

DROP TABLE IF EXISTS oauth2_authorization_consent;

CREATE TABLE oauth2_authorization_consent
(
    registered_client_id VARCHAR(100)  NOT NULL COMMENT '客户端 ID（oauth2_registered_client.id）',
    principal_name       VARCHAR(200)  NOT NULL COMMENT '主体名称（用户名 / userId）',
    authorities          VARCHAR(1000) NOT NULL COMMENT '已同意的权限集合（逗号分隔）',
    tenant_id            VARCHAR(64)   NULL COMMENT '租户 ID（可选）',
    app_id               VARCHAR(64)   NULL COMMENT '应用 ID（多应用场景可用）',
    ext                  JSON          NULL COMMENT '扩展字段（业务自定义）',
    CONSTRAINT pk_oauth2_authorization_consent PRIMARY KEY (registered_client_id, principal_name)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT = 'OAuth2 授权同意（Consent）记录表';

DELIMITER ;;
DROP PROCEDURE IF EXISTS drop_consent_index;;
CREATE PROCEDURE drop_consent_index(IN idx_name VARCHAR(128))
BEGIN
    IF (SELECT COUNT(*)
        FROM information_schema.statistics
        WHERE table_schema = DATABASE()
          AND table_name = 'oauth2_authorization_consent'
          AND index_name = idx_name) > 0
    THEN
        SET @sql = CONCAT('DROP INDEX ', idx_name, ' ON oauth2_authorization_consent');
        PREPARE stmt FROM @sql;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
    END IF;
END;;
DELIMITER ;

CALL drop_consent_index('idx_oauth2_authorization_consent_principal');
CALL drop_consent_index('idx_oauth2_authorization_consent_tenant');
DROP PROCEDURE drop_consent_index;

CREATE INDEX idx_oauth2_authorization_consent_principal ON oauth2_authorization_consent (principal_name);
CREATE INDEX idx_oauth2_authorization_consent_tenant ON oauth2_authorization_consent (tenant_id);
package io.r2mo.spring.security.oauth2.config;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lang : 2025-11-14
 */
@Data
public class ConfigOAuth2Client implements Serializable {

    private String clientId;
    private String clientSecret;
    private String clientName;

    /**
     * 授权模式：
     * authorization_code / refresh_token / client_credentials / password / device_code ...
     */
    private List<String> grantTypes = new ArrayList<>();

    /**
     * 客户端认证方式：
     * client_secret_basic / client_secret_post / none ...
     */
    private List<String> authMethods = new ArrayList<>();

    /**
     * 授权回调地址列表
     */
    private List<String> redirectUris = new ArrayList<>();

    /**
     * 登出回调地址列表（可选）
     */
    private List<String> postLogoutRedirectUris = new ArrayList<>();

    /**
     * Scope 列表
     */
    private List<String> scopes = new ArrayList<>();

    /**
     * 是否需要授权确认页
     */
    private boolean requireConsent;
}

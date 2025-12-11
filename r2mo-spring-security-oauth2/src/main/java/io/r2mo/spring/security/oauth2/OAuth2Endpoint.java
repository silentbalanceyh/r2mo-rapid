package io.r2mo.spring.security.oauth2;

/**
 * OAuth2 授权服务器标准端点常量定义（基于 Spring Authorization Server 默认行为）
 *
 * <p>本类定义的端点均属于 OAuth2 授权服务器（Authorization Server）对外暴露的核心入口，
 * 用于实现 RFC 6749 (OAuth 2.0)、RFC 8414 (AS Metadata)、OpenID Connect 等规范。
 * 这些端点由 Spring Security OAuth2 Authorization Server 自动处理，
 * 业务安全框架（如 r2mo）应跳过对这些路径的重复认证或拦截。</p>
 *
 * <h3>端点使用场景与关联授权类型说明</h3>
 *
 * <pre>
 * ┌────────────────────────────┬───────────────────────────────────────────────────────┬───────────────────────┐
 * │ 端点                       │ 使用场景与支持的 Grant Type                           │ 涉及的 Token 类型     │
 * ├────────────────────────────┼───────────────────────────────────────────────────────┼───────────────────────┤
 * │ AUTHORIZE()                │ 浏览器重定向入口，用户登录并授权                      │ 不直接返回 Token      │
 * │                            │ - authorization_code（含 PKCE）✅                      │ 返回 code（授权码）   │
 * │                            │ - implicit（已废弃，不推荐）❌                        │                       │
 * ├────────────────────────────┼───────────────────────────────────────────────────────┼───────────────────────┤
 * │ TOKEN()                    │ 客户端换取或刷新 Token 的后端接口                     │ access_token          │
 * │                            │ - authorization_code（用 code 换 token）✅            │ refresh_token（可选） │
 * │                            │ - client_credentials（服务间调用）✅                  │ id_token（OIDC）      │
 * │                            │ - refresh_token（刷新访问令牌）✅                     │                       │
 * │                            │ - password（已废弃）❌                                │                       │
 * ├────────────────────────────┼───────────────────────────────────────────────────────┼───────────────────────┤
 * │ JWKS()                     │ 公钥分发端点，供 Resource Server 或客户端校验 JWT     │ 无 Token，返回 JWK 集 │
 * │                            │ 仅用于 RS256 等非对称签名算法                         │                       │
 * ├────────────────────────────┼───────────────────────────────────────────────────────┼───────────────────────┤
 * │ INTROSPECT()               │ Token 内省端点（RFC 7662）                            │ access_token /        │
 * │                            │ 用于 opaque token 或跨域校验 JWT 状态                 │ refresh_token         │
 * │                            │ 客户端需携带 client credentials 调用                  │                       │
 * ├────────────────────────────┼───────────────────────────────────────────────────────┼───────────────────────┤
 * │ REVOKE()                   │ Token 撤销端点（RFC 7009）                            │ access_token /        │
 * │                            │ 主动吊销已颁发的 Token                                │ refresh_token         │
 * │                            │ 客户端需认证                                          │                       │
 * ├────────────────────────────┼───────────────────────────────────────────────────────┼───────────────────────┤
 * │ DEVICE_AUTHORIZATION()     │ 设备码授权流程入口（RFC 8628）                        │ 返回 device_code +    │
 * │                            │ 适用于无浏览器设备（如 TV、IoT）                      │ user_code             │
 * │                            │ 后续通过轮询 /token 获取 access_token                 │                       │
 * └────────────────────────────┴───────────────────────────────────────────────────────┴───────────────────────┘
 * </pre>
 *
 * <h3>Discovery 与元数据端点（无前缀）</h3>
 * <ul>
 *   <li>{@link #OIDC_DISCOVERY}：
 *       OpenID Connect 发现文档，OIDC 客户端通过此端点自动发现授权服务器能力，
 *       包含 issuer、authorization_endpoint、token_endpoint、jwks_uri 等。
 *   </li>
 *   <li>{@link #OAUTH_SERVER_METADATA}：
 *       通用 OAuth 2.0 授权服务器元数据（RFC 8414），非 OIDC 场景使用。
 *   </li>
 * </ul>
 *
 * <h3>注意事项</h3>
 * <ul>
 *   <li>所有带前缀端点（如 /oauth2/authorize）均由 {@code OAuth2AuthorizationServerConfigurer}
 *       自动注册，路径可通过配置覆盖，但默认值统一在此类中维护。</li>
 *   <li>/userinfo 端点通常由 Resource Server 实现，不属于授权服务器核心端点，
 *       因此未包含在此类中。</li>
 *   <li>若未来支持动态前缀（如通过配置），只需修改内部 {@code PREFIX} 或扩展为方法参数。</li>
 * </ul>
 */
public final class OAuth2Endpoint {

    /**
     * OIDC Discovery 端点：{@code /.well-known/openid-configuration}
     * <p>
     * 客户端通过此端点获取授权服务器的 OpenID Connect 元数据，
     * 包括 issuer、authorization_endpoint、token_endpoint、jwks_uri、scopes_supported 等。
     */
    public static final String OIDC_DISCOVERY = "/.well-known/openid-configuration";

    // ============================
    // Discovery & Metadata (无前缀)
    // ============================
    /**
     * OAuth 2.0 授权服务器元数据端点：{@code /.well-known/oauth-authorization-server}
     * <p>
     * 遵循 RFC 8414，提供通用 OAuth2 能力描述，适用于非 OIDC 场景。
     */
    public static final String OAUTH_SERVER_METADATA = "/.well-known/oauth-authorization-server";
    private static final String PREFIX = "/oauth2";

    // ============================
    // Core Endpoints (带前缀)
    // ============================

    private OAuth2Endpoint() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * 授权端点：{@code /oauth2/authorize}
     * <p>
     * 浏览器重定向至此，触发用户登录与授权同意流程。
     * 支持 {@code authorization_code}（含 PKCE），是 OIDC 标准流程入口。
     */
    public static String AUTHORIZE() {
        return PREFIX + "/authorize";
    }

    /**
     * Token 颁发端点：{@code /oauth2/token}
     * <p>
     * 客户端通过此端点提交授权凭证（如 code、client_secret）换取 access_token。
     * 支持多种 grant_type：authorization_code, client_credentials, refresh_token。
     */
    public static String TOKEN() {
        return PREFIX + "/token";
    }

    /**
     * JWK Set 端点：{@code /oauth2/jwks}
     * <p>
     * 返回授权服务器用于签名 JWT 的公钥集合（JSON Web Key Set）。
     * Resource Server 或客户端通过此端点动态获取公钥以验证 ID Token 或 Access Token。
     */
    public static String JWKS() {
        return PREFIX + "/jwks";
    }

    /**
     * Token 内省端点：{@code /oauth2/introspect}
     * <p>
     * 遵循 RFC 7662，用于校验 opaque token 或 JWT 的有效性、scope、exp 等。
     * 调用方需提供 client credentials 进行身份认证。
     */
    public static String INTROSPECT() {
        return PREFIX + "/introspect";
    }

    /**
     * Token 撤销端点：{@code /oauth2/revoke}
     * <p>
     * 遵循 RFC 7009，允许客户端主动吊销已颁发的 access_token 或 refresh_token。
     * 调用时需携带 client credentials。
     */
    public static String REVOKE() {
        return PREFIX + "/revoke";
    }

    /**
     * Device Authorization 端点：{@code /oauth2/device_authorization}
     * <p>
     * 遵循 RFC 8628，用于设备码授权流程。
     * 无输入能力的设备（如智能电视）调用此端点获取 device_code 和 user_code，
     * 用户在另一设备上输入 user_code 完成授权。
     */
    public static String DEVICE_AUTHORIZATION() {
        return PREFIX + "/device_authorization";
    }
}
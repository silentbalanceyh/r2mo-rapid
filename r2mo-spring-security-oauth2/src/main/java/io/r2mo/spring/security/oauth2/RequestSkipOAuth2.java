package io.r2mo.spring.security.oauth2;

import io.r2mo.spring.security.config.ConfigSecurity;
import io.r2mo.spring.security.extension.RequestSkip;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * OAuth2 请求跳过规则
 *
 * 用于定义【在 r2mo 安全框架层面不再重复认证】的 OAuth2 相关端点。
 * 这些端点的安全控制（如 client 鉴权、用户登录流程）交给
 * Spring Authorization Server 自身来处理。
 *
 * 注意：
 * 1. 这里只放行授权服务器“入口类端点”，例如 /oauth2/authorize、/oauth2/token 等；
 * 2. 像 /userinfo 这种需要携带 AccessToken 的端点，不在此处跳过，
 * 应由 Resource Server 的配置负责校验。
 *
 * @author lang : 2025-11-13
 */
public class RequestSkipOAuth2 implements RequestSkip {

    /**
     * OAuth2 端点的统一前缀。
     * 如果后续支持自定义前缀（例如通过配置 spring.security.oauth2.r2mo.endpoint-prefix），
     * 可以将该常量改为从 ConfigSecurity / Environment 中动态读取。
     */
    private static final String PREFIX = "/oauth2";

    @Override
    public Set<String> openApi(final ConfigSecurity security) {
        final Set<String> uris = new LinkedHashSet<>();

        // ============================
        // OIDC / Discovery 相关端点
        // ============================

        // OIDC Discovery 文档：
        // - OIDC 客户端通过此端点获取授权服务器的元信息（issuer、端点地址等）
        uris.add("/.well-known/openid-configuration");

        // OAuth 2.0 Authorization Server Metadata：
        // - 一些工具会访问该端点获取更加通用的元信息
        uris.add("/.well-known/oauth-authorization-server");

        // JWK 公钥端点：
        // - Resource Server 校验 JWT 时，会从此端点加载 JWK 集合
        uris.add(PREFIX + "/jwks");

        // ============================
        // 核心 OAuth2 授权流程端点
        // ============================

        // 授权端点：
        // - 浏览器会被重定向到这里，由 Authorization Server 再触发登录页面
        // - 此处不走业务侧 Authenticator，避免双重拦截
        uris.add(PREFIX + "/authorize");

        // Token 颁发端点：
        // - client_credentials / authorization_code / refresh_token 等都打到这里
        // - 由 Authorization Server 完成 client 鉴权和 token 下发
        uris.add(PREFIX + "/token");

        // Token 内省端点（可选）：
        // - opaque token / 跨服务场景下，可通过此端点校验 token 状态
        uris.add(PREFIX + "/introspect");

        // Token 撤销端点（可选）：
        // - 支持对 AccessToken / RefreshToken 进行主动吊销
        uris.add(PREFIX + "/revoke");

        // Device Authorization 端点（可选）：
        // - 用于设备码授权流程（例如 TV、IoT 终端登录）
        // - 用户真正输入 code 的页面通常是业务自定义 URL，因此不在此处处理
        uris.add(PREFIX + "/device_authorization");

        // ============================
        // 说明：
        // - /userinfo 需要携带 AccessToken，本身应作为受保护资源，
        //   不在此处加入 openApi，而是交给 Resource Server 的配置处理。
        // ============================

        // 返回不可变集合，避免外层误修改
        return Set.copyOf(uris);
    }
}

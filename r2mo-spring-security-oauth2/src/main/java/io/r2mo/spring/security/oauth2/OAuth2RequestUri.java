package io.r2mo.spring.security.oauth2;

import cn.hutool.extra.spring.SpringUtil;
import io.r2mo.spring.security.config.ConfigSecurity;
import io.r2mo.spring.security.config.ConfigSecurityUri;
import io.r2mo.spring.security.extension.RequestUri;
import org.springframework.http.HttpMethod;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * OAuth2 请求跳过规则
 * 用于定义【在 r2mo 安全框架层面不再重复认证】的 OAuth2 相关端点。
 * 这些端点的安全控制（如 client 鉴权、用户登录流程）交给
 * Spring Authorization Server 自身来处理。
 * 注意：
 * 1. 这里只放行授权服务器“入口类端点”，例如 /oauth2/authorize、/oauth2/token 等；
 * 2. 像 /userinfo 这种需要携带 AccessToken 的端点，不在此处跳过，
 * 应由 Resource Server 的配置负责校验。
 *
 * @author lang : 2025-11-13
 */
public class OAuth2RequestUri implements RequestUri {

    private final ConfigSecurityUri securityUriConfig;

    public OAuth2RequestUri() {
        this.securityUriConfig = SpringUtil.getBean(ConfigSecurityUri.class);
    }

    @Override
    public Set<String> ignores(final ConfigSecurity security) {
        final Set<String> uris = new LinkedHashSet<>();
        // 配置白名单，防止页面的 Exceeded maxRedirects
        /*
         * 此处不能追加这个页面，会抛出
         * org.springframework.web.servlet.resource.NoResourceFoundException: No static resource login
         * 此处的配置应该转换到 form 表单中去处理，而不是这样来处理
         */
        // uris.add(this.securityUriConfig.getLogin() + ":" + HttpMethod.GET);
        uris.add(this.securityUriConfig.getError() + ":" + HttpMethod.GET);
        // 返回不可变集合，避免外层误修改
        return Set.copyOf(uris);
    }
}
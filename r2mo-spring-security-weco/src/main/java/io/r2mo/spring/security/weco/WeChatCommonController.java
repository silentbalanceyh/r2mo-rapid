package io.r2mo.spring.security.weco;

import io.r2mo.jaas.session.UserAt;
import io.r2mo.spring.security.auth.AuthService;
import io.r2mo.spring.security.auth.AuthTokenResponse;
import io.r2mo.typed.json.JObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 微信公众号 (WeChat) 认证控制器
 *
 * @author lang : 2025-12-09
 */
@RestController
@Slf4j
public class WeChatCommonController {

    @Autowired
    private WeChatService weChatService;

    @Autowired
    private AuthService authService;

    /**
     * 获取微信公众号授权 URL (前端重定向用)
     * <p>GET /auth/wechat-url</p>
     *
     * @param redirectUri 回调地址
     * @param state       状态码 (默认: state)
     */
    @GetMapping("/auth/wechat-url")
    public JObject getAuthUrl(@RequestParam final String redirectUri, @RequestParam(defaultValue = "state") final String state) {
        // 委托给 Service 处理 (含模块开启校验)
        return this.weChatService.getAuthUrl(redirectUri, state);
    }

    /**
     * 微信公众号 Code 登录
     * <p>POST /auth/wechat-login</p>
     *
     * @param params 请求参数 { "code": "..." }
     */
    @PostMapping("/auth/wechat-login")
    public AuthTokenResponse login(final JObject params) {
        // 1. 构造专用请求 (构造函数内自动校验 code 非空)
        final WeChatLoginRequest request = new WeChatLoginRequest(params);

        // 2. 业务校验 & 获取 OpenID
        // Service 会抛出 _80502 (未启用) 或 _80503 (认证失败)
        final WeChatLoginRequest requestValid = this.weChatService.validate(request);

        // 3. 执行系统登录 (查找绑定关系 -> 生成 Token)
        // 如果找不到绑定用户，AuthService 通常会抛出 "账号未绑定" 类异常
        final UserAt userAt = this.authService.login(requestValid);

        return new AuthTokenResponse(userAt);
    }
}

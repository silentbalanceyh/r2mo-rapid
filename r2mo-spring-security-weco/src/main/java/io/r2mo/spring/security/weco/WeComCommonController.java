package io.r2mo.spring.security.weco;

import io.r2mo.jaas.session.UserAt;
import io.r2mo.spring.security.auth.AuthService;
import io.r2mo.spring.security.auth.AuthTokenResponse;
import io.r2mo.typed.json.JObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 企业微信 (WeCom) 认证控制器
 *
 * @author lang : 2025-12-09
 */
@RestController
@Slf4j
public class WeComCommonController {

    @Autowired
    private WeComService weComService;

    @Autowired
    private AuthService authService;

    /**
     * 获取企业微信扫码登录 URL (前端重定向用)
     * <p>GET /auth/wecom-url</p>
     *
     * @param redirectUri 回调地址
     * @param state       状态码 (默认: state)
     */
    @GetMapping("/auth/wecom-url")
    public JObject getAuthUrl(@RequestParam final String redirectUri, @RequestParam(defaultValue = "state") final String state) {
        // 委托给 Service 处理 (含模块开启校验)
        return this.weComService.getAuthUrl(redirectUri, state);
    }

    /**
     * 企业微信 Code 登录
     * <p>POST /auth/wecom-login</p>
     *
     * @param params 请求参数 { "code": "..." }
     */
    @PostMapping("/auth/wecom-login")
    public AuthTokenResponse login(final JObject params) {
        // 1. 构造专用请求 (构造函数内自动校验 code 非空)
        final WeComLoginRequest request = new WeComLoginRequest(params);

        // 2. 业务校验 & 获取 UserID
        // Service 会抛出 _80552 (未启用) 或 _80553 (认证失败)
        final WeComLoginRequest requestValid = this.weComService.validate(request);

        // 3. 执行系统登录 (查找绑定关系 -> 生成 Token)
        final UserAt userAt = this.authService.login(requestValid);

        return new AuthTokenResponse(userAt);
    }

    /**
     * 获取企业微信扫码登录二维码 (SSO URL)
     * <p>GET /auth/wecom-qrcode</p>
     *
     * @param redirectUri   回调地址 (必需)
     */
    @GetMapping("/auth/wecom-qrcode")
    public JObject getQrCode(@RequestParam final String redirectUri) {
        return this.weComService.getQrCode(redirectUri);
    }

    /**
     * 检查扫码状态
     * <p>POST /auth/wecom-status</p>
     *
     * @param params 请求参数 { "uuid": "..." }
     */
    @PostMapping("/auth/wecom-status")
    public JObject checkStatus(@RequestBody final JObject params) {
        final String uuid = params.getString("uuid");
        return this.weComService.checkStatus(uuid);
    }
}
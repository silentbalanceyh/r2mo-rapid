package io.r2mo.spring.security.weco;

import io.r2mo.spi.SPI;
import io.r2mo.spring.security.auth.AuthService;
import io.r2mo.spring.security.auth.AuthTokenResponse;
import io.r2mo.typed.exception.web._501NotSupportException;
import io.r2mo.typed.json.JObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    // ==========================================
    // 模式一：OAuth2 网页授权 (手机微信内使用)
    // ==========================================

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
        throw new _501NotSupportException("[ R2MO ] /auth/wechat-url 等待开发！");
    }

    /**
     * 微信公众号 Code 登录
     * <p>POST /auth/wechat-login</p>
     *
     * @param params 请求参数 { "code": "..." }
     */
    @PostMapping("/auth/wechat-login")
    public AuthTokenResponse login(final JObject params) {
        throw new _501NotSupportException("[ R2MO ] /auth/wechat-login 等待开发！");
    }

    // ==========================================
    // 模式二：扫码登录 (PC端/非微信环境使用)
    // ==========================================

    /**
     * 获取微信扫码登录二维码
     * <p>GET /auth/wechat-qrcode</p>
     *
     * @param expireSeconds 二维码有效时间（秒），默认300秒
     */
    @GetMapping("/auth/wechat-qrcode")
    public JObject getQrCode(@RequestParam(defaultValue = "300") final Integer expireSeconds) {
        throw new _501NotSupportException("[ R2MO ] /auth/wechat-qrcode 等待开发！");
    }

    /**
     * 检查扫码状态
     * <p>POST /auth/wechat-status</p>
     *
     * @param params 请求参数 { "uuid": "..." }
     */
    @PostMapping("/auth/wechat-status")
    public JObject checkStatus(@RequestBody final JObject params) {
        throw new _501NotSupportException("[ R2MO ] /auth/wechat-status 等待开发！");
    }

    /**
     * 微信服务器配置验证接口 (GET 请求)
     */
    @GetMapping("/auth/wechat-callback")
    public String callback(
        @RequestParam(name = "signature", required = false) final String signature,
        @RequestParam(name = "timestamp", required = false) final String timestamp,
        @RequestParam(name = "nonce", required = false) final String nonce,
        @RequestParam(name = "echostr", required = false) final String echostr) {

        final JObject params = SPI.J();
        params.put("signature", signature);
        params.put("timestamp", timestamp);
        params.put("nonce", nonce);
        final boolean checked = this.weChatService.checkEcho(params);
        if (checked) {
            log.info("[ R2MO ] 签名检查通过：{}", echostr);
            return echostr;
        }
        return "";
    }
}
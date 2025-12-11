package io.r2mo.spring.security.weco;

import io.r2mo.spi.SPI;
import io.r2mo.spring.security.weco.exception._80553Exception401WeComAuthFailure;
import io.r2mo.typed.json.JObject;
import io.r2mo.xync.weco.wecom.WeComIdentify;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
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

    private static final String COOKIE_NAME = "R2MO_WECOM_COOKIE";

    @GetMapping("/auth/wecom-init")
    public JObject init(@RequestParam("targetUrl") final String targetUrl, final HttpServletResponse response) {
        /*
         * 返回结果
         * - state
         * - session
         */
        final WeComIdentify identify = this.weComService.initialize(targetUrl);
        final JObject responseJ = identify.response();
        log.info("[ R2MO ] 状态信息：{}", responseJ.encode());
        return responseJ;
    }

    /**
     * 企业微信 Code 登录
     * <p>POST /auth/wecom-login</p>
     */
    @GetMapping("/auth/wecom-login")
    public void login(@RequestParam("code") final String code,  // 2. 参数直接从 URL 里的 code 取
                      @RequestParam("state") final String state,
                      final HttpServletResponse response // 3. 引入 Response 对象用于重定向
    ) {
        // 1. 构造专用请求 (构造函数内自动校验 code 非空)
        final JObject params = SPI.J()
            .put("code", code).put("state", state);
        log.info("[ R2MO ] 企微登录请求参数：{}", params.encode());
        final WeComLoginRequest request = new WeComLoginRequest(params);

        // 2. 业务校验 & 获取 UserID
        final WeComIdentify validated = this.weComService.validate(request);

        try {

            // 4. 重定向到目标地址
            response.sendRedirect(this.pageOf(validated.url(), validated.token()));
        } catch (final Throwable ex) {
            log.error(ex.getMessage(), ex);
            throw new _80553Exception401WeComAuthFailure();
        }
    }

    private String pageOf(final String url, final String token) {
        return url.contains("?") ? url + "&token=" + token : url + "?token=" + token;
    }

    /**
     * 获取企业微信扫码登录二维码 (SSO URL)
     * <p>GET /auth/wecom-qrcode</p>
     */
    @GetMapping("/auth/wecom-qrcode")
    public JObject getQrCode(@RequestParam("state") final String state) {
        return this.weComService.getQrCode(state);
    }
}
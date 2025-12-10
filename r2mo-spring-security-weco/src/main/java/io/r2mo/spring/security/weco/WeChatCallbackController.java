package io.r2mo.spring.security.weco;

import io.r2mo.spi.SPI;
import io.r2mo.typed.json.JObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lang : 2025-12-10
 */
@RestController
@Slf4j
public class WeChatCallbackController {

    @Autowired
    private WeChatService weChatService;


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

    /**
     * 接收微信回调事件 (扫码、关注等)
     * <p>POST /auth/wechat-callback</p>
     * 注意：
     * 1. 微信发送的是 XML，不能用 JObject 接收
     * 2. 必须返回 "success" 纯文本，不能返回 JSON
     */
    @PostMapping(value = "/auth/wechat-callback", consumes = {"text/xml", "application/xml"}) // 明确声明消费 XML
    public String callback(
        @RequestBody final String requestBody, // 1. 接收原始 XML 字符串
        @RequestParam("signature") final String signature,
        @RequestParam("timestamp") final String timestamp,
        @RequestParam("nonce") final String nonce,
        @RequestParam(name = "openid", required = false) final String openid,
        @RequestParam(name = "encrypt_type", required = false) final String encType,
        @RequestParam(name = "msg_signature", required = false) final String msgSignature) {

        log.info("[ R2MO ] 接收到微信回调 XML: {}", requestBody);

        // 2. 构造参数传给 Service 处理
        // Service 内部需调用 WxMpService.route(xml) 或手动解析 XML 处理扫码逻辑
        // 这里假设你的 weChatService.handleCallback 负责解析 XML 并更新 Redis 状态
        // this.weChatService.handleCallback(requestBody, signature, timestamp, nonce, openid, encType, msgSignature);

        // 3. 必须返回 "success" 告诉微信服务器处理成功，否则微信会重试
        return "success";
    }
}

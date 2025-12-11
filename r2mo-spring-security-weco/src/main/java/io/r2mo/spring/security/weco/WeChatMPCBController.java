package io.r2mo.spring.security.weco;

import io.r2mo.spring.security.auth.AuthService;
import io.r2mo.typed.json.JObject;
import io.r2mo.xync.weco.wechat.WeArgsCallback;
import io.r2mo.xync.weco.wechat.WeArgsSignature;
import io.r2mo.xync.weco.wechat.WeChatType;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

/**
 * 公众号回调模式
 *
 * @author lang : 2025-12-10
 */
@RestController
@Slf4j
public class WeChatMPCBController {

    @Autowired
    private WeChatService weChatService;
    @Autowired
    private AuthService authService;


    @GetMapping("/auth/wechat-callback")
    public String callback(
        @RequestParam(name = "signature", required = false) final String signature,
        @RequestParam(name = "timestamp", required = false) final String timestamp,
        @RequestParam(name = "nonce", required = false) final String nonce,
        @RequestParam(name = "echostr", required = false) final String echostr) {

        // 参数准备
        final WeArgsSignature params = WeArgsSignature.builder()
            .signature(signature)
            .timestamp(timestamp)
            .nonce(nonce)
            .build();

        // 签名检查
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
        @RequestParam(name = "encrypt_type", required = false) final String encType,
        @RequestParam(name = "msg_signature", required = false) final String msgSignature) {
        // 1. 解析 XML
        final WxMpXmlMessage message = WxMpXmlMessage.fromXml(requestBody);
        final String event = message.getEvent(); // SUBSCRIBE 或 SCAN
        final String eventKey = message.getEventKey(); // 可能带 qrscene_ 前缀
        final String openid = message.getFromUser();

        // 2. 提取 UUID
        String uuid = null;
        if ("subscribe".equalsIgnoreCase(event)) {
            // 关注事件：Key 是 "qrscene_UUID"
            if (eventKey != null && eventKey.startsWith("qrscene_")) {
                uuid = eventKey.replace("qrscene_", "");
            }
        } else if ("SCAN".equalsIgnoreCase(event)) {
            // 已关注扫码：Key 就是 "UUID"
            uuid = eventKey;
        }

        // 3. 只有提取了 UUID 才能把人和浏览器对上号
        if (Objects.nonNull(uuid)) {
            // 参数准备
            final WeArgsCallback params = WeArgsCallback.builder()
                .signature(signature)
                .timestamp(timestamp)
                .nonce(nonce)
                .openid(openid)
                .msgSignature(msgSignature)
                .encType(encType)
                .type(WeChatType.MP)
                .build();

            // 构造登录专用请求
            final JObject response = this.weChatService.extract(uuid, params);
            final String id = response.getString("id");
            final String token = response.getString("token");

            log.info("[ R2MO ] 用户关注/扫描成功，Token 已就绪，ID = {}, Token = {}", id, token);
        }
        return "success";
    }
}

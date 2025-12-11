package io.r2mo.xync.weco.wechat;

import io.r2mo.base.exchange.UniMessage;
import io.r2mo.base.exchange.UniResponse;
import io.r2mo.typed.json.JObject;
import io.r2mo.xync.weco.WeCoAction;
import io.r2mo.xync.weco.WeCoUtil;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpQrCodeTicket;

import java.util.UUID;

/**
 * 动作：获取带参二维码 (APP_AUTH_QR)
 * <p>依赖 WxMpService 进行 API 调用，依赖 WeCoSession SPI 存储初始状态。</p>
 *
 * @author lang : 2025-12-10
 */
class WeChatActionQrCode extends WeChatAction implements WeCoAction<Void> {
    /**
     * 构造函数：仅注入 WxMpService（WeCoSession 通过 SPI 自动获取）
     */
    WeChatActionQrCode(final WxMpService service) {
        super(service);
    }

    /**
     * 执行获取带参二维码的命令，并初始化会话状态到缓存。
     * <pre>
     * 输入格式 ({@link UniMessage})
     * -- Header:
     * ---- expireSeconds: String/Integer. 二维码的有效时长（秒）。**必需** (1s ~ 2592000s)。
     * -- Payload:
     * ---- null (Void)
     * 输出格式 ({@link UniResponse})
     * -- Payload:
     * ---- {@link JObject}. 包含以下字段:
     * ------ uuid: String. 扫码会话的唯一标识，用于轮询。
     * ------ qrUrl: String. 二维码图片的 URL 地址。
     * ------ expireSeconds: Integer. 实际使用的过期时间（秒）。
     * ------ actionType: String. {@code APP_AUTH_QR}。
     * </pre>
     *
     * @param request 封装了过期时长的 UniMessage 请求。
     *
     * @return 包含二维码链接和 UUID 的 UniResponse。
     * @throws Exception 微信API调用失败或参数缺失。
     */
    @Override
    public UniResponse execute(final UniMessage<Void> request) throws Exception {
        // 读取 expireSeconds
        final int expireSeconds = WeCoUtil.inputExpired(request);

        final String uuid = UUID.randomUUID().toString().replace("-", "");

        // 1. 调用 WxJava 获取临时二维码 Ticket
        final WxMpQrCodeTicket ticket = this.service().getQrcodeService().qrCodeCreateTmpTicket(uuid, expireSeconds);
        final String qrUrl = this.service().getQrcodeService().qrCodePictureUrl(ticket.getTicket());

        final JObject response = WeCoUtil.replyQr(uuid, qrUrl, expireSeconds);
        return UniResponse.success(response);
    }
}
package io.r2mo.xync.weco.wecom;

import cn.hutool.core.util.StrUtil;
import io.r2mo.base.exchange.UniMessage;
import io.r2mo.base.exchange.UniResponse;
import io.r2mo.spi.SPI;
import io.r2mo.typed.exception.web._400BadRequestException;
import io.r2mo.typed.json.JObject;
import io.r2mo.xync.weco.WeCoAction;
import io.r2mo.xync.weco.WeCoActionType;
import io.r2mo.xync.weco.WeCoConstant;
import io.r2mo.xync.weco.WeCoSession;
import io.r2mo.xync.weco.WeCoStatus;
import me.chanjar.weixin.cp.api.WxCpService;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 动作：获取带参二维码 (APP_AUTH_QR)
 * <p>依赖 WxCpService 进行 API 调用，依赖 WeCoSession SPI 存储初始状态。</p>
 *
 * @author lang : 2025-12-10
 */
public class WeComActionQrCode extends WeComAction implements WeCoAction<Void> {

    // 【关键变更】通过 SPI 机制查找 WeCoSession 的单实例实现
    private final WeCoSession weCoSession = SPI.findOneOf(WeCoSession.class);

    /**
     * 构造函数：仅注入 WxCpService（WeCoSession 通过 SPI 自动获取）
     */
    public WeComActionQrCode(final WxCpService service) {
        super(service);
    }

    /**
     * 执行获取带参二维码的命令，并初始化会话状态到缓存。
     * <pre>
     * 输入格式 ({@link UniMessage})
     * -- Header:
     * ---- redirectUri: String. 授权回调地址。**必需**。
     * ---- expireSeconds: String/Integer. 二维码/会话的有效时长（秒）。**必需** (1s ~ 2592000s)。
     * -- Payload:
     * ---- null (Void)
     * 输出格式 ({@link UniResponse})
     * -- Payload:
     * ---- {@link JObject}. 包含以下字段:
     * ------ uuid: String. 扫码会话的唯一标识，用于轮询。
     * ------ qrUrl: String. 企微扫码登录的 URL 地址 (前端可嵌入 iframe 或跳转)。
     * ------ expireSeconds: Integer. 实际使用的过期时间（秒）。
     * ------ actionType: String. {@code APP_AUTH_QR}。
     * </pre>
     *
     * @param request 封装了过期时长的 UniMessage 请求。
     *
     * @return 包含二维码链接和 UUID 的 UniResponse。
     * @throws Exception 企业微信API调用失败或参数缺失。
     */
    @Override
    public UniResponse execute(final UniMessage<Void> request) throws Exception {
        final String expireSecondsStr = request.header("expireSeconds");
        final String redirectUri = request.header(WeCoConstant.HEADER_REDIRECT_URI);

        if (StrUtil.isBlank(expireSecondsStr)) {
            throw new _400BadRequestException("[R2MO] Header 缺少 'expireSeconds' 参数，该参数为必填项。");
        }
        if (StrUtil.isBlank(redirectUri)) {
            throw new _400BadRequestException("[R2MO] Header 缺少 'redirectUri' 参数，该参数为必填项。");
        }

        final int expireSeconds;
        try {
            expireSeconds = Integer.parseInt(expireSecondsStr);
            if (expireSeconds <= 0 || expireSeconds > WeCoSession.MAX_EXPIRE_SECONDS) {
                throw new _400BadRequestException(
                    "expireSeconds 必须大于0且小于等于 " + WeCoSession.MAX_EXPIRE_SECONDS + " (30天)。"
                );
            }
        } catch (final NumberFormatException e) {
            throw new _400BadRequestException("expireSeconds 必须是一个有效的整数值。");
        }

        final String uuid = UUID.randomUUID().toString().replace("-", "");

        // 1. 构造企业微信扫码登录 URL (SSO)
        // 注意：企微没有类似公众号的“带参二维码Ticket”接口， standard practice is using the SSO QR Connect URL.
        // 前端可以使用这个 URL 在 iframe 中展示二维码，或者直接跳转。
        final String qrUrl = this.service().buildQrConnectUrl(redirectUri, uuid);

        // 2. 存储初始状态到 SPI
        final String sessionKey = WeCoSession.keyOf(uuid);
        // 缓存时间比二维码有效期稍长 (多加60秒的缓冲)
        final Duration storeDuration = Duration.ofSeconds(expireSeconds).plusSeconds(60);

        // 调用通过 SPI 机制获取的 WeCoSession 实例
        this.weCoSession.save(
            sessionKey,
            WeCoStatus.WAITING.name(),
            storeDuration
        );

        // 3. 封装结果返回给上层 Service
        final Map<String, Object> result = new HashMap<>();
        result.put(WeCoConstant.PARAM_UUID, uuid);
        result.put("qrUrl", qrUrl);
        result.put("expireSeconds", expireSeconds);
        result.put("actionType", WeCoActionType.APP_AUTH_QR.name());
        final JObject response = SPI.J();
        response.put(result);
        return UniResponse.success(response);
    }
}
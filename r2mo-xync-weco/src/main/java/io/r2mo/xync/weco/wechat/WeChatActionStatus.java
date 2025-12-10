package io.r2mo.xync.weco.wechat;

import io.r2mo.base.exchange.UniMessage;
import io.r2mo.base.exchange.UniResponse;
import io.r2mo.spi.SPI;
import io.r2mo.typed.exception.web._400BadRequestException;
import io.r2mo.typed.json.JObject;
import io.r2mo.xync.weco.WeCoAction;
import io.r2mo.xync.weco.WeCoSession;
import io.r2mo.xync.weco.WeCoStatus;
import me.chanjar.weixin.mp.api.WxMpService;

/**
 * 动作：检查扫码状态 (APP_STATUS)
 * <p>依赖 WeCoSession SPI 查询状态。</p>
 *
 * @author lang : 2025-12-10
 */
class WeChatActionStatus extends WeChatAction implements WeCoAction<String> {

    // 通过 SPI 机制查找 WeCoSession 的单实例实现
    private final WeCoSession weCoSession = SPI.findOneOf(WeCoSession.class);

    /**
     * 构造函数：仅注入 WxMpService（Service 在此动作中非必须，但遵循架构保留）
     */
    WeChatActionStatus(final WxMpService service) {
        super(service);
    }

    /**
     * 执行检查扫码登录会话状态的命令。
     * <pre>
     * 输入格式 ({@link UniMessage})
     * -- Header:
     * ---- (无特殊必需 Header)
     * -- Payload:
     * ---- String. 扫码会话的 UUID。**必需**。
     * 输出格式 ({@link UniResponse})
     * -- Payload:
     * ---- {@link JObject}. 包含以下字段:
     * ------ status: String. 当前状态 (WAITING/SUCCESS)。
     * ------ isSuccess: Boolean. 是否已登录成功。
     * ------ openId: String. 仅在 status 为 SUCCESS 时返回用户的 OpenID。
     * </pre>
     *
     * @param request 封装了会话 UUID 的 UniMessage 请求。
     *
     * @return 包含当前状态和 OpenID (如果成功) 的 UniResponse。
     * @throws Exception 参数缺失。
     */
    @Override
    public UniResponse execute(final UniMessage<String> request) {
        // Payload 约定为 UUID 字符串
        final String uuid = request.payload();

        if (uuid == null) {
            throw new _400BadRequestException("[R2MO] 缺少 Payload 参数: UUID");
        }

        // 1. 构建缓存 Key 并查询 SPI 存储
        final String sessionKey = WeCoSession.keyOf(uuid);
        final String status = this.weCoSession.get(sessionKey);

        final JObject result = SPI.J();

        // 2. 判断状态
        if (status == null || WeCoStatus.WAITING.name().equals(status) || WeCoStatus.EXPIRED.name().equals(status)) {
            // 状态：等待中、过期、或缓存不存在
            result.put("status", WeCoStatus.WAITING.name());
            result.put("isSuccess", false);
        } else {
            // 状态：成功 (缓存中存储的就是 OpenID)
            result.put("status", WeCoStatus.SUCCESS.name());
            result.put("isSuccess", true);
            // 缓存中的值就是 OpenID，返回给上层 Service
            result.put("openId", status);
        }

        return UniResponse.success(result);
    }
}
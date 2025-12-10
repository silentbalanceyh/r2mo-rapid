package io.r2mo.xync.weco.wecom;

import io.r2mo.base.exchange.UniMessage;
import io.r2mo.base.exchange.UniResponse;
import io.r2mo.typed.exception.web._400BadRequestException;
import io.r2mo.typed.json.JObject;
import io.r2mo.xync.weco.WeCoAction;
import io.r2mo.xync.weco.WeCoUtil;
import me.chanjar.weixin.cp.api.WxCpService;

/**
 * 动作：检查扫码状态 (APP_STATUS)
 * <p>依赖 WeCoSession SPI 查询状态。</p>
 *
 * @author lang : 2025-12-10
 */
class WeComActionStatus extends WeComAction implements WeCoAction<String> {

    /**
     * 构造函数：仅注入 WxCpService（Service 在此动作中非必须，但遵循架构保留）
     */
    WeComActionStatus(final WxCpService service) {
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
     */
    @Override
    public UniResponse execute(final UniMessage<String> request) {
        // Payload 约定为 UUID 字符串
        final String uuid = request.payload();

        if (uuid == null) {
            throw new _400BadRequestException("[ R2MO ] 缺少 Payload 参数: UUID");
        }

        final JObject result = WeCoUtil.replyStatus(uuid);

        return UniResponse.success(result);
    }
}
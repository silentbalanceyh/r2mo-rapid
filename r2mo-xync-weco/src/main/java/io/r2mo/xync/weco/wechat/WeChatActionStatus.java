package io.r2mo.xync.weco.wechat;

import io.r2mo.base.exchange.UniMessage;
import io.r2mo.base.exchange.UniResponse;
import io.r2mo.spi.SPI;
import io.r2mo.typed.json.JObject;
import io.r2mo.typed.webflow.Akka;
import io.r2mo.xync.weco.WeCoAction;
import io.r2mo.xync.weco.WeCoUtil;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.mp.api.WxMpService;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 动作：检查扫码状态 (APP_STATUS)
 * <p>依赖 WeCoSession SPI 查询状态。</p>
 *
 * @author lang : 2025-12-10
 */
@Slf4j
class WeChatActionStatus extends WeChatAction implements WeCoAction<String> {

    private static final AtomicBoolean IS_LOG = new AtomicBoolean(Boolean.TRUE);

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
     * @return 包含当前状态和 OpenID (如果成功) 的 UniResponse。
     */
    @Override
    public UniResponse execute(final UniMessage<String> request) {

        final JObject result = WeCoUtil.replyStatus(request);

        return UniResponse.success(result);
    }

    @Override
    public Akka<UniResponse> executeAsync(final UniMessage<String> request) {
        final WeCoAction<String> actionOr = this.findReplaced();
        if (Objects.isNull(actionOr)) {
            return WeCoAction.super.executeAsync(request);
        }
        if (IS_LOG.getAndSet(Boolean.FALSE)) {
            log.info("[ R2MO ] WeCoAction 行为替换 ( Async + Sync 组合 ）：{}", actionOr.getClass());
        }
        return actionOr.executeAsync(request);
    }

    @SuppressWarnings("unchecked")
    private WeCoAction<String> findReplaced() {
        final String cachedKey = WeCoAction.ACTION_CHAT_STATUS + "@" + this.getClass().getName();
        return (WeCoAction<String>) WeCoAction.CC_ACTION.pick(() -> SPI.findOne(WeCoAction.class, WeCoAction.ACTION_CHAT_STATUS), cachedKey);
    }
}
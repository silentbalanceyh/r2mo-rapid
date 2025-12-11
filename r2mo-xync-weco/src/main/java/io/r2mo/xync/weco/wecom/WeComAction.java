package io.r2mo.xync.weco.wecom;

import io.r2mo.typed.exception.web._404NotFoundException;
import io.r2mo.xync.weco.WeCoAction;
import io.r2mo.xync.weco.WeCoActionType;
import me.chanjar.weixin.cp.api.WxCpService;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

/**
 * 企业微信 (WeCom) 动作工厂和基类
 *
 * @author lang : 2025-12-10
 */
@SuppressWarnings("all")
class WeComAction {

    // Action 构造函数映射表
    private static final ConcurrentMap<WeCoActionType, Function<WxCpService, WeCoAction>> SUPPLIER = new ConcurrentHashMap<>() {
        {
            // 企微认证 Action 列表 (需要提前实现 WeComActionAuthUrl, WeComActionLogin 等)
            this.put(WeCoActionType.WX_AUTH_URL, WeComActionAuthUrl::new);
            this.put(WeCoActionType.WX_LOGIN_BY, WeComActionLogin::new);
            this.put(WeCoActionType.APP_AUTH_QR, WeComActionQrCode::new);
            this.put(WeCoActionType.APP_STATUS, WeComActionStatus::new);
        }
    };
    private final WxCpService service;

    // --- 基类职责 ---

    protected WeComAction(final WxCpService service) {
        this.service = service;
    }

    /**
     * 工厂方法：获取或创建指定的 WeCoAction 实例
     */
    static <T> WeCoAction<T> of(final WeCoActionType actionType, final WxCpService service) {
        final Function<WxCpService, WeCoAction> constructorFn = SUPPLIER.get(actionType);
        if (Objects.isNull(constructorFn)) {
            throw new _404NotFoundException("[ R2MO ] 未知的 WeCoActionType:" + actionType);
        }

        // 缓存 Key 策略：Action 基类名 + ActionType
        final String cacheKey = WeComAction.class.getName() + "@" + actionType.name();

        // 使用 WeCoAction.CC_ACTION 缓存 Action 实例
        return (WeCoAction<T>) WeCoAction.CC_ACTION.pick(() -> constructorFn.apply(service), cacheKey);
    }

    /**
     * 获取 WxCpService 实例，供子类调用
     */
    protected WxCpService service() {
        return this.service;
    }

}
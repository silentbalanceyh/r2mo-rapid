package io.r2mo.xync.weco.wechat;

import io.r2mo.typed.exception.web._404NotFoundException;
import io.r2mo.xync.weco.WeCoAction;
import io.r2mo.xync.weco.WeCoActionType;
import me.chanjar.weixin.mp.api.WxMpService;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

/**
 * 两种职责（抽象工厂模式）
 * <pre>
 *     1. 继承用的基类
 *     2. 作为工厂
 * </pre>
 *
 * @author lang : 2025-12-10
 */
@SuppressWarnings("all")
class WeChatAction {

    private static final ConcurrentMap<WeCoActionType, Function<WxMpService, WeCoAction>> SUPPLIER = new ConcurrentHashMap<>() {
        {
            this.put(WeCoActionType.WX_AUTH_URL, WeChatActionAuthUrl::new);
            this.put(WeCoActionType.WX_LOGIN_BY, WeChatActionLogin::new);
            this.put(WeCoActionType.APP_AUTH_QR, WeChatActionQrCode::new);
            this.put(WeCoActionType.APP_STATUS, WeChatActionStatus::new);
            this.put(WeCoActionType.APP_PRE, WeChatActionPre::new);
        }
    };
    private final WxMpService service;

    protected WeChatAction(final WxMpService service) {
        this.service = service;
    }

    static <T> WeCoAction<T> of(final WeCoActionType actionType, final WxMpService service) {
        final Function<WxMpService, WeCoAction> constructorFn = SUPPLIER.get(actionType);
        if (Objects.isNull(constructorFn)) {
            throw new _404NotFoundException("[ R2MO ] 未知的 WeCoActionType:" + actionType);
        }
        final String cacheKey = WeChatAction.class.getName() + "@" + actionType.name();
        return (WeCoAction<T>) WeCoAction.CC_ACTION.pick(() -> constructorFn.apply(service), cacheKey);
    }

    protected WxMpService service() {
        return this.service;
    }

}

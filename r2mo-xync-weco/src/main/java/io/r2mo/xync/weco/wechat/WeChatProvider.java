package io.r2mo.xync.weco.wechat;

import io.r2mo.base.exchange.UniAccount;
import io.r2mo.base.exchange.UniContext;
import io.r2mo.base.exchange.UniCredential;
import io.r2mo.base.exchange.UniMessage;
import io.r2mo.base.exchange.UniProvider;
import io.r2mo.base.exchange.UniResponse;
import io.r2mo.typed.annotation.SPID;
import io.r2mo.typed.cc.Cc;
import io.r2mo.typed.exception.web._500ServerInternalException;
import io.r2mo.xync.weco.WeCoAction;
import io.r2mo.xync.weco.WeCoActionType;
import io.r2mo.xync.weco.WeCoUtil;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import me.chanjar.weixin.mp.config.impl.WxMpDefaultConfigImpl;

/**
 * 微信 (WeChat) Provider
 * <p>
 * 核心职责：处理微信生态的交互 (扫码登录、模板消息等)
 * </p>
 *
 * @author lang : 2025-12-09
 */
@SPID("UNI_WECHAT")
@Slf4j
public class WeChatProvider implements UniProvider {
    // 缓存 Key: ContextHash + CredentialHash
    private static final Cc<String, WxMpService> CC_CLIENT = Cc.open();

    @Override
    public String channel() {
        return "WECHAT";
    }

    @Override
    public Class<? extends UniCredential> credentialType() {
        return WeChatCredential.class;
    }

    @Override
    @SuppressWarnings("all")
    public UniResponse exchange(final UniAccount account, final UniMessage<?> request, final UniContext context) {
        // 1. 凭证校验 (保持不变)
        if (!(account.credential() instanceof final WeChatCredential cred)) {
            throw new IllegalArgumentException("[ R2MO ] 凭证类型不匹配，需要 WeChatCredential");
        }
        if (!(context instanceof final WeChatContext ctx)) {
            throw new IllegalArgumentException("[ R2MO ] 上下文类型不匹配，需要 WeChatContext");
        }

        // 2. 获取客户端 (保持不变)
        final WxMpService service = this.getService(cred, ctx);

        // 3. 执行分发：使用 Action 工厂
        final String actionName = request.header("action");

        // 尝试将字符串转换为 ActionType 枚举
        final WeCoActionType actionType;
        try {
            // 假设 actionName 匹配 WeCoActionType 中的枚举名 (例如 "WX_AUTH_URL")
            actionType = WeCoActionType.valueOf(actionName);
        } catch (final IllegalArgumentException e) {
            throw new IllegalArgumentException("[ R2MO ] 未知的 Action: " + actionName);
        }

        try {
            // 通过工厂创建对应的 WeCoAction 实例 (Action 实例可能从缓存中获取)
            final WeCoAction actionImpl = WeChatAction.of(actionType, service);

            // 执行 Action，并返回结果
            return actionImpl.execute(request);

        } catch (final Throwable ex) {
            log.error("[ R2MO ] WeChat 操作失败，Action: {}，错误: {}", actionType.name(), ex.getMessage(), ex);
            throw new _500ServerInternalException(ex.getMessage());
        }
    }

    // --- 原有内部逻辑被移除 ---
    // private UniResponse handleGetUrl(...) -> 移除
    // private UniResponse handleLoginByCode(...) -> 移除

    // --- 客户端工厂 (保持不变) ---

    private WxMpService getService(final WeChatCredential cred, final WeChatContext ctx) {
        final String cacheKey = ctx.hashCode() + "@" + cred.hashCode();

        return CC_CLIENT.pick(() -> {
            log.info("[ R2MO ] 初始化新的 WeChat 客户端：{}", cacheKey);

            final WxMpDefaultConfigImpl config = new WxMpDefaultConfigImpl();
            config.setAppId(cred.appId());
            config.setSecret(cred.secret());
            config.setToken(ctx.getToken());

            // 使用 Helper 统一处理代理
            WeCoUtil.applyProxy(config, ctx.getProxy());

            // 注入重试
            config.setMaxRetryTimes(ctx.getMaxRetry());

            final WxMpService service = new WxMpServiceImpl();
            service.setWxMpConfigStorage(config);
            return service;
        }, cacheKey);
    }
}
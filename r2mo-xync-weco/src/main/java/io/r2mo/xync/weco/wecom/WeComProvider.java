package io.r2mo.xync.weco.wecom;

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
import io.r2mo.xync.weco.WeCoConstant;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.cp.api.WxCpService;
import me.chanjar.weixin.cp.api.impl.WxCpServiceImpl;
import me.chanjar.weixin.cp.config.impl.WxCpDefaultConfigImpl;

/**
 * 企业微信 (WeCom) Provider
 * <p>
 * 核心职责：处理企业微信生态的交互 (扫码登录、内部通知等)
 * </p>
 *
 * @author lang : 2025-12-09
 */
@SPID("UNI_WECOM")
@Slf4j
public class WeComProvider implements UniProvider {
    // 缓存 Key: ContextHash + CredentialHash
    private static final Cc<String, WxCpService> CC_CLIENT = Cc.open();

    @Override
    public String channel() {
        return "WECOM";
    }

    @Override
    public Class<? extends UniCredential> credentialType() {
        return WeComCredential.class;
    }

    @Override
    @SuppressWarnings("all")
    public UniResponse exchange(final UniAccount account, final UniMessage<?> request, final UniContext context) {
        // 1. 凭证校验 (保持不变)
        if (!(account.credential() instanceof final WeComCredential cred)) {
            throw new IllegalArgumentException("[ R2MO ] 凭证类型不匹配，需要 WeComCredential");
        }
        if (!(context instanceof final WeComContext ctx)) {
            throw new IllegalArgumentException("[ R2MO ] 上下文类型不匹配，需要 WeComContext");
        }

        // 2. 获取客户端 (保持不变)
        final WxCpService service = this.getService(cred, ctx);

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
            final WeCoAction actionImpl = WeComAction.of(actionType, service);

            // 执行 Action，并返回结果
            return actionImpl.execute(request);

        } catch (final Throwable ex) {
            log.error("[ R2MO ] WeCom 操作失败，Action: {}，错误: {}", actionType.name(), ex.getMessage(), ex);
            throw new _500ServerInternalException(ex.getMessage());
        }
    }

    // --- 原有内部逻辑被移除 ---
    // private UniResponse handleGetUrl(...) -> 移除
    // private UniResponse handleLoginByCode(...) -> 移除

    // --- 客户端工厂 (保持不变) ---

    private WxCpService getService(final WeComCredential cred, final WeComContext ctx) {
        final String cacheKey = ctx.hashCode() + "@" + cred.hashCode();

        return CC_CLIENT.pick(() -> {
            log.info("[ R2MO ] 初始化新的 WeCom 客户端：{}", cacheKey);

            final WxCpDefaultConfigImpl config = new WxCpDefaultConfigImpl();
            config.setCorpId(cred.corpId());
            config.setCorpSecret(cred.secret());
            config.setAgentId(cred.agentId()); // 企微扫码强制要求 AgentId

            // 使用 Helper 统一处理代理
            WeCoConstant.Helper.applyProxy(config, ctx.getProxy());

            // 注入 Host (企微私有化部署支持)
            // 如果 Context 指定了 Host，则修改 BaseApiUrl
            //            if (StrUtil.isNotBlank(ctx.getHost())) {
            //                final String protocol = ctx.getOrDefault(UniContext.KEY_PROTOCOL, "https");
            //                // 企微 SDK 要求完整的 URL 前缀 (包含 protocol 和 结尾斜杠)
            //                config.setBaseApiUrl(protocol + "://" + ctx.getHost() + "/");
            //            }

            final WxCpService service = new WxCpServiceImpl();
            service.setWxCpConfigStorage(config);
            return service;
        }, cacheKey);
    }
}
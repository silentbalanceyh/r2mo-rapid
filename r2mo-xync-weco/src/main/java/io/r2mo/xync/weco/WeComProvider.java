package io.r2mo.xync.weco;

import cn.hutool.core.util.StrUtil;
import io.r2mo.base.exchange.UniAccount;
import io.r2mo.base.exchange.UniContext;
import io.r2mo.base.exchange.UniCredential;
import io.r2mo.base.exchange.UniMessage;
import io.r2mo.base.exchange.UniProvider;
import io.r2mo.base.exchange.UniResponse;
import io.r2mo.typed.annotation.SPID;
import io.r2mo.typed.cc.Cc;
import io.r2mo.typed.exception.web._500ServerInternalException;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.cp.api.WxCpService;
import me.chanjar.weixin.cp.api.impl.WxCpServiceImpl;
import me.chanjar.weixin.cp.bean.WxCpOauth2UserInfo;
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
    public UniResponse exchange(final UniAccount account, final UniMessage<?> request, final UniContext context) {
        // 1. 凭证校验
        if (!(account.credential() instanceof final WeComCredential cred)) {
            throw new IllegalArgumentException("[ R2MO ] 凭证类型不匹配，需要 WeComCredential");
        }
        // Context 复用 WeComContext
        if (!(context instanceof final WeComContext ctx)) {
            throw new IllegalArgumentException("[ R2MO ] 上下文类型不匹配，需要 WeComContext");
        }

        // 2. 获取客户端
        final WxCpService service = this.getService(cred, ctx);

        // 3. 执行分发
        final String action = request.header("action");
        try {
            if (WeCoAction.GET_AUTH_URL.equals(action)) {
                return this.handleGetUrl(service, request);
            }
            if (WeCoAction.LOGIN_BY_CODE.equals(action)) {
                return this.handleLoginByCode(service, request);
            }
            throw new IllegalArgumentException("[ R2MO ] 未知的 Action: " + action);
        } catch (final Throwable ex) {
            log.error("[ R2MO ] WeCom 操作失败：{}", ex.getMessage());
            throw new _500ServerInternalException(ex.getMessage());
        }
    }

    // --- 内部逻辑 ---

    private UniResponse handleGetUrl(final WxCpService service, final UniMessage<?> request) {
        final String redirectUri = request.header(WeCoAction.HEADER_REDIRECT_URI);
        final String state = request.header(WeCoAction.HEADER_STATE);

        if (StrUtil.isBlank(redirectUri) || StrUtil.isBlank(state)) {
            throw new IllegalArgumentException("缺少必要 Header: redirectUri 或 state");
        }

        // 构造扫码 URL (企微不需要 scope 参数，方法签名不同)
        final String url = service.buildQrConnectUrl(redirectUri, state);
        return UniResponse.success(url);
    }

    private UniResponse handleLoginByCode(final WxCpService service, final UniMessage<?> request) throws Exception {
        final String code = (String) request.payload();

        // 企微 Code 换取 UserID (内部员工) 或 OpenID (外部联系人)
        final WxCpOauth2UserInfo userInfo = service.getOauth2Service().getUserInfo(code);

        log.info("[ R2MO ] 企微扫码登录成功，UserID: {}", userInfo.getUserId());
        return UniResponse.success(userInfo);
    }

    // --- 客户端工厂 ---

    private WxCpService getService(final WeComCredential cred, final WeComContext ctx) {
        final String cacheKey = ctx.hashCode() + "@" + cred.hashCode();

        return CC_CLIENT.pick(() -> {
            log.info("[ R2MO ] 初始化新的 WeCom 客户端：{}", cacheKey);

            final WxCpDefaultConfigImpl config = new WxCpDefaultConfigImpl();
            config.setCorpId(cred.corpId());
            config.setCorpSecret(cred.secret());
            config.setAgentId(cred.agentId()); // 企微扫码强制要求 AgentId

            // 使用 Helper 统一处理代理
            WeCoAction.Helper.applyProxy(config, ctx.getProxy());

            // 注入 Host (企微私有化部署支持)
            // 如果 Context 指定了 Host，则修改 BaseApiUrl
            if (StrUtil.isNotBlank(ctx.getHost())) {
                final String protocol = ctx.getOrDefault(UniContext.KEY_PROTOCOL, "https");
                // 企微 SDK 要求完整的 URL 前缀 (包含 protocol 和 结尾斜杠)
                config.setBaseApiUrl(protocol + "://" + ctx.getHost() + "/");
            }

            final WxCpService service = new WxCpServiceImpl();
            service.setWxCpConfigStorage(config);
            return service;
        }, cacheKey);
    }
}
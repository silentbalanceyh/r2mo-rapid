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
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import me.chanjar.weixin.common.bean.oauth2.WxOAuth2AccessToken;
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
    public UniResponse exchange(final UniAccount account, final UniMessage<?> request, final UniContext context) {
        // 1. 凭证校验
        if (!(account.credential() instanceof final WeChatCredential cred)) {
            throw new IllegalArgumentException("[ R2MO ] 凭证类型不匹配，需要 WeChatCredential");
        }
        // Context 依然复用 WeChatContext (之前的定义)
        if (!(context instanceof final WeChatContext ctx)) {
            throw new IllegalArgumentException("[ R2MO ] 上下文类型不匹配，需要 WeChatContext");
        }

        // 2. 获取客户端
        final WxMpService service = this.getService(cred, ctx);

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
            log.error("[ R2MO ] WeChat 操作失败：{}", ex.getMessage());
            throw new _500ServerInternalException(ex.getMessage());
        }
    }

    // --- 内部逻辑 ---

    private UniResponse handleGetUrl(final WxMpService service, final UniMessage<?> request) {
        final String redirectUri = request.header(WeCoAction.HEADER_REDIRECT_URI);
        final String state = request.header(WeCoAction.HEADER_STATE);

        if (StrUtil.isBlank(redirectUri) || StrUtil.isBlank(state)) {
            throw new IllegalArgumentException("缺少必要 Header: redirectUri 或 state");
        }

        // 构造扫码 URL (开放平台 snsapi_login)
        final String url = service.buildQrConnectUrl(redirectUri, "snsapi_login", state);
        return UniResponse.success(url);
    }

    private UniResponse handleLoginByCode(final WxMpService service, final UniMessage<?> request) throws Exception {
        final String code = (String) request.payload();

        // 换 Token & UserInfo
        final WxOAuth2AccessToken accessToken = service.getOAuth2Service().getAccessToken(code);
        final WxOAuth2UserInfo userInfo = service.getOAuth2Service().getUserInfo(accessToken, null);

        log.info("[ R2MO ] 微信登录成功，OpenID: {}", userInfo.getOpenid());
        return UniResponse.success(userInfo);
    }

    // --- 客户端工厂 ---

    private WxMpService getService(final WeChatCredential cred, final WeChatContext ctx) {
        final String cacheKey = ctx.hashCode() + "@" + cred.hashCode();

        return CC_CLIENT.pick(() -> {
            log.info("[ R2MO ] 初始化新的 WeChat 客户端：{}", cacheKey);

            final WxMpDefaultConfigImpl config = new WxMpDefaultConfigImpl();
            config.setAppId(cred.appId());
            config.setSecret(cred.secret());

            // 使用 Helper 统一处理代理
            WeCoAction.Helper.applyProxy(config, ctx.getProxy());

            // 注入重试
            config.setMaxRetryTimes(ctx.getMaxRetry());

            final WxMpService service = new WxMpServiceImpl();
            service.setWxMpConfigStorage(config);
            return service;
        }, cacheKey);
    }
}

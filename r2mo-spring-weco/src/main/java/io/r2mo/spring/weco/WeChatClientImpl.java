package io.r2mo.spring.weco;

import io.r2mo.base.exchange.UniAccount;
import io.r2mo.base.exchange.UniContext;
import io.r2mo.base.exchange.UniMessage;
import io.r2mo.base.exchange.UniProvider;
import io.r2mo.base.exchange.UniResponse;
import io.r2mo.spi.SPI;
import io.r2mo.typed.cc.Cc;
import io.r2mo.typed.json.JObject;
import io.r2mo.xync.weco.WeCoConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 微信公众号业务客户端实现
 *
 * @author lang : 2025-12-09
 */
@Service
public class WeChatClientImpl implements WeChatClient {

    // 缓存 Provider 实例，避免重复 SPI 查找
    private static final Cc<String, UniProvider> CC_PROVIDER = Cc.openThread();

    @Autowired
    private WeCoConfig config;

    @Override
    public JObject authUrl(final String redirectUri, final String state) {
        // 1. 准备参数 (无需 Payload)
        final JObject params = SPI.J();

        // 2. 设置头部指令
        // 告诉 Provider 执行 "获取认证URL" 操作，并传递必要参数
        final Map<String, Object> headers = Map.of(
            "action", WeCoConstant.WX_AUTH_URL,
            WeCoConstant.HEADER_REDIRECT_URI, redirectUri,
            WeCoConstant.HEADER_STATE, state
        );

        return this.doExchange(params, headers);
    }

    @Override
    public JObject login(final String code) {
        // 1. 准备参数 (Payload 为 code)
        final JObject params = SPI.J()
            .put("code", code);

        // 2. 设置头部指令 (执行登录)
        final Map<String, Object> headers = Map.of(
            "action", WeCoConstant.WX_LOGIN_BY
        );

        return this.doExchange(params, headers);
    }

    @Override
    public JObject qrCode() {
        final JObject params = SPI.J();

        final Map<String, Object> headers = Map.of(
            "action", WeCoConstant.APP_AUTH_QR,
            "expireSeconds", String.valueOf(this.config.getWechat().getExpireSeconds())
        );

        return this.doExchange(params, headers);
    }

    @Override
    public JObject checkStatus(final String uuid) {
        // Payload 约定为 UUID (对应 WeCoActionStatus 的 request.payload())
        // WeCoBuilder 会将 "code" 或 "content" 作为 Payload
        final JObject params = SPI.J()
            .put("code", uuid);

        final Map<String, Object> headers = Map.of(
            "action", WeCoConstant.APP_STATUS
        );

        return this.doExchange(params, headers);
    }

    /**
     * 核心交换逻辑
     */
    private JObject doExchange(final JObject params, final Map<String, Object> headers) {
        // 1. 获取转换器
        final UniProvider.Wait<WeCoConfig.WeChat> wait = UniProvider.waitFor(WeChatWaitSpring::new);
        final WeCoConfig.WeChat wechatConfig = this.config.getWechat();

        // 2. 转换为标准对象 (Account, Context, Message)
        final UniAccount account = wait.account(params, wechatConfig);
        final UniContext context = wait.context(params, wechatConfig);
        final UniMessage<String> message = wait.message(params, headers, wechatConfig);

        // 3. 获取底层 Provider (SPI ID: UNI_WECHAT)
        final UniProvider provider = CC_PROVIDER.pick(() -> SPI.findOne(UniProvider.class, "UNI_WECHAT"));

        // 4. 执行并返回
        final UniResponse response = provider.exchange(account, message, context);
        return (JObject) response.content();
    }
}
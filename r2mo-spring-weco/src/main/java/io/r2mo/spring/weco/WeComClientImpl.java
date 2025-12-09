package io.r2mo.spring.weco;

import io.r2mo.base.exchange.UniAccount;
import io.r2mo.base.exchange.UniContext;
import io.r2mo.base.exchange.UniMessage;
import io.r2mo.base.exchange.UniProvider;
import io.r2mo.base.exchange.UniResponse;
import io.r2mo.spi.SPI;
import io.r2mo.typed.cc.Cc;
import io.r2mo.typed.json.JObject;
import io.r2mo.xync.weco.WeCoAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 企业微信业务客户端实现
 *
 * @author lang : 2025-12-09
 */
@Service
public class WeComClientImpl implements WeComClient {

    private static final Cc<String, UniProvider> CC_PROVIDER = Cc.openThread();

    @Autowired
    private WeCoConfig config;

    @Override
    public JObject authUrl(final String redirectUri, final String state) {
        final JObject params = SPI.J();

        final Map<String, Object> headers = Map.of(
            "action", WeCoAction.GET_AUTH_URL,
            WeCoAction.HEADER_REDIRECT_URI, redirectUri,
            WeCoAction.HEADER_STATE, state
        );

        return this.doExchange(params, headers);
    }

    @Override
    public JObject login(final String code) {
        // 放入 code，WaitSpring 会将其提取到 message payload 中
        final JObject params = SPI.J()
            .put("code", code);

        final Map<String, Object> headers = Map.of(
            "action", WeCoAction.LOGIN_BY_CODE
        );

        return this.doExchange(params, headers);
    }

    private JObject doExchange(final JObject params, final Map<String, Object> headers) {
        // 1. 获取企微转换器
        final UniProvider.Wait<WeCoConfig.WeCom> wait = UniProvider.waitFor(WeComWaitSpring::new);
        final WeCoConfig.WeCom wecomConfig = this.config.getWecom();

        // 2. 转换标准对象
        final UniAccount account = wait.account(params, wecomConfig);
        final UniContext context = wait.context(params, wecomConfig);
        final UniMessage<String> message = wait.message(params, headers, wecomConfig);

        // 3. 获取底层 Provider (SPI ID: UNI_WECOM)
        final UniProvider provider = CC_PROVIDER.pick(() -> SPI.findOne(UniProvider.class, "UNI_WECOM"));

        // 4. 执行并返回
        final UniResponse response = provider.exchange(account, message, context);
        return (JObject) response.content();
    }
}
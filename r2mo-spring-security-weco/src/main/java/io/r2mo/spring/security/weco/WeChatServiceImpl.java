package io.r2mo.spring.security.weco;

import io.r2mo.function.Fn;
import io.r2mo.spring.security.weco.exception._80502Exception501WeChatDisabled;
import io.r2mo.spring.security.weco.exception._80503Exception401WeChatAuthFailure;
import io.r2mo.spring.weco.WeChatClient;
import io.r2mo.spring.weco.WeCoConfig;
import io.r2mo.typed.json.JObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @author lang : 2025-12-09
 */
@Service
@Slf4j
public class WeChatServiceImpl implements WeChatService {

    @Autowired
    private WeChatClient weChatClient;

    @Autowired
    private WeCoConfig config;

    @Override
    public JObject getAuthUrl(final String redirectUri, final String state) {
        // 1. 确认模块开启
        this.checkEnabled();

        // 2. 调用 Client 获取链接
        return this.weChatClient.authUrl(redirectUri, state);
    }

    @Override
    public WeChatLoginRequest validate(final WeChatLoginRequest request) {
        this.checkEnabled();

        final String code = request.getCode();

        // 1. 远程换取 Token/Info
        final JObject result = this.weChatClient.login(code);

        // 2. 提取关键信息 (OpenID)
        final String openId = result.getString("openid");
        Fn.jvmKo(Objects.isNull(openId), _80502Exception501WeChatDisabled.class);

        // 3. 填充身份标识
        // 这里会自动联动设置父类的 id = openId
        request.setOpenId(openId);
        request.setUnionId(result.getString("unionid")); // 只有绑定了开放平台才有

        log.info("[ R2MO ] WeChat 认证通过, OpenID: {}", openId);
        return request;
    }

    @Override
    public JObject getQrCode() {
        this.checkEnabled();
        return this.weChatClient.qrCode();
    }

    @Override
    public JObject checkStatus(final String uuid) {
        this.checkEnabled();
        return this.weChatClient.checkStatus(uuid);
    }

    private void checkEnabled() {
        Fn.jvmKo(Objects.isNull(this.config.getWechat()), _80503Exception401WeChatAuthFailure.class);
    }
}
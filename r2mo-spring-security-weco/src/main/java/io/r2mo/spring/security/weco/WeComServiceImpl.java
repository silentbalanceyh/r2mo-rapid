package io.r2mo.spring.security.weco;

import io.r2mo.function.Fn;
import io.r2mo.spring.security.weco.exception._80552Exception501WeComDisabled;
import io.r2mo.spring.security.weco.exception._80553Exception401WeComAuthFailure;
import io.r2mo.spring.weco.WeCoConfig;
import io.r2mo.spring.weco.WeComClient;
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
public class WeComServiceImpl implements WeComService {

    @Autowired
    private WeComClient weComClient;

    @Autowired
    private WeCoConfig config;

    @Override
    public JObject getAuthUrl(final String redirectUri, final String state) {
        // 1. 确认模块开启
        this.checkEnabled();

        // 2. 调用 Client 获取链接
        return this.weComClient.authUrl(redirectUri, state);
    }

    @Override
    public WeComLoginRequest validate(final WeComLoginRequest request) {
        this.checkEnabled();

        final String code = request.getCode();

        // 1. 远程换取 Token/Info
        final JObject result = this.weComClient.login(code);

        // 2. 提取关键信息 (UserID 或 OpenID)
        // 企微逻辑：优先取 UserId (内部成员)，取不到则取 OpenId (外部联系人)
        String userId = result.getString("UserId");
        if (userId == null) {
            userId = result.getString("OpenId");
        }

        // 参照 WeChat 实现，此处校验失败抛出 501 Disabled 类型异常
        Fn.jvmKo(Objects.isNull(userId), _80552Exception501WeComDisabled.class);

        // 3. 填充身份标识
        // 这里会自动联动设置父类的 id = userId
        request.setUserId(userId);

        log.info("[ R2MO ] WeCom 认证通过, UserID: {}", userId);
        return request;
    }

    @Override
    public JObject getQrCode(final String redirectUri) {
        this.checkEnabled();
        return this.weComClient.qrCode(redirectUri);
    }

    @Override
    public JObject checkStatus(final String uuid) {
        this.checkEnabled();
        return this.weComClient.checkStatus(uuid);
    }

    private void checkEnabled() {
        // 参照 WeChat 实现，此处配置缺失抛出 401 AuthFailure 类型异常
        Fn.jvmKo(Objects.isNull(this.config.getWecom()), _80553Exception401WeComAuthFailure.class);
    }
}
package io.r2mo.spring.security.weco;

import cn.hutool.core.util.StrUtil;
import io.r2mo.function.Fn;
import io.r2mo.jaas.session.UserAt;
import io.r2mo.spring.security.auth.AuthService;
import io.r2mo.spring.security.auth.AuthTokenResponse;
import io.r2mo.spring.security.weco.exception._80552Exception501WeComDisabled;
import io.r2mo.spring.security.weco.exception._80553Exception401WeComAuthFailure;
import io.r2mo.spring.security.weco.exception._80554Exception401WeComBlocked;
import io.r2mo.spring.weco.WeComClient;
import io.r2mo.spring.weco.config.WeCoConfig;
import io.r2mo.typed.json.JObject;
import io.r2mo.xync.weco.WeCoSession;
import io.r2mo.xync.weco.wecom.WeComIdentify;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.time.Duration;
import java.util.Objects;
import java.util.UUID;

/**
 * @author lang : 2025-12-09
 */
@Service
@Slf4j
public class WeComServiceImpl implements WeComService {

    @Autowired
    private WeComClient weComClient;

    @Autowired
    private AuthService authService;

    @Autowired
    private WeCoConfig config;

    @Override
    public WeComIdentify initialize(final String targetUrl) {
        this.checkEnabled();
        final WeCoConfig.WeComCp wecomConfig = this.config.getWecomCp();
        // 黑名单校验
        this.validateDomain(targetUrl);
        // 生成流程 ID（state）
        final String state = UUID.randomUUID().toString().replace("-", "");
        final WeComIdentify identify = new WeComIdentify()
            .state(state)
            .url(targetUrl);
        // 缓存会话信息
        final String sessionKey = WeCoSession.keyOf(state);
        WeCoSession.of().save(sessionKey, identify.cached(), Duration.ofSeconds(wecomConfig.getExpireSeconds()));    // 默认：5分钟有效期
        log.info("[ R2MO ] 企微登录初始化完成，sessionKey = {}, state = {}", sessionKey, state);
        return identify;
    }

    private void validateDomain(final String targetUrl) {
        // 构造 state 参数，这个流程中必须，targetUrl 必须是白名单中的 URL
        final WeCoConfig.WeComCp wecomConfig = this.config.getWecomCp();
        final String host = URI.create(targetUrl).getHost();
        if (Objects.isNull(host) || wecomConfig.getBlockDomains().contains(host)) {
            throw new _80554Exception401WeComBlocked(targetUrl);
        }
    }

    @Override
    public WeComIdentify validate(final WeComLoginRequest request) {
        this.checkEnabled();

        final String code = request.getCode();

        // 1. 远程换取 Token/Info
        final JObject result = this.weComClient.login(code);

        // 2. 提取关键信息 (UserID 或 OpenID)
        // 企微逻辑：优先取 UserId (内部成员)，取不到则取 OpenId (外部联系人)
        String userId = result.getString("userId");
        if (userId == null) {
            userId = result.getString("openId");
        }

        // 参照 WeChat 实现，此处校验失败抛出 501 Disabled 类型异常
        Fn.jvmKo(Objects.isNull(userId), _80553Exception401WeComAuthFailure.class);

        // 3. 填充身份标识
        // 这里会自动联动设置父类的 id = userId
        request.setUserId(userId);

        final UserAt userAt = this.authService.login(request);
        Fn.jvmKo(Objects.isNull(userAt), _80553Exception401WeComAuthFailure.class);


        final AuthTokenResponse response = new AuthTokenResponse(userAt);
        final String token = response.getToken();
        Fn.jvmKo(StrUtil.isEmpty(token), _80553Exception401WeComAuthFailure.class);

        log.info("[ R2MO ] WeCom 认证通过, UserID: {}", userId);
        final String sessionKey = WeCoSession.keyOf(request.getState());
        final WeCoConfig.WeComCp wecomConfig = this.config.getWecomCp();
        final String cached = WeCoSession.of().get(sessionKey, Duration.ofSeconds(wecomConfig.getExpireSeconds()));

        final WeComIdentify identify = new WeComIdentify(cached);
        identify.token(token);
        return identify;
    }

    @Override
    public JObject getQrCode(final String state) {
        this.checkEnabled();
        return this.weComClient.qrCode(state);
    }

    private void checkEnabled() {
        // 参照 WeChat 实现，此处配置缺失抛出 401 AuthFailure 类型异常
        Fn.jvmKo(Objects.isNull(this.config.getWecomCp()), _80552Exception501WeComDisabled.class);
    }
}
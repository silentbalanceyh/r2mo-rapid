package io.r2mo.spring.security.weco;

import io.r2mo.function.Fn;
import io.r2mo.jaas.session.UserAt;
import io.r2mo.spi.SPI;
import io.r2mo.spring.security.auth.AuthService;
import io.r2mo.spring.security.auth.TokenDynamicResponse;
import io.r2mo.spring.security.weco.exception._80502Exception501WeChatDisabled;
import io.r2mo.spring.security.weco.exception._80503Exception401WeChatAuthFailure;
import io.r2mo.spring.weco.WeChatClient;
import io.r2mo.spring.weco.config.WeCoConfig;
import io.r2mo.typed.json.JObject;
import io.r2mo.xync.weco.WeCoSession;
import io.r2mo.xync.weco.wechat.WeArgsCallback;
import io.r2mo.xync.weco.wechat.WeArgsSignature;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
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
    private AuthService authService;

    @Autowired
    private WeCoConfig config;

    @Override
    public JObject getAuthUrl(final String redirectUri, final String state) {
        // 1. 确认模块开启
        this.enabledOpen();

        // 2. 调用 Client 获取链接
        return this.weChatClient.authUrl(redirectUri, state);
    }

    @Override
    public WeChatReqPreLogin validate(final WeChatReqPreLogin request) {
        this.enabledOpen();

        final String code = request.getCode();

        // 1. 远程换取 Token/Info
        final JObject result = this.weChatClient.login(code);

        // 2. 提取关键信息 (OpenID)
        final String openId = result.getString("openid");
        Fn.jvmKo(Objects.isNull(openId), _80503Exception401WeChatAuthFailure.class);

        // 3. 填充身份标识
        // 这里会自动联动设置父类的 id = openId
        request.setOpenId(openId);
        request.setUnionId(result.getString("unionid")); // 只有绑定了开放平台才有

        log.info("[ R2MO ] WeChat 认证通过, OpenID: {}", openId);
        return request;
    }

    // -------------------------------------------- 公众号扫码登录
    @Override
    public JObject getQrCode() {
        this.enabledMp();
        return this.weChatClient.qrCode();
    }

    @Override
    public JObject checkStatus(final String uuid) {
        this.enabledMp();
        return this.weChatClient.checkStatus(uuid);
    }

    @Override
    public boolean checkEcho(final WeArgsSignature params) {
        this.enabledMp();
        return this.weChatClient.checkEcho(params);
    }

    @Override
    public JObject extract(final String uuid, final WeArgsCallback params) {
        this.enabledMp();
        try {
            // 提取用户信息生成 LoginRequest
            final JObject checked = this.weChatClient.extractUser(params);
            final WeChatReqAccount request = new WeChatReqAccount(checked);


            // 登录执行，
            final UserAt userAt = this.authService.login(request);
            final TokenDynamicResponse response = new TokenDynamicResponse(userAt);


            // 更新 token 保证 WeCoSession 中的 /wechat-status 能检查到最新的 Token
            final String sessionKey = WeCoSession.keyOf(uuid);
            final Duration expiredAt = Duration.ofSeconds(this.config.getWechatMp().getExpireSeconds());
            WeCoSession.of().save(sessionKey, response.getToken(), expiredAt);


            // 返回登录结果
            return SPI.J()
                .put("id", userAt.id())
                .put("token", response.getToken());
        } catch (final Throwable ex) {
            /*
             * 此处的 try-catch 由于是异步模式，是必须要存在的，否则内部执行过程中如果不清楚情况的话会导致一些问题，典型如：
             * - [ R2MO-10404 ] [ R2MO ] 用户提供者未配置：BeanName = UserAt/ID_WECHAT, details = No bean named 'UserAt/ID_WECHAT' available
             * 上述异常是用户未提供相关配置导致的，也是微信登录必须实现的 UserAtService 的核心接口
             */
            log.error(ex.getMessage(), ex);
            return null;
        }
    }

    private void enabledOpen() {
        Fn.jvmKo(Objects.isNull(this.config.getWechatOpen()), _80502Exception501WeChatDisabled.class);
    }

    private void enabledMp() {
        Fn.jvmKo(Objects.isNull(this.config.getWechatMp()), _80502Exception501WeChatDisabled.class);
    }
}
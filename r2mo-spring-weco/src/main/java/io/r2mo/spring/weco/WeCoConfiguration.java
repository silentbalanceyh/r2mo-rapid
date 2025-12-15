package io.r2mo.spring.weco;

import cn.hutool.core.util.StrUtil;
import io.r2mo.function.Fn;
import io.r2mo.spring.weco.config.WeCoConfig;
import io.r2mo.spring.weco.exception._80501Exception500WeChatConfig;
import io.r2mo.spring.weco.exception._80551Exception500WeComConfig;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

/**
 * 微信体系自动配置与校验
 *
 * @author lang : 2025-12-09
 */
@Configuration
@Slf4j
public class WeCoConfiguration {

    @Autowired
    private WeCoConfig config;

    @PostConstruct
    public void configured() {
        // 1-1. 尝试配置微信公众号
        final boolean isWeChatMp = this.configuredWeChatMp(this.config.getWechatMp());

        // 1-2. 尝试配置微信开放平台
        final boolean isWeChatOpen = this.configuredWeChatOpen(this.config.getWechatOpen());

        // 2-1. 尝试配置企业微信
        final boolean isWeCom = this.configuredWeCom(this.config.getWecomCp());

        if (!isWeChatOpen && !isWeChatMp && !isWeCom) {
            log.warn("[ R2MO ] WeCo 模块已加载，但未检测到任何有效配置 (wechat/wecom)。");
        }
    }

    private boolean configuredWeChatOpen(final WeCoConfig.WeChatOpen wechatOpen) {
        if (wechatOpen == null) {
            return false;
        }

        // AppID 检查
        Fn.jvmKo(StrUtil.isEmpty(wechatOpen.getAppId()), _80501Exception500WeChatConfig.class, "app-id");

        // Secret 检查
        Fn.jvmKo(StrUtil.isEmpty(wechatOpen.getSecret()), _80501Exception500WeChatConfig.class, "secret");

        // Redirect URI 检查
        Fn.jvmKo(StrUtil.isEmpty(wechatOpen.getRedirectUri()), _80501Exception500WeChatConfig.class, "redirect-uri");

        log.info("[ R2MO ] ----> 已启用 WeChat (开放平台) 服务模块！[AppID: {}]", wechatOpen.getAppId());
        return true;
    }

    private boolean configuredWeChatMp(final WeCoConfig.WeChatMp wechat) {
        if (wechat == null) {
            return false;
        }

        // AppID 检查
        Fn.jvmKo(StrUtil.isEmpty(wechat.getAppId()), _80501Exception500WeChatConfig.class, "app-id");

        // Secret 检查
        Fn.jvmKo(StrUtil.isEmpty(wechat.getSecret()), _80501Exception500WeChatConfig.class, "secret");

        // Token 检查
        Fn.jvmKo(StrUtil.isEmpty(wechat.getToken()), _80501Exception500WeChatConfig.class, "token");

        log.info("[ R2MO ] ----> 已启用 WeChat (公众号) 服务模块！[AppID: {}]", wechat.getAppId());
        return true;
    }

    private boolean configuredWeCom(final WeCoConfig.WeComCp wecom) {
        if (wecom == null) {
            return false;
        }

        // CorpID 检查
        Fn.jvmKo(StrUtil.isEmpty(wecom.getCorpId()), _80551Exception500WeComConfig.class, "corp-id");

        // Secret 检查
        Fn.jvmKo(StrUtil.isEmpty(wecom.getSecret()), _80551Exception500WeComConfig.class, "secret");

        // AgentID 检查
        Fn.jvmKo(wecom.getAgentId() == null, _80551Exception500WeComConfig.class, "agent-id");

        // Callback 检查（必须配置）
        Fn.jvmKo(StrUtil.isEmpty(wecom.getUrlCallback()), _80551Exception500WeComConfig.class, "callback");

        log.info("[ R2MO ] ----> 已启用 WeCom (企业微信) 服务模块！[CorpID: {}, AgentID: {}]", wecom.getCorpId(), wecom.getAgentId());
        return true;
    }
}
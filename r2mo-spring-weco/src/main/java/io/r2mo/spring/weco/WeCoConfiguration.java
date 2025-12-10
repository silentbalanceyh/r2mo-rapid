package io.r2mo.spring.weco;

import cn.hutool.core.util.StrUtil;
import io.r2mo.function.Fn;
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
        // 1. 尝试配置微信公众号
        final boolean isWeChat = this.configuredWeChat(this.config.getWechat());

        // 2. 尝试配置企业微信
        final boolean isWeCom = this.configuredWeCom(this.config.getWecom());

        // 3. 兜底提示
        if (!isWeChat && !isWeCom) {
            log.warn("[ R2MO ] WeCo 模块已加载，但未检测到任何有效配置 (wechat/wecom)。");
        }
    }

    private boolean configuredWeChat(final WeCoConfig.WeChat wechat) {
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

    private boolean configuredWeCom(final WeCoConfig.WeCom wecom) {
        if (wecom == null) {
            return false;
        }

        // CorpID 检查
        Fn.jvmKo(StrUtil.isBlank(wecom.getCorpId()), _80551Exception500WeComConfig.class, "corp-id");

        // Secret 检查
        Fn.jvmKo(StrUtil.isBlank(wecom.getSecret()), _80551Exception500WeComConfig.class, "secret");

        // AgentID 检查
        Fn.jvmKo(wecom.getAgentId() == null, _80551Exception500WeComConfig.class, "agent-id");

        log.info("[ R2MO ] ----> 已启用 WeCom (企业微信) 服务模块！[AgentID: {}]", wecom.getAgentId());
        return true;
    }
}
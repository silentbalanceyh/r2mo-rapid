package io.r2mo.spring.security.oauth2;

import cn.hutool.extra.spring.SpringUtil;
import io.r2mo.spring.security.extension.AuthSwitcher;
import io.r2mo.spring.security.oauth2.config.ConfigOAuth2;
import io.r2mo.typed.annotation.SPID;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lang : 2025-11-13
 */
@Slf4j
@SPID
public class OAuth2Switcher implements AuthSwitcher {
    private final ConfigOAuth2 config;

    public OAuth2Switcher() {
        this.config = SpringUtil.getBean(ConfigOAuth2.class);
    }

    @Override
    public boolean hasJwt() {
        final boolean hasJwt = this.config != null
            && this.config.isOn()
            && (this.config.isJwt() || this.config.isOidc());
        log.info("[ R2MO ] OAuth2Switcher 检测到 OAuth2 JWT 认证方式：{}", hasJwt);
        return hasJwt;
    }

    @Override
    public boolean hasOAuth2() {
        return true;
    }
}

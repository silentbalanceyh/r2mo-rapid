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
    private final boolean hasJwt;

    public OAuth2Switcher() {
        final ConfigOAuth2 config = SpringUtil.getBean(ConfigOAuth2.class);

        this.hasJwt = config != null
            && config.isOn()
            && (config.isJwt() || config.isOidc());

        log.info("[ R2MO ] OAuth2Switcher 检测到 OAuth2 JWT 认证方式：{}", this.hasJwt);
    }

    @Override
    public boolean hasJwt() {
        return this.hasJwt;
    }

    @Override
    public boolean hasOAuth2() {
        return true;
    }
}

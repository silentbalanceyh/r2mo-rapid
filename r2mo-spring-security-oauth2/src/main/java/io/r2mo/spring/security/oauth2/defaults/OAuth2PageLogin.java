package io.r2mo.spring.security.oauth2.defaults;

import io.r2mo.spi.SPI;
import io.r2mo.typed.cc.Cc;

/**
 * @author lang : 2025-12-05
 */
public interface OAuth2PageLogin {

    Cc<String, OAuth2PageLogin> CC_SKELETON = Cc.openThread();

    static OAuth2PageLogin of() {
        return CC_SKELETON.pick(() -> SPI.findOneOf(OAuth2PageLogin.class));
    }

    String loginPage(String error);
}

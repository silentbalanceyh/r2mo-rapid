package io.r2mo.spring.security.extension;

import io.r2mo.spring.security.config.ConfigSecurity;

import java.util.Set;

/**
 * SPI（注册专用）
 *
 * @author lang : 2025-11-11
 */
public interface RequestSkip {

    Set<String> openApi(ConfigSecurity security);
}

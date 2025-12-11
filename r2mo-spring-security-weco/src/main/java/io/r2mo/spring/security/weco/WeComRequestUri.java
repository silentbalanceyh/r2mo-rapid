package io.r2mo.spring.security.weco;

import io.r2mo.spring.security.config.ConfigSecurity;
import io.r2mo.spring.security.extension.RequestUri;

import java.util.Set;

/**
 * @author lang : 2025-12-11
 */
public class WeComRequestUri implements RequestUri {
    @Override
    public Set<String> ignores(final ConfigSecurity security) {
        return Set.of("/WW_verify_*.txt:GET");
    }
}

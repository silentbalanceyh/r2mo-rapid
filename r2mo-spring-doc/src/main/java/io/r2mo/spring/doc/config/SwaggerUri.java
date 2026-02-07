package io.r2mo.spring.doc.config;

import io.r2mo.spring.security.config.ConfigSecurity;
import io.r2mo.spring.security.extension.RequestUri;

import java.util.Set;

public class SwaggerUri implements RequestUri {
    @Override
    public Set<String> ignores(final ConfigSecurity security) {
        return Set.of(
            "/swagger-ui.html:GET",
            "/swagger-ui/**:GET",
            "/v3/api-docs:GET",
            "/v3/api-docs/**:GET",
            // --- 追加 Knife4j 必需路径 ---
            "/doc.html:GET",          // Knife4j 入口界面
            "/webjars/**:GET"         // Knife4j 依赖的静态资源（js/css）
        );
    }
}

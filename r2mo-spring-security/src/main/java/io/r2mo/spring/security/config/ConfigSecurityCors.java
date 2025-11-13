package io.r2mo.spring.security.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * CORS 配置属性类，绑定到 app.cors.*
 *
 * @author lang : 2025-11-13
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "security.cors")
public class ConfigSecurityCors {

    /**
     * 允许的源（支持通配符模式，如 <a href="http://localhost">...</a>:*）
     */
    private List<String> allowedOriginPatterns = List.of("http://localhost:*", "http://127.0.0.1:*");

    /**
     * 允许的 HTTP 方法
     */
    private List<String> allowedMethods = List.of("GET", "POST", "PUT", "DELETE", "OPTIONS");

    /**
     * 允许的请求头
     */
    private List<String> allowedHeaders = List.of("*");

    /**
     * 是否允许携带凭证（如 cookies）
     */
    private boolean allowCredentials = false;

    /**
     * CORS 配置生效的路径模式
     */
    private List<String> pathPatterns = List.of("/**");
}
package io.r2mo.spring.security.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.io.Serializable;

/**
 * @author lang : 2025-11-28
 */
@Configuration
@ConfigurationProperties(prefix = "security.url")
@Data
public class ConfigSecurityUri implements Serializable {

    private String welcome = "/welcome";

    private String login = "/login";

    private String logout = "/logout";

    private String error = "/error";
}

package io.r2mo.spring.security.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lang : 2025-11-11
 */
@Configuration
@ConfigurationProperties(prefix = "spring.security.user")
@Data
public class ConfigUser implements Serializable {
    private String name;
    private String password;
    private List<String> roles = new ArrayList<>();
    private boolean enabled;
}

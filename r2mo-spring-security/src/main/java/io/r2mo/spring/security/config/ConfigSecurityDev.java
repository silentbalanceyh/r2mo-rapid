package io.r2mo.spring.security.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.io.Serializable;

@Data
@Configuration
@ConfigurationProperties(prefix = "security.development")
public class ConfigSecurityDev implements Serializable {
    @JsonProperty("header-name")
    private String headerName;
    @JsonProperty("header-value")
    private String headerValue;

    private String username;

    private String password;
}

package io.r2mo.spring.common.config;

import io.r2mo.spi.SPI;
import io.r2mo.typed.json.JObject;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "app")
@Data
@Slf4j
public class WebApp {

    private String clientId;
    private String clientSecret;
    private String tenant;
    private String name;

    private Map<String, Map<String, Object>> integration = new HashMap<>();
    private Map<String, JObject> configData = new HashMap<>();

    public JObject getIntegration(final String integration) {
        return this.configData.get(integration);
    }

    public void finishConfiguration() {
        this.integration.forEach((key, value) -> {
            final JObject configItem = SPI.J();
            this.configData.put(key, configItem.put(value));
        });
    }

    @PostConstruct
    public void init() {
        log.info("[ R2MO ] 应用集成配置：{}", this.integration.size());
        this.finishConfiguration();
    }
}

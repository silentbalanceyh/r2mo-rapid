package io.r2mo.spring.security.actuator;

import io.r2mo.spring.security.config.ConfigSecurity;
import io.r2mo.spring.security.extension.RequestUri;

import java.util.Set;

/**
 * Spring Boot Actuator 端点白名单（免认证）
 * 通过 SPI 自动注册，所有 R2MO Spring 项目统一生效
 *
 * @author lang
 */
public class ActuatorRequestUri implements RequestUri {
    @Override
    public Set<String> ignores(final ConfigSecurity security) {
        return Set.of(
            // Health - 存活/就绪探针
            "/actuator/health:GET",
            "/actuator/health/**:GET",
            // Info - 应用元数据
            "/actuator/info:GET",
            // Prometheus - 监控指标
            "/actuator/prometheus:GET"
        );
    }
}

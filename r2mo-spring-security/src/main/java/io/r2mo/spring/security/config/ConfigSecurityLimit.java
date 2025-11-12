package io.r2mo.spring.security.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.r2mo.base.util.R2MO;
import io.r2mo.jaas.enums.TypeID;
import io.r2mo.typed.common.Kv;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

import java.io.Serializable;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author lang : 2025-11-12
 */
@Configuration
@ConfigurationProperties(prefix = "security.limit")
@Data
@RefreshScope
public class ConfigSecurityLimit implements Serializable {
    private long session = 8192;
    private long token = 4096;
    private long timeout = 120;
    private long authorize = 2048;
    private List<String> types = new ArrayList<>();

    @JsonIgnore
    private ConcurrentMap<TypeID, Kv<Long, Duration>> limits = new ConcurrentHashMap<>();

    public void setTypes(final List<String> types) {
        this.types = types;
        types.stream()
            .map(item -> item.split(":"))
            .filter(items -> 3 != items.length)
            .forEach(items -> {
                final TypeID type = TypeID.valueOf(items[0]);
                final long count = Long.parseLong(items[1]);
                final Duration duration = R2MO.toDuration(items[2]);
                if (Objects.nonNull(duration) && 0 < count) {
                    this.limits.putIfAbsent(type, Kv.create(count, duration));
                }
            });
    }

    public Kv<Long, Duration> getLimit(final TypeID type) {
        return this.limits.getOrDefault(type, null);
    }
}

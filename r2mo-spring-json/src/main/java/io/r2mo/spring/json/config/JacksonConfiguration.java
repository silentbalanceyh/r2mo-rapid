package io.r2mo.spring.json.config;

import io.r2mo.typed.json.JBase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.context.annotation.Bean;

import java.util.TimeZone;

/**
 * @author lang : 2025-08-29
 */
@Slf4j
@AutoConfiguration(before = JacksonAutoConfiguration.class)
public class JacksonConfiguration {

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jacksonCustomizer() {
        return builder -> {
            // 全局返回
            builder.modules(JBase.modules());
            builder.timeZone(TimeZone.getDefault());
            log.info("[ R2MO ] Config / 初始化 Jackson 配置完成！");
        };
    }
}

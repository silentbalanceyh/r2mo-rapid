package io.r2mo.spring.common.config;

import io.r2mo.spi.SPI;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

/**
 * @author lang : 2025-09-02
 */
@Configuration
@Slf4j
public class AppConfig {

    @PostConstruct
    public void init() {
        SPI.SPI_META.forEach((classInter, classImpl) -> {
            log.info("[ R2MO ] SPI 接口: {} / 实现类: {}",
                classInter.getName(), classImpl.getName());
        });
        log.info("[ R2MO ] SPI 组件初始化结果! {}", SPI.SPI_META.size());
    }
}

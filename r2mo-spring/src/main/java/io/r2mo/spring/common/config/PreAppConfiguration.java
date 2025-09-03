package io.r2mo.spring.common.config;

import io.r2mo.base.io.HStore;
import io.r2mo.base.web.ForAbort;
import io.r2mo.base.web.ForLocale;
import io.r2mo.base.web.ForStatus;
import io.r2mo.spi.SPI;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

/**
 * @author lang : 2025-09-02
 */
@Configuration
@Slf4j
public class PreAppConfiguration {

    @PostConstruct
    public void init() {
        SPI.SPI_META.forEach((classInter, classImpl) ->
            log.info("[ R2MO ] SPI 实现类: {} / 接口: {}", classImpl.getName(), classInter.getName()));

        final HStore store = SPI.V_STORE;
        if (Objects.nonNull(store)) {
            log.info("[ R2MO ] (HStore) 组件加载：{}", store.getClass().getName());
        }

        final ForAbort abort = SPI.V_ABORT;
        if (Objects.nonNull(abort)) {
            log.info("[ R2MO ] (Web Abort) 组件加载：{}", abort.getClass().getName());
            log.info("[ R2MO ]    (FailOr) 子组件：Jvm = {}", abort.failJvm());
            log.info("[ R2MO ]    (FailOr) 子组件容器：Container = {}", abort.failContainer());
            log.info("[ R2MO ]    (FailOr) 子组件App：App = {}", abort.failApp());
        }

        final ForStatus status = SPI.V_STATUS;
        if (Objects.nonNull(status)) {
            log.info("[ R2MO ] (Web Status) 状态组件：{}", status.getClass().getName());
        }
        final ForLocale locale = SPI.V_LOCALE;
        if (Objects.nonNull(locale)) {
            log.info("[ R2MO ] (Web Locale) 国际化组件加载：{}", locale.getClass().getName());
        }
        log.info("[ R2MO ] SPI 组件初始化结果! {}", SPI.SPI_META.size());
    }
}

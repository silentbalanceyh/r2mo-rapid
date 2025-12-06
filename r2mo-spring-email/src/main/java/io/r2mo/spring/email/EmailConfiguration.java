package io.r2mo.spring.email;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class EmailConfiguration {

    @PostConstruct
    public void configured() {
        log.info("[ R2MO ] ----> 已启用邮件服务模块！");
    }
}

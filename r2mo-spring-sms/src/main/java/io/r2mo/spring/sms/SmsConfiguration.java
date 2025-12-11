package io.r2mo.spring.sms;

import cn.hutool.core.util.StrUtil;
import io.r2mo.function.Fn;
import io.r2mo.spring.sms.exception._80351Exception404SmsAccessId;
import io.r2mo.spring.sms.exception._80352Exception404SmsAccessSecret;
import io.r2mo.spring.sms.exception._80353Exception404SmsSignName;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

/**
 * @author lang : 2025-12-08
 */
@Configuration
@Slf4j
public class SmsConfiguration {

    @Autowired
    private SmsConfig smsConfig;

    @PostConstruct
    public void configured() {
        final String accessId = this.smsConfig.getAccessId();
        // Access ID 检查
        Fn.jvmKo(StrUtil.isEmpty(accessId), _80351Exception404SmsAccessId.class);

        final String accessSecret = this.smsConfig.getAccessSecret();
        // Access Secret 检查
        Fn.jvmKo(StrUtil.isEmpty(accessSecret), _80352Exception404SmsAccessSecret.class);

        final String signName = this.smsConfig.getSignName();
        // 短信签名检查
        Fn.jvmKo(StrUtil.isEmpty(signName), _80353Exception404SmsSignName.class);

        log.info("[ R2MO ] ----> 已启用短信服务模块！");
    }
}

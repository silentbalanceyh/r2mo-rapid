package io.r2mo.spring.security.sms;

import cn.hutool.core.util.RandomUtil;
import io.r2mo.spring.security.auth.ServicePreAuth;
import io.r2mo.typed.common.Kv;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author lang : 2025-12-08
 */
@Service("PreAuth/SMS")
public class SmsServicePreAuth implements ServicePreAuth {
    @Autowired
    private SmsCaptchaConfig config;

    @Override
    public Kv<String, String> authorize(final String identifier) {
        // 生成验证码
        final int length = this.config.getLength();
        final String captcha = RandomUtil.randomNumbers(length);
        return Kv.create(identifier, captcha);
    }
}

package io.r2mo.spring.security.email;

import cn.hutool.core.util.RandomUtil;
import io.r2mo.spring.security.auth.ServicePreAuth;
import io.r2mo.typed.common.Kv;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Email 的验证码生成流程，根据配置来设置
 *
 * @author lang : 2025-12-08
 */
@Service("PreAuth/EMAIL")
public class EmailServicePreAuth implements ServicePreAuth {
    @Autowired
    private EmailCaptchaConfig config;

    @Override
    public Kv<String, String> authorize(final String identifier) {
        // 生成验证码
        final int length = this.config.getLength();
        final String captcha = RandomUtil.randomNumbers(length);
        return Kv.create(identifier, captcha);
    }
}

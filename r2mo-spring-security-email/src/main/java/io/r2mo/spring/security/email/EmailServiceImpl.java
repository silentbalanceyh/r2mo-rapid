package io.r2mo.spring.security.email;

import cn.hutool.core.util.RandomUtil;
import io.r2mo.base.util.R2MO;
import io.r2mo.spi.SPI;
import io.r2mo.spring.email.EmailClient;
import io.r2mo.spring.security.auth.AuthService;
import io.r2mo.typed.json.JObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 此处负责参数准备
 *
 * @author lang : 2025-12-07
 */
@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private EmailClient client;

    @Autowired
    private EmailCaptchaConfig config;

    @Autowired
    private AuthService authService;

    @Override
    public boolean sendCaptcha(final Set<String> toSet) {
        // 1. 提取基础参数
        final int length = this.config.getLength();
        final int expiredAt = this.config.getExpiredAt();       // 秒
        final String template = this.config.getTemplate();
        final String subject = this.config.getSubject();
        // 2. 提取 AuthService
        final JObject params = SPI.J();
        params.put("subject", subject);
        params.put("expiredAt", R2MO.uiDate(expiredAt, TimeUnit.SECONDS));
        // 3. 生成随机码
        final String captcha = RandomUtil.randomNumbers(length);
        params.put("captcha", captcha);

        // 4. 发送邮件
        final JObject sent = this.client.send(template, params, toSet);
        return false;
    }
}

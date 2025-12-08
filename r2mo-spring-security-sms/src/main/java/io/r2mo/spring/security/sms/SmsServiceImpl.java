package io.r2mo.spring.security.sms;

import io.r2mo.base.util.R2MO;
import io.r2mo.spi.SPI;
import io.r2mo.spring.security.auth.AuthService;
import io.r2mo.spring.sms.SmsClient;
import io.r2mo.typed.json.JObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * @author lang : 2025-12-08
 */
@Service
public class SmsServiceImpl implements SmsService {
    @Autowired
    private SmsClient client;

    @Autowired
    private SmsCaptchaConfig config;

    @Autowired
    private AuthService authService;

    @Override
    public boolean sendCaptcha(final String mobile) {
        // 1. 提取基础参数
        final String template = this.config.getTemplate();
        final SmsLoginRequest request = new SmsLoginRequest();
        request.setId(mobile);
        // 2. 生成验证码
        final String captcha = this.authService.authorize(request, this.config.expiredAt());
        final JObject params = SPI.J();
        params.put("captcha", captcha);
        // 3. 发送短信
        final JObject sent = this.client.send(template, params, Set.of(mobile));
        return R2MO.valueT(sent, "success");
    }
}

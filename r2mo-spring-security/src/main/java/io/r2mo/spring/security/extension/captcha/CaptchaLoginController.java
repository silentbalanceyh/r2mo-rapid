package io.r2mo.spring.security.extension.captcha;

import io.r2mo.spi.SPI;
import io.r2mo.typed.json.JObject;
import io.r2mo.typed.webflow.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@Slf4j
public class CaptchaLoginController {

    @Autowired
    private CaptchaService captchaService;

    @GetMapping("/auth/captcha")
    public R<JObject> loginCaptcha() {
        final Map<String, Object> captcha = this.captchaService.generate();
        return R.ok(SPI.J().put(captcha));
    }
}

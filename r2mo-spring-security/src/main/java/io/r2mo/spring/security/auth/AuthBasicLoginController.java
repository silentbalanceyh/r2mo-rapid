package io.r2mo.spring.security.auth;

import io.r2mo.jaas.session.UserAt;
import io.r2mo.spring.security.basic.BasicLoginRequest;
import io.r2mo.spring.security.basic.BasicLoginResponse;
import io.r2mo.spring.security.extension.captcha.CaptchaOn;
import io.r2mo.typed.json.JObject;
import io.r2mo.typed.webflow.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class AuthBasicLoginController {

    @Autowired
    private AuthService authService;

    @PostMapping("/auth/login")
    @CaptchaOn
    public R<BasicLoginResponse> loginBasic(final JObject requestJ) {
        final BasicLoginRequest request = new BasicLoginRequest(requestJ);
        final UserAt userAt = this.authService.login(request);
        return R.ok(new BasicLoginResponse(userAt));
    }
}

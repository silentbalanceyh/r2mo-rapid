package io.r2mo.spring.security.exchange;

import io.r2mo.jaas.session.UserAt;
import io.r2mo.spring.security.auth.AuthService;
import io.r2mo.spring.security.config.ConfigSecurity;
import io.r2mo.typed.json.JObject;
import io.r2mo.typed.webflow.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 第三方集成 Token 交换端点
 *
 * @author lang : 2026-05-06
 */
@RestController
@Slf4j
@Tag(name = "Auth", description = "认证接口")
@ConditionalOnProperty(prefix = "security.exchange", name = "enabled", havingValue = "true")
public class ExchangeController {

    @Autowired
    private AuthService authService;

    @Autowired
    private ConfigSecurity config;

    @PostMapping("/auth/exchange")
    @Operation(summary = "第三方集成 Token 交换", description = "通过 clientId/clientSecret 获取访问令牌")
    public R<ExchangeLoginResponse> exchange(final JObject requestJ) {
        final ExchangeLoginRequest request = new ExchangeLoginRequest(requestJ);
        final UserAt userAt = this.authService.login(request);
        return R.ok(new ExchangeLoginResponse(userAt));
    }
}

package io.r2mo.spring.security.ldap;

import io.r2mo.function.Fn;
import io.r2mo.jaas.session.UserAt;
import io.r2mo.spring.security.auth.AuthService;
import io.r2mo.spring.security.auth.TokenDynamicResponse;
import io.r2mo.spring.security.ldap.exception._80401Exception501LdapDisabled;
import io.r2mo.typed.json.JObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

/**
 * @author lang : 2025-12-09
 */
@RestController
@Slf4j
public class LdapCommonController {

    @Autowired
    private LdapService ldapService;

    @Autowired
    private AuthService authService;

    @Autowired
    private LdapConfig config;

    /**
     * <pre>
     *     {
     *         "username": "通常是 email 格式",
     *         "password": "用户密码"
     *     }
     * </pre>
     *
     * @param params 参数信息
     * @return 登录结果
     */
    @PostMapping("/auth/ldap-login")
    public TokenDynamicResponse login(final JObject params) {
        Fn.jvmKo(Objects.isNull(this.config), _80401Exception501LdapDisabled.class);

        Fn.jvmKo(!this.config.isEnabled(), _80401Exception501LdapDisabled.class);
        final LdapLoginRequest request = new LdapLoginRequest(params);
        final LdapLoginRequest requestValid = this.ldapService.validate(request);
        final UserAt userAt = this.authService.login(requestValid);
        return new TokenDynamicResponse(userAt);
    }
}

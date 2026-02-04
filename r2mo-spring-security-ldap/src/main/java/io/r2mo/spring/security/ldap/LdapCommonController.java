package io.r2mo.spring.security.ldap;

import io.r2mo.function.Fn;
import io.r2mo.jaas.session.UserAt;
import io.r2mo.openapi.components.schemas.RequestLoginCommon;
import io.r2mo.openapi.components.schemas.ResponseLoginDynamic;
import io.r2mo.openapi.operations.DescAuth;
import io.r2mo.openapi.operations.DescMeta;
import io.r2mo.spring.security.auth.AuthService;
import io.r2mo.spring.security.auth.TokenDynamicResponse;
import io.r2mo.spring.security.extension.captcha.CaptchaOn;
import io.r2mo.spring.security.ldap.exception._80401Exception501LdapDisabled;
import io.r2mo.typed.json.JObject;
import io.r2mo.typed.webflow.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.core.MediaType;
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
@Tag(name = DescAuth.group)
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
    @CaptchaOn
    @Operation(
        summary = DescAuth._auth_ldap_login_summary, description = DescAuth._auth_ldap_login_desc,
        requestBody = @RequestBody(
            required = true, description = DescMeta.request_post,
            /*
             * - username
             * - password
             * - captcha              (optional)
             * - captchaId            (optional)
             */
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(implementation = RequestLoginCommon.class)
            )
        ),
        responses = {
            @ApiResponse(
                responseCode = DescMeta.response_code_200,
                description = DescMeta.response_ok_json,
                /*
                 * - data
                 *   - id
                 *   - token
                 *   - refreshToken
                 */
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(name = "data", implementation = ResponseLoginDynamic.class)
                )
            )
        }
    )
    public R<TokenDynamicResponse> login(final JObject params) {
        Fn.jvmKo(Objects.isNull(this.config), _80401Exception501LdapDisabled.class);

        Fn.jvmKo(!this.config.isEnabled(), _80401Exception501LdapDisabled.class);
        final LdapLoginRequest request = new LdapLoginRequest(params);
        final LdapLoginRequest requestValid = this.ldapService.validate(request);
        final UserAt userAt = this.authService.login(requestValid);
        return R.ok(new TokenDynamicResponse(userAt));
    }
}

package io.r2mo.spring.security.jwt;

import io.r2mo.jaas.session.UserAt;
import io.r2mo.openapi.components.schemas.RequestLoginCommon;
import io.r2mo.openapi.components.schemas.ResponseLoginJwt;
import io.r2mo.openapi.operations.DescAuth;
import io.r2mo.openapi.operations.DescMeta;
import io.r2mo.spring.security.auth.AuthService;
import io.r2mo.spring.security.extension.captcha.CaptchaOn;
import io.r2mo.typed.json.JObject;
import io.r2mo.typed.webflow.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.core.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lang : 2025-11-12
 */
@RestController
@Tag(name = DescAuth.group)
public class JwtLoginController {

    @Autowired
    private AuthService authService;

    @PostMapping("/auth/jwt-login")
    @CaptchaOn
    @Operation(
        summary = DescAuth._auth_jwt_login_summary, description = DescAuth._auth_jwt_login_desc,
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
                 *   - token
                 *   - refreshToken
                 *   - tokenType
                 *   - expiresIn
                 */
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(name = "data", implementation = ResponseLoginJwt.class)
                )
            )
        }
    )
    public R<JwtLoginResponse> loginJwt(final JObject requestJ) {
        final JwtLoginRequest request = new JwtLoginRequest(requestJ);
        final UserAt userAt = this.authService.login(request);
        return R.ok(new JwtLoginResponse(userAt));
    }
}

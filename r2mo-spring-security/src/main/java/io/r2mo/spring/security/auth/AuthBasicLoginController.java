package io.r2mo.spring.security.auth;

import io.r2mo.jaas.session.UserAt;
import io.r2mo.openapi.components.schemas.RequestLoginCommon;
import io.r2mo.openapi.components.schemas.ResponseLoginCommon;
import io.r2mo.openapi.operations.DescAuth;
import io.r2mo.openapi.operations.DescMeta;
import io.r2mo.spring.security.basic.BasicLoginRequest;
import io.r2mo.spring.security.basic.BasicLoginResponse;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@Tag(name = DescAuth.group, description = DescAuth.description)
public class AuthBasicLoginController {

    @Autowired
    private AuthService authService;

    @PostMapping("/auth/login")
    @CaptchaOn
    @Operation(
        summary = DescAuth._auth_login_summary, description = DescAuth._auth_login_desc,
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
                 *   - id
                 *   - username
                 */
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(name = "data", implementation = ResponseLoginCommon.class)
                )
            )
        }
    )
    public R<BasicLoginResponse> loginBasic(final JObject requestJ) {
        final BasicLoginRequest request = new BasicLoginRequest(requestJ);
        final UserAt userAt = this.authService.login(request);
        return R.ok(new BasicLoginResponse(userAt));
    }
}

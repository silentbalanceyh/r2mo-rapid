package io.r2mo.spring.security.email;

import cn.hutool.core.util.StrUtil;
import io.r2mo.base.util.R2MO;
import io.r2mo.function.Fn;
import io.r2mo.jaas.auth.LoginID;
import io.r2mo.jaas.session.UserAt;
import io.r2mo.openapi.components.schemas.RequestEmailLogin;
import io.r2mo.openapi.components.schemas.RequestEmailSend;
import io.r2mo.openapi.components.schemas.ResponseLoginDynamic;
import io.r2mo.openapi.operations.DescAuth;
import io.r2mo.openapi.operations.DescMeta;
import io.r2mo.spring.security.auth.AuthService;
import io.r2mo.spring.security.auth.TokenDynamicResponse;
import io.r2mo.spring.security.email.exception._80301Exception400EmailRequired;
import io.r2mo.spring.security.email.exception._80302Exception400EmailFormat;
import io.r2mo.spring.security.email.exception._80303Exception500EmailSending;
import io.r2mo.spring.security.extension.captcha.CaptchaOn;
import io.r2mo.typed.json.JObject;
import io.r2mo.typed.webflow.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.core.MediaType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 核心接口
 * <pre>
 *     /auth/email-send
 *     /auth/email-login
 * </pre>
 *
 * @author lang : 2025-12-05
 */
@RestController
@Slf4j
@Tag(
    name = DescAuth.group,
    description = DescAuth.description
)
public class EmailCommonController {

    @Autowired
    private EmailService service;

    @Autowired
    private AuthService authService;

    /**
     * <pre>
     *     {
     *         "email": "account@xxx.com"
     *     }
     * </pre>
     *
     * @param params 参数信息
     * @return 发送结果
     */
    @PostMapping("/auth/email-send")
    @CaptchaOn
    @Operation(
        summary = DescAuth._auth_email_send_summary, description = DescAuth._auth_email_send_desc,
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true, description = DescMeta.request_post,
            /*
             * - email
             * - captcha          (optional)
             * - captchaId        (optional)
             */
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(implementation = RequestEmailSend.class)
            )
        ),
        responses = {
            @ApiResponse(
                responseCode = DescMeta.response_code_200,
                description = DescMeta.response_ok_json,
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(
                        name = "data",
                        type = "boolean",
                        description = DescMeta.response_ok_boolean
                    )
                )
            )
        }
    )
    public R<Boolean> send(@RequestBody final JObject params) {
        final String email = R2MO.valueT(params, LoginID.EMAIL);
        // 必须输入邮箱
        Fn.jvmKo(StrUtil.isEmpty(email), _80301Exception400EmailRequired.class);
        // 邮箱格式检查
        Fn.jvmKo(!R2MO.isEmail(email), _80302Exception400EmailFormat.class, email);
        // 构造 to 清单
        final boolean sent = this.service.sendCaptcha(email);
        // 发送过程失败
        Fn.jvmKo(!sent, _80303Exception500EmailSending.class, email);
        // 验证码处理过程
        return R.ok(Boolean.TRUE);
    }

    /**
     * <pre>
     *    {
     *        "email": "???",
     *        "captcha": "1234"
     *    }
     * </pre>
     *
     * @param params 参数信息
     * @return 发送结果
     */
    @PostMapping("/auth/email-login")
    @Operation(
        summary = DescAuth._auth_email_login_summary, description = DescAuth._auth_email_login_desc,
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true, description = DescMeta.request_post,
            /*
             * - email
             * - captcha
             */
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(implementation = RequestEmailLogin.class)
            )
        ),
        responses = {
            @ApiResponse(
                responseCode = DescMeta.response_code_200,
                description = DescMeta.response_ok_json,
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(name = "data", implementation = ResponseLoginDynamic.class)
                )
            )
        }
    )
    public R<TokenDynamicResponse> login(final JObject params) {
        final EmailLoginRequest request = new EmailLoginRequest(params);
        final UserAt userAt = this.authService.login(request);
        return R.ok(new TokenDynamicResponse(userAt));
    }
}

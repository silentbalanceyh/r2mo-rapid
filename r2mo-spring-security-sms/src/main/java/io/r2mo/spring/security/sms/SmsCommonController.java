package io.r2mo.spring.security.sms;

import cn.hutool.core.util.StrUtil;
import io.r2mo.base.util.R2MO;
import io.r2mo.function.Fn;
import io.r2mo.jaas.auth.LoginID;
import io.r2mo.jaas.session.UserAt;
import io.r2mo.openapi.components.schemas.RequestSmsLogin;
import io.r2mo.openapi.components.schemas.RequestSmsSend;
import io.r2mo.openapi.components.schemas.ResponseLoginDynamic;
import io.r2mo.openapi.operations.DescAuth;
import io.r2mo.openapi.operations.DescMeta;
import io.r2mo.spring.security.auth.AuthService;
import io.r2mo.spring.security.auth.TokenDynamicResponse;
import io.r2mo.spring.security.extension.captcha.CaptchaOn;
import io.r2mo.spring.security.sms.exception._80381Exception400MobileRequired;
import io.r2mo.spring.security.sms.exception._80382Exception400MobileFormat;
import io.r2mo.spring.security.sms.exception._80383Exception500MobileSending;
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
 *     /auth/sms-send
 *     /auth/sms-login
 * </pre>
 *
 * @author lang : 2025-12-08
 */
@RestController
@Slf4j
@Tag(
    name = DescAuth.group,
    description = DescAuth.description
)
public class SmsCommonController {

    @Autowired
    private SmsService service;

    @Autowired
    private AuthService authService;

    /**
     * <pre>
     *     {
     *         "mobile": "13800000000"
     *     }
     * </pre>
     *
     * @param params 参数信息
     * @return 发送结果
     */
    @PostMapping("/auth/sms-send")
    @CaptchaOn
    @Operation(
        summary = DescAuth._auth_sms_send_summary, description = DescAuth._auth_sms_send_desc,
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true, description = DescMeta.request_post,
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(implementation = RequestSmsSend.class)
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
        final String mobile = R2MO.valueT(params, LoginID.MOBILE);
        // 必须输入手机号
        Fn.jvmKo(StrUtil.isEmpty(mobile), _80381Exception400MobileRequired.class);
        // 手机格式检查
        Fn.jvmKo(!R2MO.isMobile(mobile), _80382Exception400MobileFormat.class, mobile);
        // 构造 to 清单
        final boolean sent = this.service.sendCaptcha(mobile);
        // 发送过程失败
        Fn.jvmKo(!sent, _80383Exception500MobileSending.class, mobile);
        // 验证处理过程
        return R.ok(Boolean.TRUE);
    }

    /**
     * <pre>
     *     {
     *         "mobile": "???",
     *         "captcha": "1234"
     *     }
     * </pre>
     *
     * @param params 参数信息
     * @return 发送结果
     */
    @PostMapping("/auth/sms-login")
    @Operation(
        summary = DescAuth._auth_sms_login_summary, description = DescAuth._auth_sms_login_desc,
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true, description = DescMeta.request_post,
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(implementation = RequestSmsLogin.class)
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
        final SmsLoginRequest request = new SmsLoginRequest(params);
        final UserAt userAt = this.authService.login(request);
        return R.ok(new TokenDynamicResponse(userAt));
    }
}

package io.r2mo.spring.security.weco;

import io.r2mo.openapi.components.schemas.WeComInitResponse;
import io.r2mo.openapi.components.schemas.WeComQrResponse;
import io.r2mo.openapi.operations.DescAuth;
import io.r2mo.openapi.operations.DescMeta;
import io.r2mo.spi.SPI;
import io.r2mo.spring.security.weco.exception._80553Exception401WeComAuthFailure;
import io.r2mo.typed.json.JObject;
import io.r2mo.typed.webflow.R;
import io.r2mo.xync.weco.wecom.WeComIdentify;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.core.MediaType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 企业微信 (WeCom) 认证控制器
 *
 * @author lang : 2025-12-09
 */
@RestController
@Slf4j
@Tag(
    name = DescAuth.group,
    description = DescAuth.description
)
public class WeComCommonController {

    private static final String COOKIE_NAME = "R2MO_WECOM_COOKIE";
    @Autowired
    private WeComService weComService;

    @GetMapping("/auth/wecom-init")
    @Operation(
        summary = DescAuth._auth_wecom_init_summary,
        description = DescAuth._auth_wecom_init_desc,
        parameters = {
            @Parameter(
                name = "targetUrl",
                description = DescAuth.P.targetUrl,
                in = ParameterIn.QUERY,
                example = "https://console.r2mo.io"
            )
        },
        responses = {
            @ApiResponse(
                responseCode = DescMeta.response_code_200,
                description = DescMeta.response_ok_json,
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    // 关联之前定义的 Response 结构
                    schema = @Schema(name = "data", implementation = WeComInitResponse.class)
                )
            )
        }
    )
    public R<JObject> init(@RequestParam("targetUrl") final String targetUrl, final HttpServletResponse response) {
        /*
         * 返回结果
         * - state
         * - session
         */
        final WeComIdentify identify = this.weComService.initialize(targetUrl);
        final JObject responseJ = identify.response();
        log.info("[ R2MO ] 状态信息：{}", responseJ.encode());
        return R.ok(responseJ);
    }

    /**
     * 企业微信 Code 登录
     * <p>POST /auth/wecom-login</p>
     */
    @GetMapping("/auth/wecom-login")
    @Operation(
        summary = DescAuth._auth_wecom_login_summary,
        description = DescAuth._auth_wecom_login_desc,
        parameters = {
            @Parameter(
                name = "code",
                description = DescAuth.P.code,
                in = ParameterIn.QUERY,
                required = true,
                example = "ww_auth_code_123456"
            ),
            @Parameter(
                name = "state",
                description = DescAuth.P.state,
                in = ParameterIn.QUERY,
                required = true,
                example = "e1eea04ed597465c833418d4cdc9373b"
            )
        },
        responses = {
            @ApiResponse(
                // ✅ 重点：标注为 302 重定向
                responseCode = DescMeta.response_code_302,
                description = DescAuth.P.targetUrl,
                content = @Content(
                    // 这里的 String 是重定向的目标 URL，通常表现为纯文本或 HTML
                    mediaType = MediaType.TEXT_HTML,
                    schema = @Schema(
                        type = "string",
                        example = "https://console.r2mo.io/dashboard?token=eyJhbGciOiJIUz..."
                    )
                )
            )
        }
    )
    public void login(@RequestParam("code") final String code,  // 2. 参数直接从 URL 里的 code 取
                      @RequestParam("state") final String state,
                      final HttpServletResponse response // 3. 引入 Response 对象用于重定向
    ) {
        // 1. 构造专用请求 (构造函数内自动校验 code 非空)
        final JObject params = SPI.J()
            .put("code", code).put("state", state);
        log.info("[ R2MO ] 企微登录请求参数：{}", params.encode());
        final WeComLoginRequest request = new WeComLoginRequest(params);

        // 2. 业务校验 & 获取 UserID
        final WeComIdentify validated = this.weComService.validate(request);

        try {

            // 4. 重定向到目标地址
            response.sendRedirect(this.pageOf(validated.url(), validated.token()));
        } catch (final Throwable ex) {
            log.error(ex.getMessage(), ex);
            throw new _80553Exception401WeComAuthFailure();
        }
    }

    private String pageOf(final String url, final String token) {
        return url.contains("?") ? url + "&token=" + token : url + "?token=" + token;
    }

    /**
     * 获取企业微信扫码登录二维码 (SSO URL)
     * <p>GET /auth/wecom-qrcode</p>
     */
    @GetMapping("/auth/wecom-qrcode")
    @Operation(
        summary = DescAuth._auth_wecom_qrcode_summary,
        description = DescAuth._auth_wecom_qrcode_desc,
        parameters = {
            @Parameter(
                name = "state",
                description = DescAuth.P.state,
                in = ParameterIn.QUERY,
                required = true,
                example = "e1eea04ed597465c833418d4cdc9373b"
            )
        },
        responses = {
            @ApiResponse(
                responseCode = DescMeta.response_code_200,
                description = DescMeta.response_ok_json,
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    // 关联之前定义的 Response 结构
                    schema = @Schema(name = "data", implementation = WeComQrResponse.class)
                )
            )
        }
    )
    public R<JObject> getQrCode(@RequestParam("state") final String state) {
        return R.ok(this.weComService.getQrCode(state));
    }
}
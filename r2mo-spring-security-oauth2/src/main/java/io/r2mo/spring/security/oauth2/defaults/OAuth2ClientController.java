package io.r2mo.spring.security.oauth2.defaults;

import io.r2mo.openapi.components.schemas.OAuth2TokenResponse;
import io.r2mo.openapi.operations.DescAuth;
import io.r2mo.openapi.operations.DescMeta;
import io.r2mo.typed.json.JObject;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@Tag(
    name = DescAuth.group,
    description = DescAuth.description
)
public class OAuth2ClientController {

    /**
     * 客户端默认回调
     *
     * @param registrationId 客户端注册 ID
     * @param code           授权码
     * @param state          状态参数   xxx_VC_{VERIFIER_CODE}
     * @param error          错误信息
     * @return 处理结果
     */
    @GetMapping(value = "/oauth2/authorized/{registrationId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
        summary = DescAuth._oauth2_callback_summary,
        description = DescAuth._oauth2_callback_desc,
        parameters = {
            // Path 参数：注册ID (如 github, google)
            @Parameter(
                name = "clientId",
                description = DescAuth.OAuth2.registrationId,
                in = ParameterIn.PATH,
                required = true,
                example = "github"
            ),
            // Query 参数：成功时的 code
            @Parameter(
                name = "code",
                description = DescAuth.OAuth2.code,
                in = ParameterIn.QUERY,
                example = "a1b2c3d4e5f6..."
            ),
            // Query 参数：状态码 (防 CSRF)
            @Parameter(
                name = "state",
                description = DescAuth.OAuth2.state,
                in = ParameterIn.QUERY,
                required = true,
                example = "xyz123"
            ),
            // Query 参数：失败时的 error
            @Parameter(
                name = "error",
                description = DescAuth.OAuth2.error,
                in = ParameterIn.QUERY,
                example = "access_denied"
            )
        },
        responses = {
            @ApiResponse(
                responseCode = DescMeta.response_code_200,
                description = DescMeta.response_ok_json,
                content = @Content(
                    mediaType = jakarta.ws.rs.core.MediaType.APPLICATION_JSON,
                    // 返回通用的 JsonObject (可能包含 token 或 user 信息)
                    schema = @Schema(implementation = OAuth2TokenResponse.class)
                )
            )
        }
    )
    public JObject handleCallback(@PathVariable("registrationId") final String registrationId,
                                  @RequestParam(name = "code", required = false) final String code,
                                  @RequestParam(name = "state", required = false) final String state,
                                  @RequestParam(name = "error", required = false) final String error) {
        // 这里可以处理授权回调逻辑
        log.info("[ R2MO ] OAuth2 授权回调：{}", registrationId);
        return OAuth2ClientAuthorized.of().handleToken(registrationId, code, state, error);
    }
}

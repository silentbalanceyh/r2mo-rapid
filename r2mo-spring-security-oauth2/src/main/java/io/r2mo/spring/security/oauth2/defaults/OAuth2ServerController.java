package io.r2mo.spring.security.oauth2.defaults;

import io.r2mo.openapi.components.schemas.RequestLoginCommon;
import io.r2mo.openapi.components.schemas.ResponseLoginCommon;
import io.r2mo.openapi.operations.DescAuth;
import io.r2mo.openapi.operations.DescMeta;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.core.MediaType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Objects;

/**
 * @author lang : 2025-11-28
 */
@Controller
@Slf4j
@Tag(name = DescAuth.group, description = DescAuth.description)
public class OAuth2ServerController {

    /**
     * 登录页跳转处理
     * <p>注意：这里返回 String 代表视图名称，Spring 会自动去 templates 找对应的 HTML</p>
     */
    @GetMapping("/login")
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
    public String handleLogin(final Model model, @RequestParam(name = "error", required = false) final String error) {

        // 1. 将 error 参数传递给 Thymeleaf 上下文，供前端 th:if 判断使用
        if (error != null) {
            model.addAttribute("error", error);
        }

        // 2. 尝试获取 SPI 扩展
        final OAuth2PageLogin found = OAuth2PageLogin.of();

        // 3. 决定视图名称：
        //    - 如果 SPI 存在，使用 SPI 指定的名称
        //    - 否则使用默认名称 "login"
        return (Objects.nonNull(found)) ? found.loginPage(error) : "login";
    }
}

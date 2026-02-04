package io.r2mo.spring.security.extension.captcha;

import io.r2mo.openapi.components.schemas.ResponseCaptcha;
import io.r2mo.openapi.operations.DescAuth;
import io.r2mo.openapi.operations.DescMeta;
import io.r2mo.spi.SPI;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@Slf4j
@Tag(
    name = DescAuth.group,
    description = DescAuth.description
)
public class CaptchaLoginController {

    @Autowired
    private CaptchaService captchaService;

    @GetMapping("/auth/captcha")
    @Operation(
        summary = DescAuth._auth_captcha_summary, description = DescAuth._auth_captcha_desc,
        responses = {
            @ApiResponse(
                responseCode = DescMeta.response_code_200,
                description = DescMeta.response_ok_json,
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(name = "data", implementation = ResponseCaptcha.class)
                )
            )
        }
    )
    public R<JObject> loginCaptcha() {
        final Map<String, Object> captcha = this.captchaService.generate();
        return R.ok(SPI.J().put(captcha));
    }
}

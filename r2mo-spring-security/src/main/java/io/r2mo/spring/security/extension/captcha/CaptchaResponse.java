package io.r2mo.spring.security.extension.captcha;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * @author lang : 2025-11-13
 */
@Data
@Schema(description = "验证码响应")
public class CaptchaResponse implements Serializable {
    private String id;
    private String image;
    private Long expiredAt = 60L; // 秒，一般是 60
}

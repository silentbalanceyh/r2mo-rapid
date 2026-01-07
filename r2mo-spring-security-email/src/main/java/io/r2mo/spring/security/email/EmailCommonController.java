package io.r2mo.spring.security.email;

import cn.hutool.core.util.StrUtil;
import io.r2mo.base.util.R2MO;
import io.r2mo.function.Fn;
import io.r2mo.jaas.auth.LoginID;
import io.r2mo.jaas.session.UserAt;
import io.r2mo.spring.security.auth.AuthService;
import io.r2mo.spring.security.auth.TokenDynamicResponse;
import io.r2mo.spring.security.email.exception._80301Exception400EmailRequired;
import io.r2mo.spring.security.email.exception._80302Exception400EmailFormat;
import io.r2mo.spring.security.email.exception._80303Exception500EmailSending;
import io.r2mo.spring.security.extension.captcha.CaptchaOn;
import io.r2mo.typed.json.JObject;
import io.r2mo.typed.webflow.R;
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
    public R<TokenDynamicResponse> login(final JObject params) {
        final EmailLoginRequest request = new EmailLoginRequest(params);
        final UserAt userAt = this.authService.login(request);
        return R.ok(new TokenDynamicResponse(userAt));
    }
}

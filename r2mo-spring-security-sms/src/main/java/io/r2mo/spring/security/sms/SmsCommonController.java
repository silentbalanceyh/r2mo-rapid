package io.r2mo.spring.security.sms;

import cn.hutool.core.util.StrUtil;
import io.r2mo.base.util.R2MO;
import io.r2mo.function.Fn;
import io.r2mo.jaas.auth.LoginID;
import io.r2mo.jaas.session.UserAt;
import io.r2mo.spring.security.auth.AuthService;
import io.r2mo.spring.security.auth.AuthTokenResponse;
import io.r2mo.spring.security.sms.exception._80381Exception400MobileRequired;
import io.r2mo.spring.security.sms.exception._80382Exception400MobileFormat;
import io.r2mo.spring.security.sms.exception._80383Exception500SendingFailure;
import io.r2mo.typed.json.JObject;
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
     *
     * @return 发送结果
     */
    @PostMapping("/auth/sms-send")
    public Boolean send(@RequestBody final JObject params) {
        final String mobile = R2MO.valueT(params, LoginID.MOBILE);
        // 必须输入手机号
        Fn.jvmKo(StrUtil.isEmpty(mobile), _80381Exception400MobileRequired.class);
        // 手机格式检查
        Fn.jvmKo(!R2MO.isMobile(mobile), _80382Exception400MobileFormat.class, mobile);
        // 构造 to 清单
        final boolean sent = this.service.sendCaptcha(mobile);
        // 发送过程失败
        Fn.jvmKo(!sent, _80383Exception500SendingFailure.class, mobile);
        // 验证处理过程
        return true;
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
     *
     * @return 发送结果
     */
    @PostMapping("/auth/sms-login")
    public AuthTokenResponse login(final JObject params) {
        final SmsLoginRequest request = new SmsLoginRequest(params);
        final UserAt userAt = this.authService.login(request);
        return new AuthTokenResponse(userAt);
    }
}

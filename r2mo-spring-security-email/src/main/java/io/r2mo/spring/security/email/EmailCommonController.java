package io.r2mo.spring.security.email;

import cn.hutool.core.util.StrUtil;
import io.r2mo.base.util.R2MO;
import io.r2mo.function.Fn;
import io.r2mo.spring.security.email.exception._80301Exception400EmailRequired;
import io.r2mo.spring.security.email.exception._80302Exception400EmailFormat;
import io.r2mo.typed.json.JObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Set;

/**
 * 核心接口
 * <pre>
 *     /auth/email-send
 *     /auth/email-login
 * </pre>
 *
 * @author lang : 2025-12-05
 */
@Controller
@Slf4j
public class EmailCommonController {

    @Autowired
    private EmailService service;

    /**
     * <pre>
     *     {
     *         "email": "account@xxx.com"
     *     }
     * </pre>
     *
     * @param params 参数信息
     *
     * @return 发送结果
     */
    @PostMapping("/auth/email-send")
    public Boolean send(@RequestBody final JObject params) {
        final String email = R2MO.valueT(params, "email");
        // 必须输入邮箱
        Fn.jvmKo(StrUtil.isEmpty(email), _80301Exception400EmailRequired.class);
        // 邮箱格式检查
        Fn.jvmKo(!R2MO.isEmail(email), _80302Exception400EmailFormat.class);
        // 构造 to 清单
        final Set<String> toSet = Set.of(email);
        return this.service.sendCaptcha(toSet);
    }

    /**
     * <pre>
     *
     * </pre>
     *
     * @param params 参数信息
     *
     * @return 发送结果
     */
    @PostMapping("/auth/email-login")
    public JObject login(final JObject params) {

        return null;
    }
}

package io.r2mo.spring.security.oauth2.defaults;

import io.r2mo.typed.json.JObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author lang : 2025-11-28
 */
@Controller
@Slf4j
public class OAuth2CommonController {
    // 注入我们刚才创建的 HTML 文件资源
    @Value("classpath:login.view.html")
    private Resource loginPage;

    @GetMapping("/login")
    @ResponseBody
    public String handleLogin(
        // 🟢 修改点 1：显式添加 name = "error"，解决编译参数名丢失报错
        @RequestParam(name = "error", required = false) final String error) {
        return OAuth2Page.of().handleLoginHtml(this.loginPage, error);
    }

    /**
     * 客户端默认回调
     *
     * @param registrationId 客户端注册 ID
     * @param code           授权码
     * @param state          状态参数   xxx_VC_{VERIFIER_CODE}
     * @param error          错误信息
     *
     * @return 处理结果
     */
    @GetMapping("/authorized/{registrationId}")
    public JObject handleCallback(@PathVariable("registrationId") final String registrationId,
                                  @RequestParam(name = "code", required = false) final String code,
                                  @RequestParam(name = "state", required = false) final String state,
                                  @RequestParam(name = "error", required = false) final String error) {
        // 这里可以处理授权回调逻辑
        log.info("[ R2MO ] OAuth2 授权回调：{}", registrationId);
        return OAuth2Page.of().handleToken(registrationId, code, state, error);
    }
}

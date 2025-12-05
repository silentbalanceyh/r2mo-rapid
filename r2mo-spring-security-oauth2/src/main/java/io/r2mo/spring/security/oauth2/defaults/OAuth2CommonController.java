package io.r2mo.spring.security.oauth2.defaults;

import io.r2mo.typed.json.JObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Objects;

/**
 * @author lang : 2025-11-28
 */
@Controller
@Slf4j
public class OAuth2CommonController {

    /**
     * 登录页跳转处理
     * <p>注意：这里返回 String 代表视图名称，Spring 会自动去 templates 找对应的 HTML</p>
     */
    @GetMapping("/login")
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
    @GetMapping("/oauth2/authorized/{registrationId}")
    public JObject handleCallback(@PathVariable("registrationId") final String registrationId,
                                  @RequestParam(name = "code", required = false) final String code,
                                  @RequestParam(name = "state", required = false) final String state,
                                  @RequestParam(name = "error", required = false) final String error) {
        // 这里可以处理授权回调逻辑
        log.info("[ R2MO ] OAuth2 授权回调：{}", registrationId);
        return OAuth2Page.of().handleToken(registrationId, code, state, error);
    }
}

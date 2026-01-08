package io.r2mo.spring.security.oauth2.defaults;

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
public class OAuth2ServerController {

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
}

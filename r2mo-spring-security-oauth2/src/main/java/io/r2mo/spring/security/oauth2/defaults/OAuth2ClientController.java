package io.r2mo.spring.security.oauth2.defaults;

import io.r2mo.typed.json.JObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
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
    public JObject handleCallback(@PathVariable("registrationId") final String registrationId,
                                  @RequestParam(name = "code", required = false) final String code,
                                  @RequestParam(name = "state", required = false) final String state,
                                  @RequestParam(name = "error", required = false) final String error) {
        // 这里可以处理授权回调逻辑
        log.info("[ R2MO ] OAuth2 授权回调：{}", registrationId);
        return OAuth2ClientAuthorized.of().handleToken(registrationId, code, state, error);
    }
}

package io.r2mo.spring.email;

import cn.hutool.core.util.StrUtil;
import io.r2mo.function.Fn;
import io.r2mo.spring.email.exception._80320Exception404EmailAccount;
import io.r2mo.spring.email.exception._80321Exception404EmailServer;
import io.r2mo.xync.email.EmailDomain;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class EmailConfiguration {

    @Autowired
    private EmailConfigServer configServer;

    @PostConstruct
    public void configured() {
        final String username = this.configServer.getUsername();
        final String password = this.configServer.getPassword();
        // 账号检查
        Fn.jvmKo(StrUtil.isEmpty(username) || StrUtil.isEmpty(password),
            _80320Exception404EmailAccount.class);

        final EmailDomain stmpServer = this.configServer.getSmtp();
        // SMTP 服务检查
        Fn.jvmKo(StrUtil.isEmpty(stmpServer.getHost()) || 0 >= stmpServer.getPort(),
            _80321Exception404EmailServer.class);
        log.info("[ R2MO ] ----> 已启用邮件服务模块！");
    }
}

package io.r2mo.spring.email;

import cn.hutool.core.util.StrUtil;
import io.r2mo.xync.email.EmailCredential;
import io.r2mo.xync.email.EmailDomain;
import io.r2mo.xync.email.EmailProtocol;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.io.Serializable;

/**
 * <pre>
 *     email:
 *       # 全局配置
 *       username:
 *       password:
 *       encoding:
 *       # 发送协议（必须认证，所以 auth = true ）
 *       smtp:
 *         host:
 *         port:
 *         ssl:
 *         timeout:
 *         username: "???"
 *         password: "???"
 *       # --------------------------------
 *       # 接受协议
 *       imap:
 *         host:
 *         port:
 *         ssl:
 *         folder: "INBOX"
 *       pop3:
 *         host:
 *         port:
 *         ssl:
 * </pre>
 *
 * @author lang : 2025-12-05
 */
@Configuration
@ConfigurationProperties(prefix = "email")
@Data
@EqualsAndHashCode(callSuper = false)
public class ConfigEmailServer implements Serializable {
    // 1. 全局默认配置
    private String username;
    private String password;
    private String encoding = "UTF-8";

    // 2. 各个协议的配置域
    private EmailDomain smtp = new EmailDomain(EmailProtocol.SMTP);
    private EmailDomain imap = new EmailDomain(EmailProtocol.IMAP);
    private EmailDomain pop3 = new EmailDomain(EmailProtocol.POP3);

    public EmailCredential getCredential() {
        final EmailCredential credential = new EmailCredential();
        credential.username(this.getUsername());
        credential.password(this.getPassword());
        return credential;
    }

    public EmailDomain getSender() {
        return this.smtp;
    }

    public EmailDomain getReceiver() {
        if (!StrUtil.isEmpty(this.pop3.getHost())) {
            return this.pop3;
        }
        return this.imap;
    }
}

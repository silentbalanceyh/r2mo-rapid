package io.r2mo.spring.email;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import io.r2mo.xync.email.EmailCredential;
import io.r2mo.xync.email.EmailDomain;
import io.r2mo.xync.email.EmailProtocol;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;

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
@SuppressWarnings("all")
public class ConfigEmailServer implements Serializable, EnvironmentAware, InitializingBean {
    // 1. 全局默认配置
    private String username;
    private String password;
    private String encoding = "UTF-8";

    // 2. 各个协议的配置域
    private EmailDomain smtp = new EmailDomain(EmailProtocol.SMTP);
    private EmailDomain imap = new EmailDomain(EmailProtocol.IMAP);
    private EmailDomain pop3 = new EmailDomain(EmailProtocol.POP3);
    // 暂存 Environment
    private Environment environment;

    @Override
    public void setEnvironment(final Environment environment) {
        this.environment = environment;
    }

    /**
     * 3. 在属性设置后，执行手动绑定逻辑
     */
    @Override
    public void afterPropertiesSet() {
        // 利用 Binder 读取 "email" 下的所有配置为 Map
        final Map<String, Object> rawProps = Binder.get(this.environment)
            .bind("email", Bindable.mapOf(String.class, Object.class))
            .orElse(Collections.emptyMap());

        if (MapUtil.isEmpty(rawProps)) {
            return;
        }

        // 3.1 绑定全局属性
        if (rawProps.containsKey("username")) {
            this.setUsername(Convert.toStr(rawProps.get("username")));
        }
        if (rawProps.containsKey("password")) {
            this.setPassword(Convert.toStr(rawProps.get("password")));
        }
        if (rawProps.containsKey("encoding")) {
            this.setEncoding(Convert.toStr(rawProps.get("encoding")));
        }

        // 3.2 绑定协议域 (调用 Domain 自己的 bind 方法)
        this.smtp.bind(MapUtil.get(rawProps, "smtp", Map.class));
        this.imap.bind(MapUtil.get(rawProps, "imap", Map.class));
        this.pop3.bind(MapUtil.get(rawProps, "pop3", Map.class));
    }

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

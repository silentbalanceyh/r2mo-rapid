package io.r2mo.spring.email;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.io.Serializable;

/**
 * <pre>
 *     email:
 *       ssl:
 *       account:
 *       password:
 *       smtp-host:
 *       smtp-port:
 *       pop3-host:
 *       pop3-port:
 * </pre>
 *
 * @author lang : 2025-12-05
 */
@Configuration
@ConfigurationProperties(prefix = "email")
@Data
public class EmailConfig implements Serializable {

    private String username;
    private String password;
    private boolean ssl = false;

    private String smtpHost;
    private Integer smtpPort;

    private String pop3Host;
    private Integer pop3Port;
}

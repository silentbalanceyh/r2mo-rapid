package io.r2mo.spring.security.ldap;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * <pre>
 *     spring:
 *       ldap:
 *         urls:
 *         base:
 *         username:
 *         password:
 *     security:
 *       ldap:
 *         enabled: true
 *         # 搜索用户的过滤器
 *         user-filter: (|(uid={0})(mail={0}))
 *         # 存储邮箱的属性名
 *         user-email: "mail"
 *         user-id: "uid"
 * </pre>
 *
 * @author lang : 2025-12-09
 */
@Configuration
@ConfigurationProperties(prefix = "security.ldap")
@Data
public class LdapConfig {
    private boolean enabled = false;
    private String userFilter = "(|(uid={0})(mail={0}))";
    private String userId = "uid";
    private String userEmail = "mail";
}

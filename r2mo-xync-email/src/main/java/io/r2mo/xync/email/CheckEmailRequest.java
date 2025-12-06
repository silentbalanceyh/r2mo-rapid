package io.r2mo.xync.email;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 邮箱连通性检查请求
 * 用于 exchange 方法，测试 SMTP 和 POP3 是否配置正确
 */
@Data
@Accessors(fluent = true, chain = true)
public class CheckEmailRequest {

    /** 是否检查 SMTP 发信能力 */
    private boolean smtp = true;

    /** 是否检查 POP3/IMAP 收信能力 */
    private boolean inbound = true;

    /** 收信协议: "pop3" 或 "imap" (默认 pop3) */
    private String protocol = "pop3";
}

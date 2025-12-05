package io.r2mo.xync.email.config;

import lombok.Data;

import java.io.Serializable;

/**
 * @author lang : 2025-12-05
 */
@Data
public class EmailServer implements Serializable {
    // 邮箱账号：一般是邮件格式
    private String user;
    // 邮箱密码
    private String password;
}

package io.r2mo.xync.email;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(fluent = true, chain = true)
public class CheckEmailResult {
    private boolean okSmtp;
    private boolean okInbound;
    private int messageCount; // 收件箱邮件数
    private String log; // 详细日志
}

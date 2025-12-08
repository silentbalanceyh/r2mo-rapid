package io.r2mo.xync.email;

import io.r2mo.base.exchange.UniAccount;
import io.r2mo.base.exchange.UniContext;
import io.r2mo.base.exchange.UniCredential;
import io.r2mo.base.exchange.UniMessage;
import io.r2mo.base.exchange.UniProvider;
import io.r2mo.typed.annotation.SPID;

/**
 * 核心 Provider 的执行流程，执行 Email 的处理逻辑
 *
 * @author lang : 2025-12-05
 */
@SPID("UNI_EMAIL")
public class EmailProvider implements UniProvider {
    @Override
    public String channel() {
        return "EMAIL";     // 邮件通道
    }

    @Override
    public Class<? extends UniCredential> credentialType() {
        return EmailCredential.class;
    }

    @Override
    public String send(final UniAccount account, final UniMessage<?> message, final UniContext context) {
        return "";
    }
}

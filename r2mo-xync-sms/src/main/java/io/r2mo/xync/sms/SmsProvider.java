package io.r2mo.xync.sms;

import io.r2mo.base.exchange.UniAccount;
import io.r2mo.base.exchange.UniContext;
import io.r2mo.base.exchange.UniCredential;
import io.r2mo.base.exchange.UniMessage;
import io.r2mo.base.exchange.UniProvider;
import io.r2mo.typed.annotation.SPID;

/**
 * @author lang : 2025-12-08
 */
@SPID("UNI_SMS")
public class SmsProvider implements UniProvider {
    @Override
    public String channel() {
        return "SMS";
    }

    @Override
    public Class<? extends UniCredential> credentialType() {
        return SmsCredential.class;
    }

    @Override
    public String send(final UniAccount account, final UniMessage<?> message, final UniContext context) {
        return UniProvider.super.send(account, message, context);
    }
}

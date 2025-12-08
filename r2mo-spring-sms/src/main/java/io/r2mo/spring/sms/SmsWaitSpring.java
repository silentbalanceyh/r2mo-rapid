package io.r2mo.spring.sms;

import io.r2mo.base.exchange.UniAccount;
import io.r2mo.base.exchange.UniContext;
import io.r2mo.base.exchange.UniMessage;
import io.r2mo.base.exchange.UniProvider;
import io.r2mo.typed.json.JObject;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * @author lang : 2025-12-08
 */
@Slf4j
public class SmsWaitSpring implements UniProvider.Wait<SmsConfig> {

    @Override
    public UniAccount account(final JObject params, final SmsConfig smsConfig) {
        return null;
    }

    @Override
    public UniContext context(final JObject params, final SmsConfig smsConfig, final boolean sendOr) {
        return null;
    }

    @Override
    public UniMessage<String> message(final JObject params, final Map<String, Object> header, final SmsConfig smsConfig) {
        return null;
    }
}

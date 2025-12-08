package io.r2mo.spring.sms;

import io.r2mo.base.exchange.UniProvider;
import io.r2mo.typed.cc.Cc;
import io.r2mo.typed.json.JObject;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * @author lang : 2025-12-08
 */
@Service
public class SmsClientImpl implements SmsClient {

    private static final Cc<String, UniProvider> CC_PROVIDER = Cc.openThread();

    @Override
    public JObject send(final String template, final JObject params, final Set<String> toSet) {
        return null;
    }
}

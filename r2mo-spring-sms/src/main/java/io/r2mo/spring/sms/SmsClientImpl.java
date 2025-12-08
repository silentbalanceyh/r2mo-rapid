package io.r2mo.spring.sms;

import io.r2mo.base.exchange.UniAccount;
import io.r2mo.base.exchange.UniContext;
import io.r2mo.base.exchange.UniMessage;
import io.r2mo.base.exchange.UniProvider;
import io.r2mo.spi.SPI;
import io.r2mo.typed.cc.Cc;
import io.r2mo.typed.json.JObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * @author lang : 2025-12-08
 */
@Service
public class SmsClientImpl implements SmsClient {

    private static final Cc<String, UniProvider> CC_PROVIDER = Cc.openThread();

    @Autowired
    private SmsConfig config;

    @Override
    public JObject send(final String template, final JObject params, final Set<String> toSet) {
        params.put("template", template);
        // 1. 根据配置发送邮件
        final UniProvider.Wait<SmsConfig> wait = UniProvider.waitFor(SmsWaitSpring::new);
        final UniAccount account = wait.account(params, this.config);
        final UniContext context = wait.context(params, this.config);

        // 2. 消息构造
        final UniMessage<String> message = wait.message(params, this.config);
        toSet.forEach(message::addTo);

        final UniProvider provider = CC_PROVIDER.pick(() -> SPI.findOne(UniProvider.class, "UNI_SMS"));
        final String result = provider.send(account, message, context);
        return UniProvider.replySuccess(result);
    }
}

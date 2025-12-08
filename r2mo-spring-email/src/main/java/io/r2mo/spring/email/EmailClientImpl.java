package io.r2mo.spring.email;

import io.r2mo.base.exchange.UniAccount;
import io.r2mo.base.exchange.UniContext;
import io.r2mo.base.exchange.UniMessage;
import io.r2mo.base.exchange.UniProvider;
import io.r2mo.base.web.ForTpl;
import io.r2mo.spi.SPI;
import io.r2mo.typed.cc.Cc;
import io.r2mo.typed.json.JObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * @author lang : 2025-12-05
 */
@Service
public class EmailClientImpl implements EmailClient {

    @Autowired
    private ForTpl thymeleafTpl;

    @Autowired
    private ConfigEmailServer serverConfig;

    private static final Cc<String, UniProvider> CC_PROVIDER = Cc.openThread();

    @Override
    public JObject send(final String template, final JObject params, final Set<String> toSet) {
        // 1. 根据模板提取内容
        final String contentHtml = this.thymeleafTpl.process(template, params);
        params.put("content", contentHtml);


        // 2. 根据配置发送邮件
        final UniProvider.Wait<ConfigEmailServer> wait = UniProvider.waitFor(EmailWaitSpring::new);
        final UniAccount account = wait.account(params, this.serverConfig);
        final UniContext context = wait.context(params, this.serverConfig, true);


        // 3. 消息构造（每次都构造新消息）
        final UniMessage<String> message = wait.message(params, this.serverConfig);
        toSet.forEach(message::addTo);

        final UniProvider provider = CC_PROVIDER.pick(() -> SPI.findOne(UniProvider.class, "UNI_EMAIL"));
        final String result = provider.send(account, message, context);


        // 4. 返回结果
        final JObject resultJ = SPI.J();
        resultJ.put("result", result);
        return resultJ;
    }
}

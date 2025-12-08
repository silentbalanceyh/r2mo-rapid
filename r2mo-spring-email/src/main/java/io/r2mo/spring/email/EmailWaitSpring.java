package io.r2mo.spring.email;

import cn.hutool.core.util.StrUtil;
import io.r2mo.base.exchange.NormMessage;
import io.r2mo.base.exchange.UniAccount;
import io.r2mo.base.exchange.UniContext;
import io.r2mo.base.exchange.UniMessage;
import io.r2mo.base.exchange.UniProvider;
import io.r2mo.base.util.R2MO;
import io.r2mo.function.Fn;
import io.r2mo.typed.cc.Cc;
import io.r2mo.typed.json.JObject;
import io.r2mo.xync.email.EmailAccount;
import io.r2mo.xync.email.EmailContext;
import io.r2mo.xync.email.EmailCredential;
import io.r2mo.xync.email.EmailDomain;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * 重点说明
 * <pre>
 *     {@see ConfigEmailCaptcha} 是配置验证码专用的配置，这个配置和 email 配置不绑定，并且是位于发送不同内容的限制专用的，
 *     简单说如果是发送验证码，这个配置是有效的，但如果是发送其他的内容，这个配置没有任何意义，所以这个配置是不适合放在此接口，
 *     此接口仅仅是为了邮件发送的 Spring 结构定制款。
 * </pre>
 *
 * @author lang : 2025-12-07
 */
@Slf4j
public class EmailWaitSpring implements UniProvider.Wait<EmailConfigServer> {
    // 发送者账号跟着配置走
    private static final Cc<String, UniAccount> CC_ACCOUNT = Cc.open();
    // 邮箱设置上下文跟着配置走
    private static final Cc<String, UniContext> CC_CONTEXT = Cc.open();

    @Override
    public UniAccount account(final JObject params, final EmailConfigServer emailConfigServer) {
        final EmailCredential credential = emailConfigServer.getCredential();
        if (Objects.isNull(credential)) {
            return null;
        }
        return CC_ACCOUNT.pick(() -> {
            // 构造基础的 UniAccount
            final EmailAccount account = new EmailAccount(emailConfigServer.getCredential());
            // 是否带有额外配置信息
            log.info("[ R2MO ] 构造邮件发送账号: {} / 签名：{}", account.getId(), account.signature());


            // 签名设置
            final String signature = R2MO.valueT(params, "signature");
            Fn.jvmAt(StrUtil.isEmpty(signature), () -> account.signature(signature));
            // 头像设置
            final String avatar = R2MO.valueT(params, "avatar");
            Fn.jvmAt(StrUtil.isEmpty(avatar), () -> account.setAvatar(avatar));
            // 姓名设置
            final String name = R2MO.valueT(params, "name");
            Fn.jvmAt(StrUtil.isEmpty(name), () -> account.setName(name));


            return account;
        }, String.valueOf(credential.hashCode()));
    }

    @Override
    public UniMessage<String> message(final JObject params, final Map<String, Object> headers,
                                      final EmailConfigServer emailConfigServer) {
        // 消息标识
        String id = R2MO.valueT(params, "id");
        if (StrUtil.isEmpty(id)) {
            id = UUID.randomUUID().toString();
        }
        log.info("[ R2MO ] 构造邮件消息 ID: {}", id);
        final NormMessage<String> message = new NormMessage<>(id);
        // 消息标题
        final String subject = R2MO.valueT(params, "subject");
        message.subject(subject);
        // 消息内容
        final String content = R2MO.valueT(params, "content");
        message.payload(content);
        // 消息头
        headers.forEach(message::header);
        return message;
    }

    @Override
    public UniContext context(final JObject params, final EmailConfigServer emailConfigServer,
                              final boolean sendOr) {
        if (sendOr) {
            final EmailDomain domainSender = emailConfigServer.getSender();

            this.buildAccount(params, emailConfigServer, domainSender);

            return this.buildContext(params, domainSender);
        } else {
            final EmailDomain domainReceiver = emailConfigServer.getReceiver();
            return this.buildContext(params, domainReceiver);
        }
    }

    /**
     * 优先级构造
     * <pre>
     *     1. 输入数据为第一优先级
     *     2. 配置域的账号密码为第二优先级
     *     3. 全局配置的账号密码为最后优先级
     * </pre>
     *
     * @param params       参数
     * @param configServer 全局配置
     * @param domain       配置域
     */
    private void buildAccount(final JObject params, final EmailConfigServer configServer,
                              final EmailDomain domain) {
        String username = R2MO.valueT(params, "username");
        if (StrUtil.isEmpty(username)) {
            username = StrUtil.isEmpty(domain.getUsername()) ?
                configServer.getUsername() : domain.getUsername();
        }
        domain.setUsername(username);
        String password = R2MO.valueT(params, "password");
        if (StrUtil.isEmpty(password)) {
            password = StrUtil.isEmpty(domain.getPassword()) ?
                configServer.getPassword() : domain.getPassword();
        }
        domain.setPassword(password);
    }

    private UniContext buildContext(final JObject params, final EmailDomain domain) {
        Objects.requireNonNull(domain);
        return CC_CONTEXT.pick(() -> {
            // 构造上下文 UniContext
            final EmailContext context = new EmailContext()
                .setHost(domain.getHost())
                .setPort(domain.getPort())
                .setSsl(domain.isSsl())
                .setProtocol(domain.getProtocol().name());
            // timeout 特殊属性
            int timeout = R2MO.valueT(params, UniContext.KEY_TIMEOUT, -1);
            // Fix Issue: Cannot invoke "java.lang.Integer.intValue()" because the return value of "getExtension(String)" is null
            if (timeout <= 0 && domain.hasExtension(UniContext.KEY_TIMEOUT)) {
                timeout = domain.getExtension(UniContext.KEY_TIMEOUT);
            }

            return context.setTimeout(timeout);
        }, domain.getProtocol() + "@" + domain.hashCode());
    }
}

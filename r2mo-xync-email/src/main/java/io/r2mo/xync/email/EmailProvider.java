package io.r2mo.xync.email;

import io.r2mo.base.exchange.UniAccount;
import io.r2mo.base.exchange.UniContext;
import io.r2mo.base.exchange.UniCredential;
import io.r2mo.base.exchange.UniMessage;
import io.r2mo.base.exchange.UniProvider;
import io.r2mo.typed.annotation.SPID;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Properties;

/**
 * 核心 Provider 的执行流程，执行 Email 的处理逻辑
 *
 * @author lang : 2025-12-05
 */
@SPID("UNI_EMAIL")
public class EmailProvider implements UniProvider {

    // --- Jakarta Mail Property Keys ---
    private static final String PROP_HOST = "mail.smtp.host";
    private static final String PROP_PORT = "mail.smtp.port";
    private static final String PROP_AUTH = "mail.smtp.auth";
    private static final String PROP_TIMEOUT = "mail.smtp.timeout";
    private static final String PROP_CONN_TIMEOUT = "mail.smtp.connectiontimeout";
    private static final String PROP_SSL_ENABLE = "mail.smtp.ssl.enable";
    private static final String PROP_SSL_FACTORY = "mail.smtp.socketFactory.class";
    private static final String PROP_STARTTLS_ENABLE = "mail.smtp.starttls.enable";

    // --- Property Values & Constants ---
    private static final String VAL_TRUE = "true";
    // JDK 原生 SSL 工厂类名，保留 javax 包名
    private static final String VAL_SSL_FACTORY_CLASS = "javax.net.ssl.SSLSocketFactory";
    private static final String CHARSET_UTF8 = "UTF-8";
    private static final String CONTENT_TYPE_HTML = "text/html;charset=UTF-8";
    private static final String RETURN_OK = "SENT_OK";

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
        // 1. 准备 SMTP 服务器配置
        final Properties props = new Properties();

        // 提取 Context 中的标准参数
        final String host = context.getHost();
        final Integer port = context.getPort();
        final boolean isSsl = context.getOrDefault(UniContext.KEY_SSL, false);
        final int timeout = context.getOrDefault(UniContext.KEY_TIMEOUT, 10000);
        final String timeoutStr = String.valueOf(timeout);

        // 填充 Jakarta Mail 属性
        props.put(PROP_HOST, host);
        props.put(PROP_PORT, port != null ? port : (isSsl ? 465 : 25));
        props.put(PROP_AUTH, VAL_TRUE);
        props.put(PROP_TIMEOUT, timeoutStr);
        props.put(PROP_CONN_TIMEOUT, timeoutStr);

        if (isSsl) {
            props.put(PROP_SSL_ENABLE, VAL_TRUE);
            props.put(PROP_SSL_FACTORY, VAL_SSL_FACTORY_CLASS);
        } else {
            // 即使不开启 SSL，通常也建议开启 STARTTLS
            props.put(PROP_STARTTLS_ENABLE, VAL_TRUE);
        }

        // 2. 准备认证信息 (从 UniAccount 获取)
        if (!(account.credential() instanceof final EmailCredential cred)) {
            throw new IllegalArgumentException("[ R2MO ] 凭证类型错误：必须是 EmailCredential 类型");
        }

        // 创建会话
        final Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(cred.username(), cred.password());
            }
        });

        // 3. 构建邮件消息
        try {
            final MimeMessage mimeMessage = new MimeMessage(session);

            // 3.1 设置发件人 (From)
            final String fromName = account.getName();
            if (fromName != null && !fromName.isEmpty()) {
                mimeMessage.setFrom(new InternetAddress(account.signature(), fromName, CHARSET_UTF8));
            } else {
                mimeMessage.setFrom(new InternetAddress(account.signature()));
            }

            // 3.2 设置收件人 (To)
            for (final String to : message.to()) {
                mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            }

            // 3.3 设置主题 (Subject)
            mimeMessage.setSubject(message.subject(), CHARSET_UTF8);

            // 3.4 设置发送日期
            mimeMessage.setSentDate(new Date());

            // 3.5 设置正文 (Payload)
            final Object payload = message.payload();
            final String content = payload != null ? payload.toString() : "";
            mimeMessage.setContent(content, CONTENT_TYPE_HTML);

            // 3.6 处理扩展头 (Header)
            if (message.header() != null) {
                message.header().forEach((k, v) -> {
                    try {
                        mimeMessage.setHeader(k, v.toString());
                    } catch (final MessagingException e) {
                        // ignore or log
                    }
                });
            }

            // 4. 发送
            Transport.send(mimeMessage);

            // 5. 返回结果
            return mimeMessage.getMessageID() != null ? mimeMessage.getMessageID() : RETURN_OK;
        } catch (final MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException("[ R2MO ] 邮件发送失败：" + e.getMessage(), e);
        }
    }
}
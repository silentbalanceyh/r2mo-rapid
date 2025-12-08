package io.r2mo.xync.email;

import io.r2mo.base.exchange.BaseContext;

/**
 * Email 发送的上下文参数信息，上下文参数中不带账号和密码，发送过程中会单独进行构造，也是方便进行多账号测试
 * <pre>
 *     1. 一个 EmailDomain 可能会构造一个基础上下文信息
 *     2. 上下文信息会包含“发送”和“接收”两种基础信息
 *        - 发送协议 smtp
 *        - 接收协议 pop3 / imap（二选一）
 *     3. 账号、邮件内容、上下文 三者相互独立
 * </pre>
 *
 * @author lang : 2025-12-05
 */
public class EmailContext extends BaseContext {

    public EmailContext setProtocol(final String protocol) {
        this.set(KEY_PROTOCOL, protocol);
        return this;
    }

    public EmailContext setSsl(final boolean ssl) {
        this.set(KEY_SSL, ssl);
        return this;
    }

    public EmailContext setTimeout(final int timeout) {
        this.set(KEY_TIMEOUT, timeout);
        return this;
    }

    public EmailContext setHost(final String host) {
        this.set(KEY_HOST, host);
        return this;
    }

    public EmailContext setPort(final int port) {
        this.set(KEY_PORT, port);
        return this;
    }
}

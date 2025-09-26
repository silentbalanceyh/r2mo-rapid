package io.r2mo.typed.exception;

import io.r2mo.typed.service.ActCommune;
import io.r2mo.typed.webflow.WebState;

import java.util.Objects;

/**
 * 远程服务调用异常基类 🌐
 *
 * <p>继承自 {@link WebException}，用于表示跨服务调用过程中的异常。</p>
 *
 * <p>特性：
 * <ul>
 *     <li>📡 绑定 {@link ActCommune} 对象，记录远程调用的上下文信息</li>
 *     <li>🌍 支持国际化消息（messageKey + messageArgs）</li>
 *     <li>📝 支持直接消息模式（messageContent）</li>
 *     <li>⚡ 支持异常包装，便于传播底层调用错误</li>
 * </ul>
 *
 * ✅ 推荐作为微服务 / RPC / HTTP 调用相关异常的统一基类。
 *
 * 继承关系：
 * <pre>
 * AbstractException
 *     └─ WebException 🌐
 *          └─ RemoteException 📡
 * </pre>
 *
 * @author lang
 * @since 2025-09-03
 */
public abstract class RemoteException extends WebException {

    /**
     * 通信上下文 📡
     * <p>通过 {@link ActCommune} 记录服务调用的元信息，例如服务名称、调用者、接受者等。</p>
     */
    protected ActCommune message;

    /**
     * 构造函数一：国际化消息模式 🌍
     *
     * <p>通过 {@code messageKey} + {@code messageArgs} 获取国际化消息，
     * 并基于 {@link WebState} 初始化通信上下文 {@link ActCommune}。</p>
     *
     * ✅ 推荐使用场景：
     * - 远程服务调用失败，需返回多语言提示
     *
     * @param status      Web 流程状态
     * @param messageKey  国际化消息键
     * @param messageArgs 国际化消息参数
     */
    protected RemoteException(final WebState status, final String messageKey, final Object... messageArgs) {
        super(status, messageKey, messageArgs);
        this.message = ActCommune.of(status);
    }

    /**
     * 构造函数二：直接消息模式 📝
     *
     * <p>使用 {@code messageContent} 作为异常消息，不经过国际化处理，
     * 同时绑定 {@link ActCommune} 通信上下文。</p>
     *
     * ✅ 推荐使用场景：
     * - 直接返回远程调用错误信息
     *
     * @param status         Web 流程状态
     * @param messageContent 已格式化的异常消息
     */
    protected RemoteException(final WebState status, final String messageContent) {
        super(status, messageContent);
        this.message = ActCommune.of(status);
    }

    /**
     * 构造函数三：异常包装模式 🔗
     *
     * <p>将底层 {@link Throwable} 包装为远程调用异常，
     * 并基于 {@link WebState} 初始化通信上下文。</p>
     *
     * ✅ 推荐使用场景：
     * - 捕获远程调用底层异常并统一封装
     * - 保留底层异常堆栈，方便追踪
     *
     * @param status Web 流程状态
     * @param ex     底层异常
     */
    protected RemoteException(final WebState status, final Throwable ex) {
        super(status, ex);
        this.message = ActCommune.of(status);
    }

    /**
     * 获取服务名称 🏷️
     *
     * <p>通过 {@link ActCommune#ofAcceptor()} 返回当前异常对应的服务名称，
     * 即远程调用过程中的接收方。</p>
     *
     * @return 服务名称（若 message 为空则返回 null）
     */
    public String service() {
        if (Objects.isNull(this.message)) {
            return null;
        }
        // Acceptor 就是服务通信过程中的接受者，即服务名称
        return this.message.ofAcceptor();
    }
}

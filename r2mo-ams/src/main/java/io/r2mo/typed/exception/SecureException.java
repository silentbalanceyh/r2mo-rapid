package io.r2mo.typed.exception;

import io.r2mo.typed.enums.SecurityScope;
import io.r2mo.typed.webflow.WebState;

/**
 * 安全相关异常抽象基类 🔒
 *
 * <p>继承自 {@link WebException}，用于表示 Web 层中涉及安全控制的异常。</p>
 *
 * <p>特性：
 * <ul>
 *     <li>🔑 绑定安全作用域 {@link SecurityScope}，支持细粒度权限控制</li>
 *     <li>🌍 支持国际化消息（通过 messageKey + messageArgs）</li>
 *     <li>📝 支持直接文本消息模式</li>
 *     <li>⚡ 支持包装底层异常</li>
 * </ul>
 *
 * ✅ 推荐作为安全/认证/授权相关异常的统一基类。
 *
 * 继承关系：
 * <pre>
 * AbstractException
 *     └─ WebException 🌐
 *          └─ SecureException 🔒
 * </pre>
 *
 * @author lang
 * @since 2025-09-03
 */
public abstract class SecureException extends WebException {

    /**
     * 安全作用域 🔑
     * <p>默认值为 {@link SecurityScope#ALL}，表示全局作用域。</p>
     */
    protected SecurityScope scope = SecurityScope.ALL;

    /**
     * 构造函数一：国际化消息模式 🌍
     *
     * <p>通过 {@code messageKey} + {@code messageArgs} 获取国际化异常消息，
     * 并绑定 {@link WebState}。</p>
     *
     * ✅ 推荐使用场景：
     * - 国际化安全错误提示
     * - 认证/授权失败时的多语言反馈
     *
     * @param status      Web 流程状态
     * @param messageKey  国际化消息键
     * @param messageArgs 国际化消息参数
     */
    protected SecureException(final WebState status, final String messageKey, final Object... messageArgs) {
        super(status, messageKey, messageArgs);
    }

    /**
     * 构造函数二：直接消息模式 📝
     *
     * <p>使用 {@code messageContent} 作为异常内容，不经过国际化处理。</p>
     *
     * ✅ 推荐使用场景：
     * - 内部安全检查失败
     * - 无需国际化的本地错误消息
     *
     * @param status         Web 流程状态
     * @param messageContent 已格式化的异常消息
     */
    protected SecureException(final WebState status, final String messageContent) {
        super(status, messageContent);
    }

    /**
     * 构造函数三：异常包装模式 🔗
     *
     * <p>用于将底层 {@link Throwable} 包装为安全异常，
     * 并绑定对应的 {@link WebState}。</p>
     *
     * ✅ 推荐使用场景：
     * - 捕获底层认证/授权模块的异常并统一封装
     * - 保留原始异常堆栈，方便排查
     *
     * @param status Web 流程状态
     * @param ex     底层异常
     */
    protected SecureException(final WebState status, final Throwable ex) {
        super(status, ex);
    }

    /**
     * 获取安全作用域 🔑
     *
     * <p>用于判断异常所属的安全控制范围（如：ALL / USER / ADMIN）。</p>
     *
     * @return 安全作用域
     */
    public SecurityScope scope() {
        return this.scope;
    }
}

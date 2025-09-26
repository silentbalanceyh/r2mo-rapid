package io.r2mo.typed.exception;

import io.r2mo.typed.webflow.WebState;
import lombok.Getter;

/**
 * Web 层通用异常抽象基类 🌐
 *
 * <p>继承自 {@link AbstractException}，主要用于描述 Web 流程中的异常情况。</p>
 *
 * <p>特性：
 * <ul>
 *     <li>🔗 绑定 {@link WebState}，区分业务/流程状态</li>
 *     <li>🌍 支持国际化异常消息（messageKey + messageArgs）</li>
 *     <li>📝 支持直接消息文本传入</li>
 *     <li>⚡ 支持封装底层异常 {@link Throwable}</li>
 * </ul>
 *
 * ✅ 推荐作为 Web 层统一异常基类，供子类扩展（如 `JvmException`）。
 *
 * @author lang
 * @since 2025-08-28
 */
@Getter
public abstract class WebException extends AbstractException {

    /**
     * Web 层状态标识 🌐
     * <p>通常与业务流程或 HTTP 状态码相关联。</p>
     */
    private final WebState status;

    /**
     * 构造函数一：国际化消息模式 🌍
     *
     * <p>通过 {@code messageKey} + {@code messageArgs} 获取国际化异常消息，
     * 并附加 {@link WebState}。</p>
     *
     * ✅ 推荐使用场景：
     * - 多语言业务异常
     * - 与资源文件绑定的错误码/消息
     *
     * @param status      业务/流程状态
     * @param messageKey  国际化消息键
     * @param messageArgs 国际化消息参数
     */
    protected WebException(final WebState status, final String messageKey, final Object... messageArgs) {
        super(messageKey, messageArgs);
        this.status = status;
    }

    /**
     * 构造函数二：直接消息模式 📝
     *
     * <p>使用 {@code messageContent} 作为异常内容，不经过国际化处理。</p>
     *
     * ✅ 推荐使用场景：
     * - 临时调试信息
     * - 内部开发/测试阶段快速抛错
     *
     * @param status         业务/流程状态
     * @param messageContent 已格式化的异常消息
     */
    protected WebException(final WebState status, final String messageContent) {
        super(messageContent);
        this.status = status;
    }

    /**
     * 构造函数三：异常包装模式 🔗
     *
     * <p>用于将底层 {@link Throwable} 包装为 {@link WebException}，
     * 并绑定对应的 {@link WebState}。</p>
     *
     * ✅ 推荐使用场景：
     * - 捕获第三方库或底层异常时，统一抛出 Web 层异常
     * - 保留原始堆栈信息，方便排查问题
     *
     * @param status 业务/流程状态
     * @param ex     底层异常
     */
    protected WebException(final WebState status, final Throwable ex) {
        super(ex);
        this.status = status;
    }
}

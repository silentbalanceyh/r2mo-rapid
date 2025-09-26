package io.r2mo.typed.exception;

import io.r2mo.typed.webflow.WebState;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Objects;

/**
 * JVM 层异常抽象基类 ⚙️
 *
 * <p>继承自 {@link WebException}，用于处理 JVM 层运行时异常，支持：
 * <ul>
 *     <li>🌐 国际化消息解析（通过 messageKey + messageArgs）</li>
 *     <li>📝 直接消息内容传入</li>
 *     <li>🔗 包装底层异常（cause）</li>
 * </ul>
 *
 * <p>统一扩展 {@link #getMessage()} 方法，附加底层 JVM 异常信息，便于定位问题。</p>
 *
 * @author lang
 * @since 2025-09-26
 */
@Accessors(fluent = true)
public abstract class JvmException extends WebException {

    /**
     * 底层触发的异常引用 🔗
     * <p>用于在 {@link #getMessage()} 中追加异常信息，便于问题追踪。</p>
     */
    @Getter
    @Setter
    private Throwable cause;

    /**
     * 构造函数一：基于状态与底层异常的构造函数 🔗
     *
     * <p>将已有的 {@link Throwable} 包装为 {@link JvmException}，同时附加 {@link WebState}。</p>
     *
     * ✅ 推荐使用场景：
     * - 🧩 封装第三方库异常
     * - 🪝 向上抛出并保留原始堆栈信息
     *
     * @param status 状态枚举（业务或系统状态）
     * @param ex     被包装的底层异常
     */
    protected JvmException(final WebState status, final Throwable ex) {
        super(status, ex);
        this.cause = ex;
    }

    /**
     * 构造函数二：基于状态与国际化消息键的构造函数 🌐
     *
     * <p>通过 {@code messageKey} 与 {@code messageArgs} 构造国际化异常消息。</p>
     *
     * ✅ 推荐使用场景：
     * - 🌍 业务异常，需要多语言支持
     * - 🏷️ 异常码与国际化资源文件绑定
     *
     * @param status      状态枚举
     * @param messageKey  国际化消息键
     * @param messageArgs 消息模板参数
     */
    protected JvmException(final WebState status, final String messageKey, final Object... messageArgs) {
        super(status, messageKey, messageArgs);
        this.cause = null;
    }

    /**
     * 构造函数三：基于状态与原始消息内容的构造函数 📝
     *
     * <p>直接使用传入的 {@code messageContent} 作为异常内容，不经过国际化处理。</p>
     *
     * ✅ 推荐使用场景：
     * - 🚧 临时错误提示或调试
     * - 🛠️ 内部工具类快速构造异常
     *
     * @param status         状态枚举
     * @param messageContent 已经格式化好的消息文本
     */
    protected JvmException(final WebState status, final String messageContent) {
        super(status, messageContent);
        this.cause = null;
    }

    /**
     * 获取异常消息 📨
     *
     * <p>如果 {@link #cause} 不为空，则在 {@code super.getMessage()} 后追加底层异常的消息。</p>
     *
     * @return 格式化后的完整异常消息
     */
    @Override
    public String getMessage() {
        if (Objects.isNull(this.cause)) {
            return super.getMessage();
        }
        return super.getMessage() + " / Jvm : " + this.cause.getMessage();
    }
}

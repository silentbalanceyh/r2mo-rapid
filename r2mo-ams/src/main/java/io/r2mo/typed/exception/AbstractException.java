package io.r2mo.typed.exception;

import io.r2mo.base.web.ForLocale;
import io.r2mo.spi.SPI;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author lang : 2025-08-28
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public abstract class AbstractException extends RuntimeException {

    private Object[] messageArgs;

    private String messageContent;

    /**
     * 国际化消息构造函数 🌐
     *
     * <p>
     * 根据 {@code messageKey} 的不同格式，采用不同的国际化解析逻辑：
     * </p>
     *
     * 🔹 **情况 1：模板模式**
     * - 当 {@code messageKey} 中包含占位符（如 <code>{}</code> 或 <code>{0}</code>）时，
     * 且不符合 <code>Exxxxx</code> 格式 👉 会被当作 **普通国际化 Key** 处理。
     * 使用 {@link ForLocale#formatInfo(String, Object...)} 方法解析并填充 {@code messageArgs}。
     *
     * 🔹 **情况 2：异常码模式**
     * - 当 {@code messageKey} 符合 <code>^E\\d+$</code> 正则（例如 <code>E11002</code>）时，
     * 👉 视为 **异常码**，会直接使用 {@link #getCode()} 去加载对应国际化资源文件中的 message 部分。
     *
     * ⚠️ 注意：最终解析结果都会写入 {@code messageContent}，供异常消息输出时使用。
     *
     * @param messageKey  国际化消息键
     *                    - 模板模式：普通 Key（可能包含 <code>{}</code>/<code>{0}</code> 占位符）
     *                    - 异常码模式：形如 <code>Exxxxx</code> 的错误码
     * @param messageArgs 国际化消息模板的参数（用于填充占位符）
     */
    public AbstractException(final String messageKey, final Object... messageArgs) {
        this.messageArgs = messageArgs;

        final ForLocale localization = SPI.SPI_WEB.ofLocale();
        /*
         * 此处不使用格式的模式处理，从 String 输入的角度考虑，不直接使用 code 来执行相关解析
         * 中间会存在推断的情况，保证整数模式下直接可解析 code 调用底层的 Message
         */
        if (messageKey != null && messageKey.matches("^[+-][1-9]\\d*$")) {
            // ✅ 异常码模式（如 E11002） -> 使用 getCode()
            final int messageCode = Integer.parseInt(messageKey);
            this.messageContent = localization.formatFail(messageCode, messageArgs);
        } else {
            // ✅ 模板模式（如 FAIL_ORDER_NOT_FOUND, "Order {} not found"）
            this.messageContent = localization.formatInfo(messageKey, messageArgs);
        }
    }


    /**
     * 基于已有消息内容的构造函数 📝
     *
     * <p>直接使用传入的 {@code messageContent} 作为异常的消息体，不经过国际化处理。</p>
     *
     * ✅ 推荐使用场景：
     * - 🚧 快速调试或临时错误提示
     * - 🛠️ 内部工具类或框架直接生成错误信息
     *
     * @param messageContent 已经格式化好的消息文本
     */
    public AbstractException(final String messageContent) {
        this.messageContent = messageContent;
    }

    /**
     * 基于底层异常的构造函数 🔗
     *
     * <p>包装一个已有的 {@link Throwable}，并将其 message
     * 作为当前异常的消息内容，便于异常链路追踪。</p>
     *
     * ✅ 推荐使用场景：
     * - 🪝 需要向上抛出并保留原始异常堆栈
     * - 🧩 封装第三方库抛出的异常
     *
     * @param ex 被包装的底层异常
     */
    public AbstractException(final Throwable ex) {
        super(ex);
        this.messageContent = ex.getMessage();
    }

    @Override
    public String getMessage() {
        return "[ R2MO" + this.getCode() + " ] " + this.messageContent;
    }

    public abstract int getCode();
}

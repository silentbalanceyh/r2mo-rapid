package io.r2mo.typed.exception;

import cn.hutool.core.util.StrUtil;
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

    private String messageKey;

    private Object[] messageArgs;

    private String messageContent;

    public AbstractException(final String messageKey, final Object... messageArgs) {
        this.messageKey = messageKey;
        this.messageArgs = messageArgs;
        if (StrUtil.isNotEmpty(messageKey)) {
            final ForLocale localization = SPI.SPI_WEB.ofLocale();
            this.messageContent = localization.format(messageKey, messageArgs);
        }
    }

    public AbstractException(final String messageContent) {
        this.messageContent = messageContent;
    }

    public AbstractException(final Throwable ex) {
        super(ex);
        this.messageContent = ex.getMessage();
    }

    @Override
    public String getMessage() {
        return "[ R2MO-ERR-" + this.getCode() + " ] " + this.messageContent;
    }

    public abstract int getCode();
}

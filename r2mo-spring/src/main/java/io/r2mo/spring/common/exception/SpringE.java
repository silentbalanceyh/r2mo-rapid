package io.r2mo.spring.common.exception;

import io.r2mo.spi.SPI;
import io.r2mo.typed.cc.Cc;
import io.r2mo.typed.webflow.WebState;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.http.HttpStatus;

/**
 * @author lang : 2025-09-11
 */
@Data
@Accessors(fluent = true)
public class SpringE {
    private final static Cc<Integer, SpringE> CC_ERROR = Cc.open();
    private final String message;
    private final int code;
    @Setter(AccessLevel.NONE)
    private WebState state;

    private SpringE(final int code, final String message) {
        this.code = code;
        this.message = message;
    }

    public static SpringE of(final int code, final String message) {
        return CC_ERROR.pick(() -> new SpringE(code, message), code);
    }

    public static SpringE of(final int code) {
        return CC_ERROR.pick(() -> new SpringE(code, "E" + Math.abs(code)), code);
    }

    public SpringE state(final HttpStatus status) {
        this.state = SPI.V_STATUS.fail(status);
        return this;
    }
}

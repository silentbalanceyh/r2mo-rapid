package io.r2mo.vertx.common.exception;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.r2mo.spi.SPI;
import io.r2mo.typed.cc.Cc;
import io.r2mo.typed.webflow.WebState;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author lang : 2025-09-25
 */
@Data
@Accessors(fluent = true)
public class VertxE {
    private final static Cc<Integer, VertxE> CC_ERROR = Cc.open();
    private final String message;
    private final int code;
    @Setter(AccessLevel.NONE)
    private WebState state;

    private VertxE(final int code, final String message) {
        this.code = code;
        this.message = message;
    }

    public static VertxE of(final int code, final String message) {
        return CC_ERROR.pick(() -> new VertxE(code, message), code);
    }

    public static VertxE of(final int code) {
        return CC_ERROR.pick(() -> new VertxE(code, "E" + Math.abs(code)), code);
    }

    public VertxE state(final HttpResponseStatus status) {
        this.state = SPI.V_STATUS.fail(status);
        return this;
    }
}

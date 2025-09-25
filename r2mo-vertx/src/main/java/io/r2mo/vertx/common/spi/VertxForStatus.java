package io.r2mo.vertx.common.spi;

import io.r2mo.base.web.ForStatus;
import io.r2mo.typed.cc.Cc;
import io.r2mo.typed.webflow.WebState;
import io.r2mo.vertx.common.enums.HttpStatus;

/**
 * @author lang : 2025-09-25
 */
public class VertxForStatus implements ForStatus {
    /**
     * 所有固定状态只生成一次，不做多余的对象创建流程
     */
    private static final Cc<Integer, WebState> CC_STATE = Cc.open();

    @Override
    public <T> WebState fail(final T status) {
        if (status instanceof final HttpStatus httpStatus) {
            return this.vStatus(httpStatus);
        }
        return this.V500();
    }

    @Override
    public <T> WebState ok(final T status) {
        if (status instanceof final HttpStatus httpStatus) {
            return this.vStatus(httpStatus);
        }
        return this.ok();
    }

    @Override
    public WebState ok() {
        return this.vStatus(HttpStatus.OK);
    }

    @Override
    public WebState ok204() {
        return this.vStatus(HttpStatus.NO_CONTENT);
    }

    @Override
    public WebState ok201() {
        return this.vStatus(HttpStatus.CREATED);
    }

    @Override
    public WebState V501() {
        return this.vStatus(HttpStatus.NOT_IMPLEMENTED);
    }

    @Override
    public WebState V500() {
        return this.vStatus(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public WebState V400() {
        return this.vStatus(HttpStatus.BAD_REQUEST);
    }

    @Override
    public WebState V401() {
        return this.vStatus(HttpStatus.UNAUTHORIZED);
    }

    @Override
    public WebState V403() {
        return this.vStatus(HttpStatus.FORBIDDEN);
    }

    @Override
    public WebState V404() {
        return this.vStatus(HttpStatus.NOT_FOUND);
    }

    @Override
    public WebState V405() {
        return this.vStatus(HttpStatus.METHOD_NOT_ALLOWED);
    }

    @Override
    public WebState V415() {
        return this.vStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    private WebState vStatus(final HttpStatus status) {
        return CC_STATE.pick(() -> new VertxHttpState(status), status.code());
    }
}

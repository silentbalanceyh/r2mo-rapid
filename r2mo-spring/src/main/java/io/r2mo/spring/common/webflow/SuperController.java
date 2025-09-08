package io.r2mo.spring.common.webflow;

import io.r2mo.SourceReflect;
import io.r2mo.spi.SPI;
import io.r2mo.typed.exception.AbstractException;
import io.r2mo.typed.service.ActOperation;
import io.r2mo.typed.service.ActResponse;
import io.r2mo.typed.service.ActState;
import io.r2mo.typed.webflow.R;
import io.r2mo.typed.webflow.WebResponse;
import io.r2mo.typed.webflow.WebState;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * 普通编程专用 Controller
 *
 * @author lang : 2025-09-08
 */
@Slf4j
public abstract class SuperController<T> {

    protected final Class<T> entityCls;

    public SuperController() {
        this.entityCls = SourceReflect.classT0(this.getClass());
    }

    protected abstract ActOperation<T> service();

    protected <RESP extends WebResponse<T>> R<RESP> replySuccess(
        final ActResponse<T> actionResult, final Supplier<RESP> supplier) {
        final ActState state = actionResult.state();
        // 无数据所有模式比较特殊
        if (ActState.SUCCESS_204_NO_DATA == state) {
            return R.ok();
        }

        // 带有异常
        final AbstractException error = actionResult.error();
        if (Objects.nonNull(error)) {
            return R.failure(error);
        }

        // 成功返回
        final WebState webState = SuperVector.webState(state);
        final RESP response = supplier.get();
        response.data(actionResult.data());
        return R.ok(response, webState);
    }

    protected R<T> replySuccess(final ActResponse<T> actionResult) {
        final ActState state = actionResult.state();
        // 无数据所有模式比较特殊
        if (ActState.SUCCESS_204_NO_DATA == state) {
            return R.ok();
        }
        final WebState webState = SuperVector.webState(state);
        return R.ok(actionResult.data(), webState);
    }

    protected R<Boolean> replySuccessOr(final ActResponse<Boolean> actionResult) {
        final ActState state = actionResult.state();
        // 无数据所有模式比较特殊
        if (ActState.SUCCESS_204_NO_DATA == state) {
            return R.ok(false, SPI.V_STATUS.ok204());
        }
        final WebState webState = SuperVector.webState(state);
        return R.ok(true, webState);
    }
}

package io.r2mo.spring.common.spi;

import io.r2mo.spi.FactoryWeb;
import io.r2mo.typed.cc.Cc;
import io.r2mo.typed.process.WebState;
import org.springframework.http.HttpStatus;

import java.text.MessageFormat;
import java.util.Objects;

/**
 * @author lang : 2025-09-02
 */
public class SpringFactoryWeb implements FactoryWeb {

    private static final Cc<Integer, WebState> CC_STATE = Cc.open();

    @Override
    public <T> WebState ofFailure(final T status) {
        if (Objects.isNull(status) || !(status instanceof HttpStatus)) {
            return this.ofFail500();
        }
        final int keyCached = status.hashCode();
        return CC_STATE.pick(() -> new SpringWebState((HttpStatus) status), keyCached);
    }

    @Override
    public WebState ofFail501() {
        return CC_STATE.pick(() -> new SpringWebState(HttpStatus.NOT_IMPLEMENTED), 501);
    }

    @Override
    public WebState ofFail500() {
        return CC_STATE.pick(() -> new SpringWebState(HttpStatus.INTERNAL_SERVER_ERROR), 500);
    }

    @Override
    public WebState ofFail400() {
        return CC_STATE.pick(() -> new SpringWebState(HttpStatus.BAD_REQUEST), 400);
    }

    @Override
    public WebState ofFail401() {
        return CC_STATE.pick(() -> new SpringWebState(HttpStatus.UNAUTHORIZED), 401);
    }

    @Override
    public WebState ofFail403() {
        return CC_STATE.pick(() -> new SpringWebState(HttpStatus.FORBIDDEN), 403);
    }

    @Override
    public <T> WebState ofSuccess(final T status) {
        if (Objects.isNull(status) || !(status instanceof HttpStatus)) {
            return this.ofSuccess();
        }
        return CC_STATE.pick(() -> new SpringWebState((HttpStatus) status), 200);
    }

    @Override
    public WebState ofSuccess() {
        return CC_STATE.pick(() -> new SpringWebState(HttpStatus.OK), 200);
    }

    @Override
    public WebState ofSuccess204() {
        return CC_STATE.pick(() -> new SpringWebState(HttpStatus.NO_CONTENT), 204);
    }

    @Override
    public String ofMessage(final String messageKey, final Object... messageArgs) {
        return MessageFormat.format(messageKey, messageArgs);
    }
}

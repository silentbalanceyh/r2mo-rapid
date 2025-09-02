package io.r2mo.spring.common.spi;

import io.r2mo.typed.process.WebState;
import org.springframework.http.HttpStatus;

/**
 * @author lang : 2025-09-02
 */
class SpringWebState implements WebState {

    private final HttpStatus status;

    SpringWebState(final HttpStatus status) {
        this.status = status;
    }

    @Override
    public String name() {
        return this.status.name();
    }

    @Override
    public int state() {
        return this.status.value();
    }

    @Override
    @SuppressWarnings("unchecked")
    public HttpStatus value() {
        return this.status;
    }
}

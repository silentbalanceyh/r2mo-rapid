package io.r2mo.spi;

import io.r2mo.typed.process.WebState;

/**
 * @author lang : 2025-08-28
 */
public interface FactoryWeb {

    <T> WebState ofFailure(T status);

    WebState ofFail501();

    WebState ofFail500();

    WebState ofFail400();

    WebState ofFail401();

    WebState ofFail403();

    <T> WebState ofSuccess(T status);

    WebState ofSuccess();

    WebState ofSuccess204();

    String ofMessage(String messageKey, Object... messageArgs);
}

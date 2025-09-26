package io.r2mo.function;

import io.r2mo.SourceReflect;
import io.r2mo.typed.exception.AbstractException;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lang : 2025-09-26
 */
@Slf4j
class FnOut {

    static <T extends AbstractException> void jvmKo(
        final boolean isKo, final Class<T> classE, final Object... args) {
        if (!isKo) {
            return;
        }
        final T error = SourceReflect.instance(classE, args);
        log.error("[ R2MO ] 异常：{} / message = {}", error.getClass().getSimpleName(),
            error.getMessage());
        throw error;
    }
}

package io.r2mo.function;

import io.r2mo.typed.exception.AbstractException;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * @author lang : 2025-09-26
 */
@Slf4j
@SuppressWarnings("all")
class FnJvm {
    static void jvmAt(final Actuator actuator) {
        jvmOr(input -> {
            actuator.exec();
            return null;
        }, null);
    }

    static <T> void jvmAt(final Consumer<T> consumer) {
        Fn.<T, Object>jvmOr(input -> {
            consumer.accept(input);
            return null;
        }, null);
    }

    static <T, R> R jvmOr(final Function<T, R> function, final R defaultValue) {
        try {
            final R ret = function.apply(null);
            return Objects.isNull(ret) ? defaultValue : ret;
        } catch (final AbstractException ex) {
            log.error("[ R2MO ] Fn.jvmAt 基本抽象异常: ", ex);
            // 自定义异常
            throw ex;
        } catch (final Throwable ex) {
            // 环境变量开启时打印异常堆栈
            log.error("[ R2MO ] Fn.jvmAt JVM异常: ", ex);
            return defaultValue;
        }
    }

    static <T> T jvmOr(final Supplier<T> supplier, final T defaultValue) {
        return Fn.<T, T>jvmOr(input -> supplier.get(), defaultValue);
    }
}

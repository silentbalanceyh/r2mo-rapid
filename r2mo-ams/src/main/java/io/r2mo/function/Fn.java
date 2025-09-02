package io.r2mo.function;

import io.r2mo.typed.exception.AbstractException;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * @author lang : 2025-09-02
 */
@Slf4j
public class Fn {

    public static void jvmAt(final Actuator actuator) {
        jvmOr(input -> {
            actuator.exec();
            return null;
        }, null);
    }

    public static <T> void jvmAt(final Consumer<T> consumer) {
        Fn.<T, Object>jvmOr(input -> {
            consumer.accept(input);
            return null;
        }, null);
    }

    public static <T> Boolean jvmIf(final Predicate<T> predicate) {
        return Fn.<T, Boolean>jvmOr(predicate::test, null);
    }

    public static <T> T jvmOr(final Supplier<T> supplier) {
        return Fn.jvmOr(input -> supplier.get(), null);
    }

    public static <T> T jvmOr(final Supplier<T> supplier, final T defaultValue) {
        return Fn.<T, T>jvmOr(input -> supplier.get(), defaultValue);
    }

    public static <T, R> R jvmOr(final Function<T, R> function) {
        return Fn.jvmOr(function, null);
    }

    public static <T, R> R jvmOr(final Function<T, R> function, final R defaultValue) {
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
}

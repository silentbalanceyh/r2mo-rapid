package io.r2mo.function;

import io.r2mo.typed.exception.AbstractException;
import io.r2mo.typed.exception.JvmException;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * @author lang : 2025-09-26
 */
@Slf4j
class FnIo {

    static <R> R jvmIo(final Supplier<R> supplier, final Supplier<JvmException> jvmFn) {
        return jvmIo(input -> supplier.get(), jvmFn);
    }

    static void jvmIo(final Actuator actuator, final Supplier<JvmException> jvmFn) {
        jvmIo(input -> {
            actuator.exec();
            return null;
        }, jvmFn);
    }

    static <T, R> R jvmIo(final Function<T, R> executor,
                          final Supplier<JvmException> jvmFn) {
        try {
            return executor.apply(null);
        } catch (final AbstractException ex) {
            log.error("[ R2MO ] Fn.jvmOr 基本抽象异常: ", ex);
            // 自定义异常
            throw ex;
        } catch (final Throwable ex) {
            throw jvmOut(ex, jvmFn);
        }
    }

    static <IO extends AutoCloseable, R> R jvmIo(final Supplier<IO> openFn, final Function<IO, R> executor,
                                                 final Supplier<JvmException> jvmFn) {
        try (final IO io = openFn.get()) {
            return executor.apply(io);
        } catch (final AbstractException ex) {
            log.error("[ R2MO ] Fn.jvmOr 基本抽象异常: ", ex);
            // 自定义异常
            throw ex;
        } catch (final Throwable ex) {
            throw jvmOut(ex, jvmFn);
        }
    }

    private static RuntimeException jvmOut(final Throwable ex, final Supplier<JvmException> jvmFn) {
        // 环境变量开启时打印异常堆栈
        log.error("[ R2MO ] Fn.jvmIo JVM异常: ", ex);
        final JvmException wrap = FnJvm.jvmOr(jvmFn, null);
        if (Objects.nonNull(wrap)) {
            throw wrap.cause(ex);
        }
        return new RuntimeException("[ R2MO ] Fn.jvmIo 无法捕捉的异常: " + ex.getMessage(), ex);
    }
}

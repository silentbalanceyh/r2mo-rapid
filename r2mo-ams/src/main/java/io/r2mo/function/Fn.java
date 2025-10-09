package io.r2mo.function;

import io.r2mo.typed.exception.AbstractException;
import io.r2mo.typed.exception.JvmException;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lang : 2025-09-02
 */
@Slf4j
public class Fn {

    public static <T extends AbstractException> void jvmKo(final java.util.function.Supplier<Boolean> condFn,
                                                           final Class<T> classE, final Object... args) {
        FnOut.jvmKo(condFn.get(), classE, args);
    }

    public static <T extends AbstractException> void jvmKo(final boolean checked,
                                                           final Class<T> classE, final Object... args) {
        FnOut.jvmKo(checked, classE, args);
    }

    public static <IO extends AutoCloseable, R> R jvmAs(
        final Supplier<IO> openFn, final Function<IO, R> executor, final Supplier<JvmException> jvmFn) {
        return FnIo.jvmIo(openFn, executor, jvmFn);
    }

    public static <R> R jvmAs(final Supplier<R> supplier, final Supplier<JvmException> jvmFn) {
        return FnIo.jvmIo(supplier, jvmFn);
    }

    public static void jvmAs(final Actuator actuator, final Supplier<JvmException> jvmFn) {
        FnIo.jvmIo(actuator, jvmFn);
    }

    public static <T, R> R jvmAs(final Function<T, R> executor, final Supplier<JvmException> jvmFn) {
        return FnIo.jvmIo(executor, jvmFn);
    }

    public static void jvmAt(final Actuator actuator) {
        FnJvm.jvmAt(actuator);
    }

    public static void jvmAt(final boolean isOk, final Actuator actuator) {
        if (isOk) {
            jvmAt(actuator);
        }
    }

    public static <T> void jvmAt(final Consumer<T> consumer) {
        FnJvm.jvmAt(consumer);
    }

    public static <T> void jvmAt(final boolean isOk, final Consumer<T> consumer) {
        if (isOk) {
            jvmAt(consumer);
        }
    }

    public static <T> Boolean jvmIf(final Predicate<T> predicate) {
        return FnJvm.jvmOr(predicate::test, null);
    }

    public static <T> T jvmOr(final Supplier<T> supplier) {
        return FnJvm.jvmOr(supplier, null);
    }

    public static <T> T jvmOr(final Supplier<T> supplier, final T defaultValue) {
        return FnJvm.jvmOr(supplier, defaultValue);
    }

    public static <T, R> R jvmOr(final Function<T, R> function) {
        return Fn.jvmOr(function, null);
    }

    public static <T, R> R jvmOr(final Function<T, R> function, final R defaultValue) {
        return FnJvm.jvmOr(function, defaultValue);
    }
}

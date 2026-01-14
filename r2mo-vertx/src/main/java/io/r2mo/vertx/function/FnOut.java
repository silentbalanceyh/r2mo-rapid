package io.r2mo.vertx.function;

import io.r2mo.SourceReflect;
import io.r2mo.typed.exception.AbstractException;
import io.r2mo.typed.exception.JvmException;
import io.r2mo.typed.exception.WebException;
import io.r2mo.typed.exception.web._500ServerInternalException;
import io.vertx.core.Future;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 统一从“异常类型 + 构造参数”生产 Vert.x 的失败 Future，或回落为成功 Future 的工具方法。
 *
 * <p>设计目标：
 * <ul>
 *   <li>调用方只需传入一个异常的 Class（例如 WebException / JvmException 的子类）与构造参数，
 *       即可得到 {@code Future.failedFuture(Throwable)}。</li>
 *   <li>若传入的异常类型既不是 WebException，也不是 JvmException（或为 null），
 *       则回落为 {@code Future.succeededFuture(null)}，避免强行失败。</li>
 * </ul>
 *
 * <p>关键点/注意事项：
 * <ul>
 *   <li>Vert.x 的 {@code Future.succeededFuture()}（无参）返回 {@code Future<Void>}，
 *       本方法签名为 {@code Future<T>}，直接返回会导致泛型不匹配或类型推断成 {@code Future<Object>}。
 *       因此采用 {@code Future.succeededFuture((T) null)} 以确保编译期类型安全。</li>
 *   <li>使用 {@code SourceReflect.instance(exceptionCls, args)} 进行反射构造，可能抛出运行时异常；
 *       这里将其包装为 {@code Future.failedFuture(e)} 返回给调用方，避免抛出至上层。</li>
 *   <li>当 {@code exceptionCls} 为空时，直接返回成功 Future（即不做失败封装）。</li>
 * </ul>
 *
 * <p>典型用法：
 * <pre>{@code
 *   // 失败返回：构造 WebException 的某个子类，并封装为 failedFuture
 *   return FnOut.failOut(MyWebException.class, "E_USER_NOT_FOUND", userId);
 *
 *   // 回落成功：既不是 WebException 也不是 JvmException
 *   return FnOut.failOut(String.class); // -> 成功 Future(null)
 * }</pre>
 *
 * @author lang
 * @since 2025-09-29
 */
@Slf4j
final class FnOut {

    // 工具类不需要实例化
    private FnOut() { /* no-op */ }

    /**
     * 强化之后的异常调用
     *
     * @param ex 调用过程中的异常
     * @return 通用 Zero 中的异常
     */
    static WebException failAt(final Throwable ex) {
        // 1. 始终打印原始异常堆栈，确保不丢失任何上下文
        Objects.requireNonNull(ex);
        // log.error("[ R2MO ] 异常捕获: {}", ex.getMessage(), ex);

        // 2. 定义搜索指针和候选项
        Throwable current = ex;
        AbstractException abstractCandidate = null; // 用于暂存 AbstractException (备胎)

        // 防止循环引用导致的死循环 (虽然罕见，但健壮性代码需要考虑)
        final Set<Throwable> visited = new HashSet<>();

        // 3. 深度遍历异常链 (从外向内剥洋葱)
        while (current != null && visited.add(current)) {

            // 优先级 I: 如果发现 WebException，直接返回
            // 这是最明确的业务意图，即使它被包裹在其他异常深处
            if (current instanceof final WebException webEx) {
                return webEx;
            }

            // 优先级 II: 如果发现 AbstractException，暂存起来
            // 我们继续往下找，万一里面还包着一个 WebException 呢？
            // 如果最终没找到 WebException，我们就用这个备胎。
            if (abstractCandidate == null && current instanceof final AbstractException absEx) {
                abstractCandidate = absEx;
            }

            // 继续向下挖掘
            current = current.getCause();
        }

        // 4. 遍历结束，根据查找结果决策

        // 场景 A: 没找到 WebException，但找到了 AbstractException (自定义通用异常)
        if (abstractCandidate != null) {
            return new _500ServerInternalException("[ R2MO ] 调用过程中的自定义异常：" + abstractCandidate.getMessage());
        }

        // 场景 B: 啥也没找到，说明是纯粹的意外 (如 NullPointer, IndexOutOfBounds)
        // 此时使用原始异常 ex 的 message，或者取 root cause 的 message 会更准确
        return new _500ServerInternalException("[ R2MO ] 调用过程中的其他异常：" + ex.getMessage());
    }

    /**
     * 尝试基于异常类型生成一个失败的 Future；若类型不匹配则返回成功 Future(null)。
     *
     * @param exceptionCls 期望构造并封装的异常类型（必须是 WebException 或 JvmException 的子类型，
     *                     否则将回落为成功 Future）。允许为 null（直接成功 Future）。
     * @param args         反射构造该异常时用到的参数（与目标异常类的构造器匹配）。
     * @param <T>          成功场景下 Future 的值类型；本方法仅在“非异常类型”时返回成功，
     *                     其值恒为 {@code null}，因此类型由调用方上下文推断。
     * @return 若可识别（WebException/JvmException），返回 {@code Future.failedFuture(ex)}；
     * 否则返回 {@code Future.succeededFuture(null)}。
     */
    // 为了返回 Future<T> 的 null 结果，需要 (T) null 转型
    static <T> Future<T> failOut(final Class<?> exceptionCls, final Object... args) {
        // 1) 容错：未指定异常类型 -> 直接成功（null）
        if (exceptionCls == null) {
            return Future.succeededFuture();
        }

        try {
            // 2) 若是 WebException 或其子类：构造并返回失败 Future
            if (WebException.class.isAssignableFrom(exceptionCls)) {
                final WebException error = (WebException) SourceReflect.instance(exceptionCls, args);
                return Future.failedFuture(error);
            }

            // 3) 若是 JvmException 或其子类：构造并返回失败 Future
            if (JvmException.class.isAssignableFrom(exceptionCls)) {
                final JvmException error = (JvmException) SourceReflect.instance(exceptionCls, args);
                return Future.failedFuture(error);
            }

            // 4) 其它类型：按约定回落为“成功(null)”
            //    注意：这里不抛异常，是为了把“无法识别的异常类型”当作“非失败信号”处理。
            return Future.succeededFuture();
        } catch (final Throwable reflectError) {
            // 5) 构造过程中可能出现：
            //    - 没有匹配的构造器
            //    - 构造器抛出异常
            //    - 访问权限问题
            //    - SourceReflect 内部错误
            //    直接转换为 failedFuture 返回，避免把异常抛给上层。
            return Future.failedFuture(reflectError);
        }
    }

    @SuppressWarnings("all")
    static <T> Function<Throwable, T> otherwiseFn(final Supplier<T> supplier) {
        return error -> {
            if (Objects.nonNull(error)) {
                log.error("[ R2MO ] Otherwise 异常输出", error);
                error.printStackTrace();
            }
            return supplier.get();
        };
    }

    @SuppressWarnings("all")
    static <T> Function<Throwable, Future<T>> recoverFn(final Supplier<T> supplier) {
        return error -> {
            if (Objects.nonNull(error)) {
                log.error("[ R2MO ] Recover 异常输出", error);
                error.printStackTrace();
            }
            return Future.failedFuture(error);
        };
    }
}

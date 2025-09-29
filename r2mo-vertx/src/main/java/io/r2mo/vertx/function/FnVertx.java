package io.r2mo.vertx.function;

import io.r2mo.function.Fn;
import io.vertx.core.Future;

/**
 * 此处要和上层的 Fn 进行一个区分，在引入过程中，避免冲突
 * <pre>
 *     1. AMS -> {@link Fn} 标准抽象函数，最顶层函数
 *     2. Vertx -> {@link FnVertx} 结合 Vertx 进行的函数扩展
 *     Zero 框架层
 *     3. Zero AMS -> {@see HFn} 结合 Zero AMS 高阶进行函数扩展
 *     4. Runtime 运行时 -> {@see FnZero} 结合 Runtime 进行的函数扩展
 * </pre>
 * 注：
 * - HFn 除了 Zero AMS 内部使用，外部不可直接调用，虽然调用是可行的，但不符合整体编程规范
 * 1. {@link FnVertx} 和 {@link Fn}
 * 2. {@see FnZero} -> 它继承自 HFn
 *
 * @author lang : 2025-09-26
 */
public class FnVertx {
    /**
     * 基于异常类型快速生成 Vert.x 的失败 {@link io.vertx.core.Future}，用于“异步短路返回”的统一入口。
     *
     * <p><b>新的推荐写法</b>：直接调用 {@code FnVertx.failOut(...)}。
     * 本方法只是一个轻量代理，便于老代码平滑迁移，实际逻辑全部委托给 {@see FnVertx#failOut(Class, Object...)}。
     *
     * <p><b>为何不用 boolean 作为参数</b>：
     * <ul>
     *   <li>是否中断应在方法外判断；在异步链中，<b>返回</b>一个失败的 Future（而非 {@code throw}）才是有效的短路方式。</li>
     *   <li>典型外层形态：
     *   <pre><code>
     *   if (invalid) {
     *     return FnVertx.failOut(_401UnauthorizedException.class, getClass(), token);
     *   }
     *   </code></pre>
     *   </li>
     * </ul>
     *
     * <p><b>行为约定</b>（由 {@code FnVertx.failOut} 保证）：
     * <ul>
     *   <li>当 {@code exceptionCls} 是 {@code WebException}/{@code JvmException} 的子类时：
     *       通过反射构造异常实例并返回 {@code Future.failedFuture(ex)}。</li>
     *   <li>当 {@code exceptionCls} 为其他类型或 {@code null}：
     *       返回 {@code Future.succeededFuture()}（安全回落，不误杀异步链）。</li>
     *   <li>反射构造出错（无匹配构造器/构造器抛错等）：
     *       返回 {@code Future.failedFuture(throwable)}，确保错误在异步管道内可观测。</li>
     * </ul>
     *
     * <p><b>迁移对照</b>（同步抛异常 → 异步失败返回）：
     * <pre><code>
     * 旧：throw Ut.Bnd.failWeb(...);
     * 新：return FnVertx.failOut(_4xxSomeWebException.class, getClass(), args...);
     *
     * 旧：FnZero.outWeb(...);   // 抛异常
     * 新：FnVertx.failOut(...); // 返回失败 Future（推荐）
     * </code></pre>
     *
     * <p><b>使用示例</b>：
     * <pre><code>
     * return svc.fetchUser(userId)
     *   .compose(user -> {
     *     if (user == null) {
     *       return FnVertx.failOut(_404UserNotFoundException.class, getClass(), userId);
     *     }
     *     return svc.loadProfile(user);
     *   })
     *   .recover(err -> {
     *     log.warn("Async error: {}", err.toString());
     *     return Future.succeededFuture(DefaultProfile.INSTANCE);
     *   });
     * </code></pre>
     *
     * <p><b>模块化/OSGi</b>：
     * {@code FnVertx.failOut} 不依赖线程上下文类加载器切换与全局缓存，适配 JPMS/OSGi 等模块化环境；
     * 仅需确保异常类及其可见构造器在当前 ClassLoader 下可达。</p>
     *
     * <p><b>性能建议</b>：
     * 此方法用于异常分支（低频）；若高频使用且创建异常成本敏感，可在上层缓存异常模板或统一工厂，但需权衡堆栈可读性。</p>
     *
     * @param exceptionCls 异常类（通常为 {@code WebException}/{@code JvmException} 的子类）；其他类型或 {@code null} 将回落成功（值为 {@code null}）
     * @param args         反射构造该异常实例所需的参数（需匹配某个构造器）
     * @param <T>          成功分支时的返回类型占位（成功时恒为 {@code null}，类型由调用上下文推断）
     *
     * @return 若识别为受支持异常 → {@code Future.failedFuture(ex)}；否则 → {@code Future.succeededFuture()}
     * @see FnVertx#failOut(Class, Object...)
     * @see io.vertx.core.Future
     */
    public static <T> Future<T> failOut(final Class<?> exceptionCls, final Object... args) {
        return FnOut.failOut(exceptionCls, args);
    }
}

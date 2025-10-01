package io.r2mo.vertx.function;

import io.r2mo.function.Fn;
import io.r2mo.typed.exception.AbstractException;
import io.r2mo.typed.exception.WebException;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

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
     * 🚨 基于异常类型快速生成 Vert.x 的失败 {@link io.vertx.core.Future}，用于"异步短路返回"的统一入口。
     *
     * <p><b>🆕 新的推荐写法</b>：直接调用 {@code FnVertx.failOut(...)}。
     * 🔄 本方法只是一个轻量代理，便于老代码平滑迁移，实际逻辑全部委托给 {@see FnVertx#failOut(Class, Object...)}。
     *
     * <p><b>❓ 为何不用 boolean 作为参数</b>：
     * <ul>
     *   <li>⚡ 是否中断应在方法外判断；在异步链中，<b>返回</b>一个失败的 Future（而非 {@code throw}）才是有效的短路方式。</li>
     *   <li>🏗️ 典型外层形态：
     *   <pre><code>
     *   if (invalid) {
     *     return FnVertx.failOut(_401UnauthorizedException.class, getClass(), token);
     *   }
     *   </code></pre>
     *   </li>
     * </ul>
     *
     * <p><b>⚙️ 行为约定</b>（由 {@code FnVertx.failOut} 保证）：
     * <ul>
     *   <li>✅ 当 {@code exceptionCls} 是 {@code WebException}/{@code JvmException} 的子类时：
     *       🔄 通过反射构造异常实例并返回 {@code Future.failedFuture(ex)}。</li>
     *   <li>🛡️ 当 {@code exceptionCls} 为其他类型或 {@code null}：
     *       🔄 返回 {@code Future.succeededFuture()}（安全回落，不误杀异步链）。</li>
     *   <li>🔧 反射构造出错（无匹配构造器/构造器抛错等）：
     *       🔄 返回 {@code Future.failedFuture(throwable)}，确保错误在异步管道内可观测。</li>
     * </ul>
     *
     * <p><b>🔄 迁移对照</b>（同步抛异常 → 异步失败返回）：
     * <pre><code>
     * 📄 旧：throw Ut.Bnd.failWeb(...);
     * 🎯 新：return FnVertx.failOut(_4xxSomeWebException.class, getClass(), args...);
     *
     * 📄 旧：FnZero.outWeb(...);   // 🚨 抛异常
     * 🎯 新：FnVertx.failOut(...); // 🔄 返回失败 Future（推荐）
     * </code></pre>
     *
     * <p><b>🎯 使用示例</b>：
     * <pre><code>
     * return svc.fetchUser(userId)
     *   .compose(user -> {
     *     if (user == null) {
     *       🚨 return FnVertx.failOut(_404UserNotFoundException.class, getClass(), userId);
     *     }
     *     return svc.loadProfile(user);
     *   })
     *   .recover(err -> {
     *     📝 log.warn("Async error: {}", err.toString());
     *     return Future.succeededFuture(DefaultProfile.INSTANCE);
     *   });
     * </code></pre>
     *
     * <p><b>📦 模块化/OSGi</b>：
     * 🔄 {@code FnVertx.failOut} 不依赖线程上下文类加载器切换与全局缓存，适配 JPMS/OSGi 等模块化环境；
     * 🎯 仅需确保异常类及其可见构造器在当前 ClassLoader 下可达。</p>
     *
     * <p><b>⚡ 性能建议</b>：
     * 🚨 此方法用于异常分支（低频）；若高频使用且创建异常成本敏感，可在上层缓存异常模板或统一工厂，但需权衡堆栈可读性。</p>
     *
     * @param exceptionCls 🚨 异常类（通常为 {@code WebException}/{@code JvmException} 的子类）；其他类型或 {@code null} 将回落成功（值为 {@code null}）
     * @param args         🔧 反射构造该异常实例所需的参数（需匹配某个构造器）
     * @param <T>          💾 成功分支时的返回类型占位（成功时恒为 {@code null}，类型由调用上下文推断）
     *
     * @return 🎯 若识别为受支持异常 → {@code Future.failedFuture(ex)}；否则 → {@code Future.succeededFuture()}
     * @see FnVertx#failOut(Class, Object...)
     * @see io.vertx.core.Future
     */
    public static <T> Future<T> failOut(final Class<?> exceptionCls, final Object... args) {
        return FnOut.failOut(exceptionCls, args);
    }

    public static JsonObject adapt(final WebException error) {
        return FnAdaptor.adapt(error);
    }

    // ---------------------------- otherwiseFn 方法专用函数，用于输出

    /**
     * 异常输出函数，位于 Vertx 中 {@link Future} 的最终回调函数
     *
     * @param supplier 供应器
     * @param <T>      输出类型
     *
     * @return {@link Function}
     */
    public static <T> Function<Throwable, T> otherwiseFn(final Supplier<T> supplier) {
        return FnOut.otherwiseFn(supplier);
    }

    /**
     * ✅ 并行检查器 - ALL模式
     *
     * <p>🎯 功能描述：检查所有的异步结果，全部为 true 时则通过检查，最终返回双态 Monad
     *
     * <p>📊 执行流程图：
     * <pre>
     * 📥 Input ──┐
     *            ├───→ 🧪 Checker1 ──→ 🔄 Future<Boolean> ──┐
     * 📥 Input ──┤                                        ├──→ 🎯 ALL → TRUE → PASS
     *            ├───→ 🧪 Checker2 ──→ 🔄 Future<Boolean> ──┤    │
     * 📥 Input ──┤                                        │    └─→ ❌ ANY → FALSE → FAIL
     *            └───→ 🧪 CheckerN ──→ 🔄 Future<Boolean> ──┘
     *
     * 📊 逻辑图：
     * [true, true, true, ..., true] → ✅ PASS (allMatch == true)
     * [true, true, false, ..., true] → ❌ FAIL (allMatch == false)
     * [false, false, false, ..., false] → ❌ FAIL (allMatch == false)
     * </pre>
     *
     * <p>🏗️ 设计理由：
     * • 🎯 严格验证：所有条件都必须满足才能通过
     * • ⚡ 高性能：并行执行，减少等待时间
     * • 🔒 安全性：所有安全检查都通过才允许操作
     * • 📈 可扩展：支持任意数量的检查器
     *
     * <p>🎨 使用场景：
     * • 用户权限验证（需要所有权限都具备）
     * • 数据完整性检查（所有验证规则都通过）
     * • 系统健康检查（所有组件都正常）
     *
     * @param response  📤 响应对象，用于传递给检查器
     * @param error     ⚠️ 检查失败时抛出的异常实例
     * @param executors ⚙️ 检查器集合，每个检查器返回 Future<Boolean>
     * @param <T>       💾 响应对象的泛型类型
     * @param <E>       🚨 异常对象的泛型类型，继承自 AbstractException
     *
     * @return {@link Future}<T> 🌟 异步结果，检查通过时返回原始响应，失败时抛出异常
     */
    public static <T, E extends AbstractException> Future<T> passAll(
        final T response, final E error,
        final Set<Function<T, Future<Boolean>>> executors) {
        return FnAsync.pass(response, error, list -> list.stream().allMatch(Boolean::booleanValue), executors);
    }

    /**
     * 🔍 并行检查器 - ANY模式
     *
     * <p>🎯 功能描述：检查所有的异步结果，只要有一个为 true 时则通过检查，最终返回双态 Monad
     *
     * <p>📊 执行流程图：
     * <pre>
     * 📥 Input ──┐
     *            ├───→ 🧪 Checker1 ──→ 🔄 Future<Boolean> ──┐
     * 📥 Input ──┤                                        ├──→ 🔍 ANY → TRUE → PASS
     *            ├───→ 🧪 Checker2 ──→ 🔄 Future<Boolean> ──┤    │
     * 📥 Input ──┤                                        │    └─→ ❌ ALL → FALSE → FAIL
     *            └───→ 🧪 CheckerN ──→ 🔄 Future<Boolean> ──┘
     *
     * 📊 逻辑图：
     * [false, false, true, ..., false] → ✅ PASS (anyMatch == true)
     * [true, false, false, ..., false] → ✅ PASS (anyMatch == true)
     * [false, false, false, ..., false] → ❌ FAIL (anyMatch == false)
     * [true, true, true, ..., true] → ✅ PASS (anyMatch == true)
     * </pre>
     *
     * <p>🏗️ 设计理由：
     * • 🎯 灵活验证：满足任一条件即可通过
     * • ⚡ 快速响应：一旦有检查通过立即返回
     * • 🔁 故障转移：多个备选方案中任一可用即可
     * • 📈 性能优化：减少不必要的检查等待
     *
     * <p>🎨 使用场景：
     * • 多重认证（任意一种认证方式通过即可）
     * • 服务发现（任一服务节点可用即可）
     * • 缓存策略（任一缓存层级命中即可）
     *
     * @param response  📤 响应对象，用于传递给检查器
     * @param error     ⚠️ 检查失败时抛出的异常实例
     * @param executors ⚙️ 检查器集合，每个检查器返回 Future<Boolean>
     * @param <T>       💾 响应对象的泛型类型
     * @param <E>       🚨 异常对象的泛型类型，继承自 AbstractException
     *
     * @return {@link Future}<T> 🌟 异步结果，检查通过时返回原始响应，失败时抛出异常
     */
    public static <T, E extends AbstractException> Future<T> passAny(
        final T response, final E error,
        final Set<Function<T, Future<Boolean>>> executors) {
        return FnAsync.pass(response, error, list -> list.stream().anyMatch(Boolean::booleanValue), executors);
    }

    /**
     * 🚫 并行检查器 - NONE模式
     *
     * <p>🎯 功能描述：检查所有的异步结果，所有结果都为 false 时则通过检查
     *
     * <p>📊 执行流程图：
     * <pre>
     * 📥 Input ──┐
     *            ├───→ 🧪 Checker1 ──→ 🔄 Future<Boolean> ──┐
     * 📥 Input ──┤                                        ├──→ 🚫 NONE → FALSE → PASS
     *            ├───→ 🧪 Checker2 ──→ 🔄 Future<Boolean> ──┤    │
     * 📥 Input ──┤                                        │    └─→ ✅ ANY → TRUE → FAIL
     *            └───→ 🧪 CheckerN ──→ 🔄 Future<Boolean> ──┘
     *
     * 📊 逻辑图：
     * [false, false, false, ..., false] → ✅ PASS (noneMatch == true)
     * [true, false, false, ..., false] → ❌ FAIL (noneMatch == false)
     * [false, true, false, ..., false] → ❌ FAIL (noneMatch == false)
     * [true, true, true, ..., true] → ❌ FAIL (noneMatch == false)
     * </pre>
     *
     * <p>🏗️ 设计理由：
     * • 🎯 安全验证：确保没有危险条件存在
     * • ⚡ 风险控制：所有风险因素都为否才允许操作
     * • 🔒 安全审计：确保没有违规操作
     * • 📊 质量保证：确保没有质量问题
     *
     * <p>🎨 使用场景：
     * • 安全扫描（确保没有安全漏洞）
     * • 质量检查（确保没有质量问题）
     * • 风险评估（确保没有风险因素）
     *
     * @param response  📤 响应对象，用于传递给检查器
     * @param error     ⚠️ 检查失败时抛出的异常实例
     * @param executors ⚙️ 检查器集合，每个检查器返回 Future<Boolean>
     * @param <T>       💾 响应对象的泛型类型
     * @param <E>       🚨 异常对象的泛型类型，继承自 AbstractException
     *
     * @return {@link Future}<T> 🌟 异步结果，检查通过时返回原始响应，失败时抛出异常
     */
    public static <T, E extends AbstractException> Future<T> passNone(
        final T response, final E error,
        final Set<Function<T, Future<Boolean>>> executors) {
        return FnAsync.pass(response, error, list -> list.stream().noneMatch(Boolean::booleanValue), executors);
    }

    /**
     * ⚡ 并行编排器 - PARALLEL模式
     *
     * <p>🎯 功能描述：并行执行多个任务，不关心中间结果，只关心执行是否成功
     *
     * <p>📊 执行流程图：
     * <pre>
     * 📥 Input ──→ 🔄 Executor1 ──→ 🎯 Output1
     *            ├──→ 🔄 Executor2 ──→ 🎯 Output2
     *            ├──→ 🔄 Executor3 ──→ 🎯 Output3
     *            └──→ 🔄 ExecutorN ──→ 🎯 OutputN
     *                                    │
     *                                    └──→ 🎯 ALL COMPLETED → Future<T>
     *
     * 🔄 执行时序：
     * T0: Input → Executor1(start), Executor2(start), Executor3(start), ..., ExecutorN(start)
     * T1: All executors running in parallel
     * T2: All executors completed → return Future<T>
     * </pre>
     *
     * <p>🏗️ 设计理由：
     * • ⚡ 性能优化：并行执行提高效率
     * • 🎯 任务独立：各任务互不影响
     * • 🔁 资源利用：充分利用多核CPU
     * • 📈 可扩展性：支持任意数量的任务
     *
     * <p>🎨 使用场景：
     * • 数据同步（多个数据源并行同步）
     * • 服务通知（通知多个下游服务）
     * • 缓存更新（多个缓存同时更新）
     *
     * @param input     📥 输入数据，传递给所有执行器
     * @param executors ⚙️ 执行器集合，每个执行器处理输入并返回Future<T>
     * @param <T>       💾 输入输出数据的泛型类型
     *
     * @return {@link Future}<T> 🌟 异步结果，所有任务完成后返回输入数据
     */
    public static <T> Future<T> parallel(final T input, final Set<Function<T, Future<T>>> executors) {
        return FnAsync.parallel(input, executors);
    }

    /**
     * 🔄 并行编排器 - List重载版本
     *
     * <p>🎯 功能描述：提供List接口的并行编排器重载版本，使用便捷
     *
     * <p>🏗️ 设计理由：
     * • 🎯 接口统一：提供多种参数形式
     * • 🔧 使用便捷：支持List数据结构
     * • 📈 类型安全：泛型保证类型安全
     *
     * @param input     📥 输入数据，传递给所有执行器
     * @param executors ⚙️ 执行器列表，每个执行器处理输入并返回Future<T>
     * @param <T>       💾 输入输出数据的泛型类型
     *
     * @return {@link Future}<T> 🌟 异步结果，所有任务完成后返回输入数据
     */
    public static <T> Future<T> parallel(final T input, final List<Function<T, Future<T>>> executors) {
        return FnAsync.parallel(input, new HashSet<>(executors));
    }

    /**
     * 🛠️ 并行编排器 - 可变参数版本
     *
     * <p>🎯 功能描述：提供可变参数的并行编排器重载版本，使用更加便捷
     *
     * <p>🏗️ 设计理由：
     * • 🎯 使用便捷：支持直接传入多个执行器
     * • 🔧 语法糖：简化调用语法
     * • 📈 开发效率：减少代码量
     *
     * @param input     📥 输入数据，传递给所有执行器
     * @param executors ⚙️ 可变参数执行器，每个执行器处理输入并返回Future<T>
     * @param <T>       💾 输入输出数据的泛型类型
     *
     * @return {@link Future}<T> 🌟 异步结果，所有任务完成后返回输入数据
     */
    @SafeVarargs
    public static <T> Future<T> parallel(final T input, final Function<T, Future<T>>... executors) {
        return FnAsync.parallel(input, new HashSet<>(Arrays.asList(executors)));
    }

    /**
     * 🎯 异步串行编排器 - PASSION模式
     *
     * <p>🎯 功能描述：串行执行任务，前一个任务的输出作为后一个任务的输入
     *
     * <p>📊 执行流程图：
     * <pre>
     * 📥 Input ──→ 🔄 Executor1 ──→ 🎯 Output1 ──→ 🔄 Executor2 ──→ 🎯 Output2 ──→ ... ──→ 🔄 ExecutorN ──→ 🎯 FinalOutput
     *             │                    │                    │                           │                    │
     *             └─→ T1 ──→ T2 ──→ T3 ──→ T4 ──→ T5 ──→ ... ──→ TN ──→ TN+1 ──→ TN+2 ──→ TN+3 ──→ TN+4 ──→ Result
     *
     * 🔄 执行时序：
     * T0: Input → Executor1(start)
     * T1: Executor1 → Output1 → Executor2(start)
     * T2: Executor2 → Output2 → Executor3(start)
     * ...
     * TN: ExecutorN-1 → OutputN-1 → ExecutorN(start)
     * TN+1: ExecutorN → FinalOutput → return Future<FinalOutput>
     * </pre>
     *
     * <p>🏗️ 设计理由：
     * • 🎯 数据流转：支持数据在任务间传递
     * • 🔗 依赖关系：任务间存在明确的依赖关系
     * • 📈 顺序保证：确保任务按序执行
     * • ⚡ 异步处理：非阻塞式串行执行
     *
     * <p>🎨 使用场景：
     * • 数据处理流水线（ETL流程）
     * • 业务流程编排（审批流程）
     * • 数据转换链（格式转换）
     *
     * @param input     📥 初始输入数据
     * @param executors ⚙️ 执行器列表，每个执行器接收前一个的输出作为输入
     * @param <T>       💾 输入输出数据的泛型类型
     *
     * @return {@link Future}<T> 🌟 异步结果，最终任务的输出
     */
    public static <T> Future<T> passion(final T input, final List<Function<T, Future<T>>> executors) {
        return FnAsync.passion(input, executors);
    }

    /**
     * 🔗 异步串行编排器 - 可变参数版本
     *
     * <p>🎯 功能描述：提供可变参数的串行编排器重载版本，使用更加便捷
     *
     * <p>🏗️ 设计理由：
     * • 🎯 使用便捷：支持直接传入多个执行器
     * • 🔧 语法糖：简化串行调用语法
     * • 📈 开发效率：减少代码量和复杂度
     *
     * @param input     📥 初始输入数据
     * @param executors ⚙️ 可变参数执行器，每个执行器接收前一个的输出作为输入
     * @param <T>       💾 输入输出数据的泛型类型
     *
     * @return {@link Future}<T> 🌟 异步结果，最终任务的输出
     */
    @SafeVarargs
    public static <T> Future<T> passion(final T input, final Function<T, Future<T>>... executors) {
        return FnAsync.passion(input, Arrays.asList(executors));
    }

    /**
     * 🔄 二元组合函数 - Future 合并模式
     *
     * <p>📊 执行流程图：
     * <pre><code>
     *                          combinerOf
     *                           f + s => ( t )
     *      ( f )        -->       fx              -->     ( t )
     *      ( s )        -->
     * </code></pre>
     *
     * <p>🎯 功能描述：针对两个异步结果执行合并，这两个异步结果可以是返回不同类型，若类型不相同则使用 combinerOf 组合函数执行
     * 最终结果的组合，组合过程也是可异步执行的操作
     *
     * <p>🏗️ 设计理由：
     * • 🎯 异步合并：支持两个不同类型的异步结果合并
     * • 🔗 类型安全：泛型保证类型安全
     * • ⚡ 并行执行：两个 Future 可以并行执行
     *
     * <p>🎨 使用场景：
     * • 数据聚合（合并用户信息和订单信息）
     * • 服务调用（合并多个微服务结果）
     * • 配置加载（合并多个配置源）
     *
     * @param futureF    📤 Future<F> 输入的异步结果，结果内是 F
     * @param futureS    📤 Future<S> 输入的异步结果，结果内是 S
     * @param combinerOf 🔧 BiFunction<F, S, Future<T>> 组合函数，输入为 F 和 S，输出为 Future<T>
     * @param <F>        💾 第一个异步结果 F
     * @param <S>        💾 第二个异步结果 S
     * @param <T>        🎯 组合函数的最终执行结果 T
     *
     * @return Future<T> 🌟 返回执行过的结果
     */
    public static <F, S, T> Future<T> combineT(
        final Future<F> futureF, final Future<S> futureS,
        final BiFunction<F, S, Future<T>> combinerOf) {
        return FnCombine.combineT(() -> futureF, () -> futureS, combinerOf);
    }

    /**
     * 🔗 二元组合函数的顺序模式
     *
     * <p>📊 执行流程图：
     * <pre><code>
     *                                                       combinerOf
     *                                                       f + s -> ( t )
     *       supplierF                                  |
     *          fx      ->   (f)                   f    |
     *                                                  |        fx          -->   ( t )
     *                            functionS             |
     *                        f      fx(f)   ->   (s)   |
     * </code></pre>
     *
     * <p>🎯 功能描述：二元组合函数的变体，参数可支持延迟执行，执行流程如下：
     * 1. 先执行第一个函数的 supplier 得到第一输出 f
     * 2. 根据第一输出执行第二个函数 function 得到第二输出 s
     * 3. 组合函数将第一输出 f 和第二输出 s 作为参数合并得到最终输出
     * 前两个函数的执行是异步顺序，且第二个函数的输入依赖第一个函数的输出
     *
     * <p>🏗️ 设计理由：
     * • 🔗 依赖关系：支持前后依赖的异步操作
     * • 🎯 顺序执行：确保操作按序完成
     * • 🔧 灵活性：支持延迟执行
     *
     * <p>🎨 使用场景：
     * • 数据获取链（先获取用户ID，再获取用户详情）
     * • 认证流程（先验证Token，再获取用户信息）
     * • 事务处理（先创建订单，再处理支付）
     *
     * @param supplierF  🔧 Supplier<Future<F>> 输入的异步结果，结果内是 F
     * @param functionS  🔧 Function<F, Future<S>> 输入的异步结果，结果内是 S
     * @param combinerOf 🔧 BiFunction<F, S, Future<T>> 组合函数，输入为 F 和 S，输出为 Future<T>
     * @param <F>        💾 第一个异步结果 F
     * @param <S>        💾 第二个异步结果 S
     * @param <T>        🎯 组合函数的最终执行结果 T
     *
     * @return Future<T> 🌟 返回执行过的结果
     */
    public static <F, S, T> Future<T> combineT(final Supplier<Future<F>> supplierF,
                                               final Function<F, Future<S>> functionS,
                                               final BiFunction<F, S, Future<T>> combinerOf) {
        return FnCombine.combineT(supplierF, functionS, combinerOf);
    }

    /**
     * 🔗 二元组合函数 - Future + Function 模式
     *
     * <p>🎯 功能描述：二元组合函数的混合模式，第一个参数是预执行的 Future，第二个参数是依赖第一个结果的函数
     *
     * <p>🏗️ 设计理由：
     * • 🎯 混合模式：支持预执行和依赖执行的混合
     * • 🔗 灵活组合：适应不同的异步场景
     *
     * @param futureF    📤 Future<F> 预执行的异步结果
     * @param functionS  🔧 Function<F, Future<S>> 依赖第一个结果的异步函数
     * @param combinerOf 🔧 BiFunction<F, S, Future<T>> 组合函数
     * @param <F>        💾 第一个异步结果 F
     * @param <S>        💾 第二个异步结果 S
     * @param <T>        🎯 组合函数的最终执行结果 T
     *
     * @return Future<T> 🌟 返回执行过的结果
     */
    public static <F, S, T> Future<T> combineT(final Future<F> futureF,
                                               final Function<F, Future<S>> functionS,
                                               final BiFunction<F, S, Future<T>> combinerOf) {
        return FnCombine.combineT(() -> futureF, functionS, combinerOf);
    }

    /**
     * 📦 二阶组合函数 - 集合处理模式
     *
     * <p>📊 执行流程图：
     * <pre><code>
     * ( [                       [
     *                                       combinerOf
     *     i                         i  -->     fx      ( t )
     *     i           --->          i  -->     fx      ( t )            --> ( [ t, t, t ] )
     *     i                         i  -->     fx      ( t )
     * ] )                       ]
     * </code></pre>
     *
     * <p>🎯 功能描述：针对异步集合结果中的每个元素执行二阶组合，最终生成一个新的集合异步结果：
     * 1. 先提取 futureL 中的最终结果 List<I>
     * 2. 然后遍历结果集合
     *
     * <p>🏗️ 设计理由：
     * • 📦 集合处理：批量处理集合中的异步操作
     * • ⚡ 并行执行：集合元素可以并行处理
     * • 🎯 类型转换：支持类型转换和映射
     *
     * <p>🎨 使用场景：
     * • 批量数据转换（批量处理用户列表）
     * • 集合异步映射（异步转换集合元素）
     * • 批量服务调用（对集合中每个元素调用服务）
     *
     * @param futureL    📤 Future<List<S>> 输入的异步结果，结果内是 List<S>
     * @param combinerOf 🔧 Function<S, Future<T>> 组合函数，输入为 S，输出为 Future<T>
     * @param <I>        💾 输入集合元素类型 I
     * @param <T>        🎯 组合函数的最终执行结果 T
     *
     * @return Future<List<T>> 🌟 返回执行过的结果数组
     */
    public static <I, T> Future<List<T>> combineT(final Future<List<I>> futureL, final Function<I, Future<T>> combinerOf) {
        return futureL.compose(source -> combineT(source, combinerOf));
    }

    /**
     * 🔄 组合函数最简单的模式 - List 版本
     *
     * <p>📊 执行流程图：
     * <pre><code>
     * [
     *      (t)
     *      (t)     -->     ( [ t, t, t ] )
     *      (t)
     * ]
     * </code></pre>
     *
     * <p>🎯 功能描述：将多个异步 Future 合并为一个包含所有结果的 List
     *
     * <p>🏗️ 设计理由：
     * • 🎯 简单合并：最基础的 Future 合并功能
     * • ⚡ 并行执行：所有 Future 并行执行
     * • 📦 结果收集：收集所有执行结果
     *
     * <p>🎨 使用场景：
     * • 批量数据获取（并行获取多个数据源）
     * • 并行服务调用（调用多个并行服务）
     * • 数据聚合（合并多个异步结果）
     *
     * @param futures List<Future<T>> 📤 输入的异步结果，结果内是 T
     * @param <T>     💾 泛型类型
     *
     * @return Future<List<T>> 🌟 返回执行过的结果数组
     */
    public static <T> Future<List<T>> combineT(final List<Future<T>> futures) {
        return FnCombine.combineT(futures);
    }

    /**
     * 🔄 组合函数最简单的模式 - Set 版本
     *
     * <p>🎯 功能描述：将多个异步 Future 合并为一个包含所有结果的 Set
     *
     * <p>🏗️ 设计理由：
     * • 🎯 去重合并：使用 Set 保证结果唯一性
     * • ⚡ 并行执行：所有 Future 并行执行
     * • 📦 结果收集：收集所有执行结果（去重）
     *
     * <p>🎨 使用场景：
     * • 去重数据聚合（合并结果并去重）
     * • 唯一性保证（确保结果不重复）
     * • 集合操作（需要唯一性的集合处理）
     *
     * @param futures Set<Future<T>> 📤 输入的异步结果，结果内是 T
     * @param <T>     💾 泛型类型
     *
     * @return Future<Set<T>> 🌟 返回执行过的结果集合
     */
    public static <T> Future<Set<T>> combineT(final Set<Future<T>> futures) {
        return FnCombine.combineT(futures);
    }

    /**
     * 🔄 组合函数的同步模式 - List 版本
     *
     * <p>📊 执行流程图：
     * <pre><code>
     * [
     *               combinerOf
     *      i  -->       fx      ( t )
     *      i  -->       fx      ( t )            --> ( [ t, t, t ] )
     *      i  -->       fx      ( t )
     * ]
     * </code></pre>
     *
     * <p>🎯 功能描述：对同步集合中的每个元素应用异步组合函数，返回异步结果集合
     *
     * <p>🏗️ 设计理由：
     * • 🎯 同步转异步：将同步集合转换为异步处理
     * • ⚡ 并行处理：集合元素并行异步处理
     * • 🔧 灵活映射：支持复杂的异步转换
     *
     * <p>🎨 使用场景：
     * • 同步数据异步处理（对同步数据进行异步转换）
     * • 批量异步映射（同步数据的异步映射）
     * • 数据预处理（同步数据的异步预处理）
     *
     * @param source     📥 输入的集合 List<I>
     * @param combinerOf 🔧 Function<I, Future<T>> 组合函数，输入为 I，输出为 Future<T>
     * @param <I>        💾 输入类型I
     * @param <T>        🎯 输出类型T
     *
     * @return Future<List<T>> 🌟 返回执行过的结果数组
     */
    public static <I, T> Future<List<T>> combineT(final List<I> source,
                                                  final Function<I, Future<T>> combinerOf) {
        final List<Future<T>> futures = new ArrayList<>();
        source.stream().map(combinerOf).forEach(futures::add);
        return FnCombine.combineT(futures);
    }

    /**
     * 🔄 组合函数的同步模式 - Set 版本
     *
     * <p>🎯 功能描述：对同步集合中的每个元素应用异步组合函数，返回异步结果集合（去重）
     *
     * <p>🏗️ 设计理由：
     * • 🎯 同步转异步：将同步集合转换为异步处理
     * • ⚡ 并行处理：集合元素并行异步处理
     * • 🔗 唯一性保证：使用 Set 保证结果唯一
     *
     * <p>🎨 使用场景：
     * • 去重异步处理（对同步数据异步处理并去重）
     * • 唯一性转换（确保异步转换结果唯一）
     *
     * @param source     📥 输入的集合 Set<I>
     * @param combinerOf 🔧 Function<I, Future<T>> 组合函数，输入为 I，输出为 Future<T>
     * @param <I>        💾 输入类型I
     * @param <T>        🎯 输出类型T
     *
     * @return Future<Set<T>> 🌟 返回执行过的结果集合
     */
    public static <I, T> Future<Set<T>> combineT(final Set<I> source,
                                                 final Function<I, Future<T>> combinerOf) {
        final Set<Future<T>> futures = new HashSet<>();
        source.stream().map(combinerOf).forEach(futures::add);
        return FnCombine.combineT(futures);
    }

    /**
     * 🔄 二元组合函数 - 延迟执行模式
     *
     * <p>📊 执行流程图：
     * <pre><code>
     *                                  combinerOf
     *                                    f + s -> ( t )
     *     supplierF               |
     *         fx   ->  f    -->   |
     *                             o        fx       -->     (t3)
     *     supplierS               |
     *         fx   ->  s    -->   |
     * </code></pre>
     *
     * <p>🎯 功能描述：二元组合函数的变体，参数可支持延迟执行功能，执行流程如下
     * 1. 先执行两个 supplier 得到第一输出 f 和第二输出 s
     * 2. 后续步骤和标准二元函数一致
     * 该方法为并行执行，第一结果和第二结果互不影响的模式，最终得到合并之后的结果
     *
     * <p>🏗️ 设计理由：
     * • ⚡ 并行执行：两个 Supplier 并行执行
     * • 🔧 延迟执行：支持按需执行
     * • 🎯 独立处理：两个结果互不影响
     *
     * <p>🎨 使用场景：
     * • 并行数据获取（并行获取两个独立数据源）
     * • 独立服务调用（调用两个独立的异步服务）
     * • 无依赖合并（两个独立异步操作的合并）
     *
     * @param supplierF  🔧 Supplier<Future<F>> 输入的异步结果执行函数，结果内是 F
     * @param supplierS  🔧 Supplier<Future<S>> 输入的异步结果执行函数，结果内是 S
     * @param combinerOf 🔧 BiFunction<F, S, Future<T>> 组合函数，输入为 F 和 S，输出为 Future<T>
     * @param <F>        💾 第一个异步结果 F
     * @param <S>        💾 第二个异步结果 S
     * @param <T>        🎯 组合函数的最终执行结果 T
     *
     * @return Future<T> 🌟 返回执行过的结果
     */
    public static <F, S, T> Future<T> combineT(final Supplier<Future<F>> supplierF, final Supplier<Future<S>> supplierS,
                                               final BiFunction<F, S, Future<T>> combinerOf) {
        return FnCombine.combineT(supplierF, supplierS, combinerOf);
    }

    // >>> 返回：Future<Boolean>
    // 内部调用 combineT，不关心结果，此处不做特殊注释说明

    /**
     * ✅ 组合函数 - 布尔结果版本 - List
     *
     * <p>🎯 功能描述：执行多个异步操作并返回布尔结果，不关心具体执行结果
     *
     * <p>🏗️ 设计理由：
     * • 🎯 简化结果：只关心操作是否完成
     * • ✅ 状态返回：返回操作成功状态
     * • 🔧 统一接口：提供布尔结果的统一接口
     *
     * @param futures List<Future<T>> 📤 输入的异步结果列表
     * @param <T>     💾 泛型类型
     *
     * @return Future<Boolean> 🌟 返回执行状态，成功为 true
     */
    public static <T> Future<Boolean> combineB(final List<Future<T>> futures) {
        return FnCombine.combineT(futures).compose(nil -> Future.succeededFuture(Boolean.TRUE));
    }

    /**
     * ✅ 组合函数 - 布尔结果版本 - Set
     *
     * <p>🎯 功能描述：执行多个异步操作并返回布尔结果，不关心具体执行结果
     *
     * <p>🏗️ 设计理由：
     * • 🎯 简化结果：只关心操作是否完成
     * • ✅ 状态返回：返回操作成功状态
     * • 🔗 唯一性保证：使用 Set 保证操作唯一性
     *
     * @param futures Set<Future<T>> 📤 输入的异步结果集合
     * @param <T>     💾 泛型类型
     *
     * @return Future<Boolean> 🌟 返回执行状态，成功为 true
     */
    public static <T> Future<Boolean> combineB(final Set<Future<T>> futures) {
        return FnCombine.combineT(futures).compose(nil -> Future.succeededFuture(Boolean.TRUE));
    }

    /**
     * ✅ 组合函数 - 布尔结果版本 - 同步集合 List
     *
     * <p>🎯 功能描述：对同步集合执行异步操作并返回布尔结果
     *
     * <p>🏗️ 设计理由：
     * • 🎯 同步转异步：将同步集合转换为异步处理
     * • ✅ 状态返回：返回操作完成状态
     * • ⚡ 并行处理：集合元素并行处理
     *
     * @param source      📥 输入的同步集合
     * @param generateFun 🔧 生成异步操作的函数
     * @param <I>         💾 输入类型
     * @param <T>         💾 中间类型
     *
     * @return Future<Boolean> 🌟 返回执行状态，成功为 true
     */
    public static <I, T> Future<Boolean> combineB(final List<I> source, final Function<I, Future<T>> generateFun) {
        final List<Future<T>> futures = new ArrayList<>();
        source.stream().map(generateFun).forEach(futures::add);
        return FnCombine.combineT(futures).compose(nil -> Future.succeededFuture(Boolean.TRUE));
    }

    /**
     * ✅ 组合函数 - 布尔结果版本 - 同步集合 Set
     *
     * <p>🎯 功能描述：对同步集合执行异步操作并返回布尔结果（去重）
     *
     * <p>🏗️ 设计理由：
     * • 🎯 同步转异步：将同步集合转换为异步处理
     * • ✅ 状态返回：返回操作完成状态
     * • 🔗 唯一性保证：使用 Set 保证操作唯一性
     *
     * @param source      📥 输入的同步集合
     * @param generateFun 🔧 生成异步操作的函数
     * @param <I>         💾 输入类型
     * @param <T>         💾 中间类型
     *
     * @return Future<Boolean> 🌟 返回执行状态，成功为 true
     */
    public static <I, T> Future<Boolean> combineB(final Set<I> source, final Function<I, Future<T>> generateFun) {
        final Set<Future<T>> futures = new HashSet<>();
        source.stream().map(generateFun).forEach(futures::add);
        return FnCombine.combineT(futures).compose(nil -> Future.succeededFuture(Boolean.TRUE));
    }
}
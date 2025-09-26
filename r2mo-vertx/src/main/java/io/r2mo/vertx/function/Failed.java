package io.r2mo.vertx.function;

import io.r2mo.function.Fn;

/**
 * 此处要和上层的 Fn 进行一个区分，在引入过程中，避免冲突
 * <pre>
 *     1. AMS -> {@link Fn} 标准抽象函数，最顶层函数
 *     2. Vertx -> {@link Failed} 结合 Vertx 进行的函数扩展
 *     Zero 框架层
 *     3. Zero AMS -> {@see HFn} 结合 Zero AMS 高阶进行函数扩展
 *     4. Runtime 运行时 -> {@see RFn} 结合 Runtime 进行的函数扩展
 * </pre>
 * 注：
 * - HFn 除了 Zero AMS 内部使用，外部可直接调用
 * 1. {@link Failed} 和 {@link Fn}
 * 2. {@see RFn} -> 它继承自 HFn
 *
 * @author lang : 2025-09-26
 */
public class Failed {
}

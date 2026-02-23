package io.r2mo.xync.weco;

import io.r2mo.base.exchange.UniMessage;
import io.r2mo.base.exchange.UniResponse;
import io.r2mo.function.Fn;
import io.r2mo.typed.cc.Cc;
import io.r2mo.typed.webflow.Akka;
import io.r2mo.typed.webflow.AkkaOf;

/**
 * 微信/企微 认证动作的底层执行接口 (Command 抽象)
 * <p>
 * 每个具体的 WeCoActionType 都会有一个实现该接口的类与之对应。
 * Service 客户端 (WxMpService/WxCpService) 应该通过构造函数注入并持有。
 * </p>
 *
 * @param <T> UniMessage 中 Payload 的类型 (如 String for Code, Void for QR code request)
 * @author lang : 2025-12-10
 */
public interface WeCoAction<T> {

    String ACTION_CHAT_STATUS = "WeCoAction/Chat/STATUS";

    Cc<String, WeCoAction<?>> CC_ACTION = Cc.openThread();

    /**
     * 执行具体的微信/企微操作命令。
     *
     * @param request 封装了 Header (如 redirectUri) 和 Payload (如 Code) 的请求消息。
     * @return 包含操作结果的 UniResponse。
     * @throws Exception 操作可能抛出的异常 (如网络错误、参数缺失、微信API调用失败)。
     */
    default UniResponse execute(final UniMessage<T> request) throws Exception {
        return null;
    }

    /**
     * 新版桥接 Spring 和 Vertx，如果是 Spring 环境直接调用 execute，无需包装成 Akka。但若遇到 Vertx 环境，则底层实现要求
     * 执行一次额外分流调用 executeAsync 方法，让异步模式穿透执行，其中
     * <pre>
     *     1. 实现层提供核心实现 -> 内置调用 Session 部分
     *     2. 抽象层 Provider 以异步调用流程为主导
     * </pre>
     *
     * @param request 封装了 Header (如 redirectUri) 和 Payload (如 Code) 的请求消息。
     * @return 包含操作结果的 UniResponse，包装在 Akka 中以适应异步执行环境。
     */
    default Akka<UniResponse> executeAsync(final UniMessage<T> request) {
        // 核心拦截专用方法
        return AkkaOf.of(Fn.jvmOr(() -> this.execute(request)));
    }
}
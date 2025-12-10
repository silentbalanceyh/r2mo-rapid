package io.r2mo.xync.weco;

import io.r2mo.base.exchange.UniMessage;
import io.r2mo.base.exchange.UniResponse;
import io.r2mo.typed.cc.Cc;

/**
 * 微信/企微 认证动作的底层执行接口 (Command 抽象)
 * <p>
 * 每个具体的 WeCoActionType 都会有一个实现该接口的类与之对应。
 * Service 客户端 (WxMpService/WxCpService) 应该通过构造函数注入并持有。
 * </p>
 *
 * @param <T> UniMessage 中 Payload 的类型 (如 String for Code, Void for QR code request)
 *
 * @author lang : 2025-12-10
 */
public interface WeCoAction<T> {

    Cc<String, WeCoAction<?>> CC_ACTION = Cc.openThread();

    /**
     * 执行具体的微信/企微操作命令。
     *
     * @param request 封装了 Header (如 redirectUri) 和 Payload (如 Code) 的请求消息。
     *
     * @return 包含操作结果的 UniResponse。
     * @throws Exception 操作可能抛出的异常 (如网络错误、参数缺失、微信API调用失败)。
     */
    UniResponse execute(final UniMessage<T> request) throws Exception;

}
package io.r2mo.base.exchange;

/**
 * @author lang : 2025-12-05
 */
public interface UniProvider {

    String channel();                                   // 渠道标识

    Class<? extends UniCredential> credentialType();    // 凭证类型

    /**
     * 【单向投递】 (Fire and Forget)
     * <p>
     * 适用于：发送短信、发送邮件、推送通知等不需要即时业务回执的场景。
     * 调用成功仅代表“提交成功”，不代表“最终送达”。
     * </p>
     *
     * @param account 发送账号 (e.g. 短信签名、发件人邮箱)
     * @param message 消息封装 (含接收人、内容、模版参数)
     * @param context 环境上下文 (Host/Port, 超时配置, 代理等)
     *
     * @return 上游消息ID (Upstream Message ID) - 用于后续在日志或回调中追踪消息状态
     */
    String send(UniAccount account, UniMessage<?> message, UniContext context);

    /**
     * 【双向交换】 (Request / Response)
     * 此时不再需要传入期望的返回类型，返回一个通用的 UniResponse 容器。
     * 由调用方拿到 Response 后，自己决定如何解析 content。
     *
     * @param account 操作账号
     * @param request 请求封装
     * @param context 环境上下文
     *
     * @return 统一响应容器
     */
    UniResponse exchange(UniAccount account, UniMessage<?> request, UniContext context);
}

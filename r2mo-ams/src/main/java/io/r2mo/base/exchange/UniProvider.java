package io.r2mo.base.exchange;

import io.r2mo.spi.SPI;
import io.r2mo.typed.cc.Cc;
import io.r2mo.typed.exception.web._501NotSupportException;
import io.r2mo.typed.json.JObject;

import java.util.Map;
import java.util.function.Supplier;

/**
 * @author lang : 2025-12-05
 */
public interface UniProvider {

    Cc<String, UniProvider.Wait<?>> CC_WAIT = Cc.openThread();

    @SuppressWarnings("unchecked")
    static <T> UniProvider.Wait<T> waitFor(final Supplier<Wait<T>> constructorFn) {
        return (UniProvider.Wait<T>) CC_WAIT.pick(constructorFn::get, String.valueOf(constructorFn.hashCode()));
    }

    static JObject replySuccess(final String messageId) {
        final JObject resultJ = SPI.J();
        resultJ.put("id", messageId);
        resultJ.put("success", Boolean.TRUE);
        return resultJ;
    }

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
    default String send(final UniAccount account, final UniMessage<?> message, final UniContext context) {
        throw new _501NotSupportException("[ R2MO ] 当前 Provider 不支持此方法！/ send");
    }

    /**
     * 此时不再需要传入期望的返回类型，返回一个通用的 UniResponse 容器。
     * 由调用方拿到 Response 后，自己决定如何解析 content。
     *
     * @param account 操作账号
     * @param request 请求封装
     * @param context 环境上下文
     *
     * @return 统一响应容器
     */
    default UniResponse exchange(final UniAccount account, final UniMessage<?> request, final UniContext context) {
        throw new _501NotSupportException("[ R2MO ] 当前 Provider 不支持此方法！/ exchange");
    }

    /**
     * 转换器，用于将输入的数据转换为对应的发送对象，为就绪专用组件
     * <pre>
     *     - CONFIG：配置对象类型
     *     - JObject：输入的数据
     * </pre>
     *
     * @author lang : 2025-12-07
     */
    interface Wait<CONFIG> {
        /**
         * 构造发送者账号
         * <pre>
         *     1. 账号提取带有优先级
         *     2. 账号按服务器中的 EmailDomain 配置进行构造（不构造多余）
         * </pre>
         *
         * @param params 参数
         * @param config 配置
         *
         * @return 账号
         */
        UniAccount account(JObject params, CONFIG config);

        /**
         * 构造上下文：发送上下文，无选择
         * <pre>
         *     1. 上下文按服务器中的 EmailDomain 配置进行构造
         *     2. 发送上下文基于 SMTP 等发送协议
         * </pre>
         *
         * @param params 参数
         * @param config 配置
         *
         * @return 上下文
         */
        UniContext context(JObject params, CONFIG config);

        /**
         * 构造上下文：接收上下文（二选一）
         * <pre>
         *     1. 上下文按服务器中的 EmailDomain 配置进行构造
         *     2. 接收上下文基于 POP3/IMAP （二选一）
         * </pre>
         *
         * @param params 参数
         * @param config 配置
         *
         * @return 上下文
         */
        UniContext contextClient(JObject params, CONFIG config);

        /**
         * 构造消息对象
         *
         * @param params 参数信息
         * @param header 头部信息
         * @param config 配置信息
         *
         * @return 消息对象
         */
        UniMessage<String> message(JObject params, Map<String, Object> header,
                                   CONFIG config);

        default UniMessage<String> message(final JObject params, final CONFIG config) {
            return this.message(params, Map.of(), config);
        }
    }
}

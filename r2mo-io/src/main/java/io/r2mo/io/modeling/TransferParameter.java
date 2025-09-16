package io.r2mo.io.modeling;

import io.r2mo.base.io.HPath;
import io.r2mo.base.io.transfer.TransferType;
import io.r2mo.io.enums.TransferOf;
import io.r2mo.typed.json.JObject;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author lang : 2025-09-16
 */
@Data
@Builder
@Accessors(fluent = true)
public class TransferParameter implements Serializable {
    /**
     * 此处的 HPath 比较特殊，她是一个存储路径规范，并且是接口，对于不同的存储介质，可以有不同的实现。在分布式系统环境中它的核心逻辑在于
     * <pre>
     *     1. 定位存储的主机
     *        {@link HPath#scheme()} - 协议
     *        {@link HPath#host()} - 主机
     *        {@link HPath#port()} - 端口
     *     2. 定位存储的基础位置
     *        {@link HPath#context()} - 上下文
     *     3. 安全需求
     *        {@link HPath#account()} - 账号信息
     * </pre>
     * 而开放出来的两个核心方法表示
     * <pre>
     *     {@link HPath#ioHome()} - 定位存储的根目录
     *     {@link HPath#ioPwd()} - 当前目录
     *     io 前缀方法表示本地转换（这种只适用于 Local 的模式）
     * </pre>
     * 实际应用过程中，部分内容需要后期绑定，典型如 HOME 的后期绑定，主要在于
     * <pre>
     *     A 服务在 A 机器，当数据传入到 B 中时，它才开始计算 HOME，而不是在 A 中直接计算 HOME
     *     但上述场景是极端的本地环境，如果是分布式环境下，HOME 可能是一个远程存储路径而不是本地路径
     * </pre>
     */
    private HPath stored;
    private TransferOf transferOf;
    private TransferType transferType;
    private JObject data;
}

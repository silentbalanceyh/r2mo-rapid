package io.r2mo.base.exchange;

import java.io.Serializable;

/**
 * 统一响应契约
 * 负责承载上游返回的原始数据，并提供“按需解析”的能力。
 *
 * @author lang : 2025-12-05
 */
public interface UniResponse extends Serializable {

    /**
     * 是否交互成功
     * (通常指业务层面的成功，如 HTTP 200 且 errcode=0)
     */
    boolean success();

    /**
     * 响应消息 / 错误描述
     */
    String message();

    /**
     * 【核心】获取业务数据 (延迟解析)
     * 将原始响应体转换为指定的类型 T。
     *
     * @param type 目标类型的 Class (e.g. String.class, UserToken.class)
     * @param <T>  泛型标记
     *
     * @return 转换后的对象，如果转换失败或数据为空可能抛出异常或返回 null
     */
    <T> T content(Class<T> type);

    /**
     * 获取原始响应数据 (便于日志记录或排错)
     * 通常返回 String (JSON/XML) 或 byte[] (文件流)
     *
     * @return 原始载体
     */
    Object raw();

    /**
     * 获取扩展元数据
     * (e.g. HTTP Headers, RequestId)
     *
     * @param key 元数据键
     *
     * @return 值 (不存在则返回 null)
     */
    Object meta(String key);
}

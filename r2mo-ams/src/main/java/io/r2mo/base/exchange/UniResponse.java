package io.r2mo.base.exchange;

import io.r2mo.typed.exception.WebException;

import java.io.Serializable;

/**
 * 统一响应契约
 * 负责承载上游返回的原始数据，并提供“按需解析”的能力。
 *
 * @author lang : 2025-12-05
 */
public interface UniResponse extends Serializable {

    static UniResponse success(final Object content) {
        return new NormResponse(content);
    }

    static UniResponse failure(final WebException ex) {
        return new NormResponse(ex);
    }

    /**
     * 是否交互成功
     * (通常指业务层面的成功，如 HTTP 200 且 errcode=0)
     */
    boolean isSuccess();

    /**
     * 响应消息 / 错误描述
     */
    String message();

    /**
     * 获取原始响应数据 (便于日志记录或排错)
     * 通常返回 String (JSON/XML) 或 byte[] (文件流)
     *
     * @return 原始载体
     */
    Object content();


    /**
     * 获取扩展元数据
     * (e.g. HTTP Headers, RequestId)
     *
     * @param key 元数据键
     *
     * @return 值 (不存在则返回 null)
     */
    Object meta(String key);

    UniResponse meta(String key, Object value);
}

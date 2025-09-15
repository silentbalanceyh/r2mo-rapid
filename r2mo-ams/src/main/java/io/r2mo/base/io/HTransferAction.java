package io.r2mo.base.io;

import io.r2mo.base.io.enums.TransferResult;

/**
 * @author lang : 2025-09-16
 */
public interface HTransferAction {
    /**
     * 取消传输
     *
     * @param token 令牌
     *
     * @return 取消结果
     */
    TransferResult cancel(String token);

    /**
     * 完成传输
     *
     * @param token 令牌
     *
     * @return 完成结果
     */
    TransferResult complete(String token);
}

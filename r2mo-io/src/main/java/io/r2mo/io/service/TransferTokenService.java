package io.r2mo.io.service;

import io.r2mo.base.io.HTransferService;
import io.r2mo.base.io.enums.TransferResult;
import io.r2mo.io.modeling.TransferRequest;
import io.r2mo.io.modeling.TransferToken;
import io.r2mo.typed.exception.web._501NotSupportException;
import io.r2mo.typed.json.JObject;

/**
 * <pre>
 *     - RESP initialize(REQ request);      // 生成令牌
 *     - List<DATA> data(String token);     // 解析令牌得到令牌中的数据
 *     - List<DATA> data(UUID id);          // 根据 {@link TransferToken} 的 id 获取令牌中的数据
 *     等待重写
 *     - default TransferResult cancel(final String token) {...}
 *     - default TransferResult complete(final String token) {...}
 * </pre>
 *
 * @author lang : 2025-09-16
 */
public interface TransferTokenService extends HTransferService<TransferRequest, TransferToken, JObject> {

    @Override
    default TransferResult cancel(final String token) {
        throw new _501NotSupportException("[ R2MO ] 不支持此操作 / cancel " + this.getClass());
    }

    @Override
    default TransferResult complete(final String token) {
        throw new _501NotSupportException("[ R2MO ] 不支持此操作 / complete " + this.getClass());
    }


    /**
     * 验证令牌
     *
     * @param token 令牌字符串
     *
     * @return 令牌对象
     */
    TransferToken runValidate(String token);

    /**
     * 查找令牌
     *
     * @param token 令牌字符串
     *
     * @return 令牌对象
     */
    TransferToken getToken(String token);

    /**
     * 吊销令牌
     *
     * @param token 令牌字符串
     *
     * @return 是否成功
     */
    boolean runRevoke(String token);

    /**
     * 延长令牌有效期
     *
     * @param token         令牌字符串
     * @param expireSeconds 延长的秒数
     *
     * @return 是否成功
     */
    boolean runExtend(String token, long expireSeconds);
}

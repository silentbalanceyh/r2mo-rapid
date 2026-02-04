package io.r2mo.jaas.token;

import io.r2mo.jaas.session.UserAt;
import io.r2mo.typed.common.Kv;
import io.r2mo.typed.webflow.Akka;

/**
 * @author lang : 2025-11-12
 */
public interface TokenBuilder {

    /**
     * 此处执行两个流程
     * <pre>
     *     1. 验证 Token 有效性
     *     2. 解析 Token 获取用户ID
     * </pre>
     * 如果是一次性 Basic 的模式，则不支持此操作，返回 null 即可
     *
     * @param token 处理指定的 Token 字符串
     * @return 用户ID
     */
    default Akka<String> accessOf(final String token) {
        return null;
    }

    Akka<String> accessOf(UserAt userAt);

    default Akka<String> refreshOf(final UserAt userAt) {
        throw new UnsupportedOperationException("[ R2MO ] 不支持的 Token 刷新操作");
    }

    default Akka<Kv<String, TokenType>> tokenOf(final String token) {
        return null;
    }
}

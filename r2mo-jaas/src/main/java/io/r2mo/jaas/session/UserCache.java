package io.r2mo.jaas.session;

import io.r2mo.jaas.enums.TypeID;
import io.r2mo.spi.SPI;
import io.r2mo.typed.common.Kv;
import io.r2mo.typed.exception.web._404NotFoundException;

import java.util.Objects;
import java.util.UUID;

/**
 * 设置缓存架构，如果有必要的话，设置分布式缓存来支持多个场景，缓存有几个
 * <pre>
 *     1. 用户上下文级的缓存
 *        账号 x 1 / 员工 x N
 *     2. 用户会话级的缓存
 *        账号 x 1 / 员工 x 1
 *     3. 用户向量级的缓存
 *        attribute x N, user id x 1
 *     4. 验证码专用缓存
 *        key x 1 / code x 1
 * </pre>
 *
 * @author lang : 2025-11-12
 */
public interface UserCache {

    String DEFAULT_NAME = "UserCache/SPI";

    String NAME_AUTHORIZE = "CACHE_AUTHORIZE";  // 前缀
    String NAME_AT = "CACHE_USER_AT";
    String NAME_CONTEXT = "CACHE_USER_CONTEXT";
    String NAME_VECTOR = "CACHE_USER_VECTOR";

    static UserCache of() {
        return of(DEFAULT_NAME);
    }

    static UserCache of(final String name) {
        // 查找缓存的相关信息，当前环境中先使用 SPI 直接查找
        final UserCache found = SPI.findOne(UserCache.class, name);
        if (Objects.isNull(found)) {
            throw new _404NotFoundException("[ R2MO ] 未找到匹配的缓存提供者：SPI = " + name);
        }
        return found;
    }

    void login(UserContext context);

    void login(UserAt userAt);

    void logout(UUID userId);

    UserContext context(UUID id);

    UserAt find(String idOr);

    UserAt find(UUID id);

    void authorize(Kv<String, String> generated, TypeID type);

    void authorize(String consumerId, TypeID type);
}

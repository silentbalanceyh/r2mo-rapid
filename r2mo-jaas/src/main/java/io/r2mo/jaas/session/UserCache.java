package io.r2mo.jaas.session;

import io.r2mo.jaas.enums.TypeID;
import io.r2mo.spi.SPI;
import io.r2mo.typed.cc.Cc;
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

    String NAME_AUTHORIZE = "CACHE_AUTHORIZE";
    String NAME_TOKEN = "CACHE_TOKEN";
    String NAME_REFRESH = "CACHE_REFRESH";
    // 前缀
    String NAME_USER_AT = "CACHE_USER_AT";
    String NAME_USER_CONTEXT = "CACHE_USER_CONTEXT";
    String NAME_USER_VECTOR = "CACHE_USER_VECTOR";

    Cc<String, UserCache> CC_SKELETON = Cc.openThread();

    /**
     * 此处的 SPI 是按照优先级查找的，默认实现
     * <pre>
     *     1. AuthUserCase
     *        - r2mo-spring-security 提供，priority = 0
     *     2. 如果存在其他实现，直接定义 priority > 0 即可
     * </pre>
     *
     * @return 缓存提供者
     */
    static UserCache of() {
        return CC_SKELETON.pick(() -> {
            // 查找缓存的相关信息，当前环境中先使用 SPI 直接查找
            final UserCache found = SPI.findOneOf(UserCache.class);
            if (Objects.isNull(found)) {
                throw new _404NotFoundException("[ R2MO ] 未找到匹配的缓存提供者：SPI");
            }
            return found;
        });
    }

    // ----- 账号部分专用缓存
    void login(UserContext context);

    void login(UserAt userAt);

    void logout(UUID userId);

    UserContext context(UUID id);

    UserAt find(String idOr);

    UserAt find(UUID id);

    // ----- 临时验证码（授权码）专用缓存
    void authorize(Kv<String, String> generated, TypeID type);

    void authorize(String consumerId, TypeID type);

    // ----- 令牌部分专用缓存
    // --- Access Token ---
    // 存储 Access Token -> UserId 映射
    void token(String token, UUID userId);

    // 查找 Access Token 对应的 UserId
    UUID token(String token);

    @SuppressWarnings("all")
    boolean tokenKo(String token);

    // --- Refresh Token ---
    // 存储 Refresh Token -> UserId 映射
    void tokenRefresh(String refreshToken, UUID userId);

    // 查找 Refresh Token 对应的 UserId
    UUID tokenRefresh(String refreshToken);

    @SuppressWarnings("all")
    boolean tokenRefreshKo(String refreshToken);
}

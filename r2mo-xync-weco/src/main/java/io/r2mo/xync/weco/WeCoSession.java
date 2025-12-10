package io.r2mo.xync.weco;

import java.time.Duration;

/**
 * 微信/企微 扫码会话状态存储 SPI 接口
 * * 职责：存储和获取扫码登录流程中的临时会话状态 (UUID -> OpenID)。
 */
public interface WeCoSession {

    int MAX_EXPIRE_SECONDS = 2592000; // 30天
    /** Redis 缓存 Key 前缀 */
    String CACHE_KEY_PREFIX = "weco:scan:"; // 接口静态常量

    /**
     * 统一生成扫码登录的 Redis Key
     *
     * @param uuid 会话 UUID
     *
     * @return 完整的 Redis Key
     */
    static String keyOf(final String uuid) { // 接口静态方法
        if (uuid == null || uuid.isEmpty()) {
            throw new IllegalArgumentException("UUID 不能为空，无法构建缓存 Key");
        }
        return CACHE_KEY_PREFIX + uuid;
    }

    /**
     * 保存或更新一个扫码会话的状态。
     *
     * @param uuid      会话唯一标识 (scence_str)
     * @param statusOr  会话状态 (e.g., WeCoConstant.STATUS_WAITING) 或 OpenID
     * @param expiredAt 时间
     */
    void save(String uuid, String statusOr, Duration expiredAt);

    /**
     * 获取指定 UUID 的会话状态。
     *
     * @param uuid 会话唯一标识
     *
     * @return 存储的状态 (可能是状态常量，也可能是 OpenID)
     */
    String get(String uuid);

    /**
     * 移除指定的会话。
     * 扫码登录成功后，可调用此方法清理临时状态。
     *
     * @param uuid 会话唯一标识
     */
    void remove(String uuid);
}

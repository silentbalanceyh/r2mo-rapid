package io.r2mo.jaas.token;

/**
 * Token 类型嗅探器
 * 用于在 UnifiedAuthenticationHandler 中快速决定分发策略
 *
 * @author lang : 2025-11-12
 */
public enum TokenType {
    /* JWT: Bearer header, 3 parts separated by dots */
    JWT,

    /* AES: Bearer header, Custom Encrypted String (No dots usually) */
    AES,

    /* BASIC: Basic header */
    BASIC,

    /* OAUTH2: Bearer header, Random String (Conflict with AES via format) */
    OPAQUE,

    /* DPoP: DPoP header */
    DPOP;

    /**
     * 静态常量，避免重复创建字符串
     */
    private static final String PREFIX_BASIC = "Basic ";
    private static final String PREFIX_BEARER = "Bearer ";
    private static final String PREFIX_DPOP = "DPoP ";

    /**
     * 根据 HTTP Authorization 头判断 Token 类型
     *
     * @param authorization HTTP Header Value (e.g., "Bearer eyJhbGci...")
     * @return TokenType or null if format is invalid
     */
    public static TokenType fromString(final String authorization) {
        if (authorization == null || authorization.isEmpty()) {
            return null;
        }

        // 1. 预处理：去除首尾空格 (防呆设计)
        final String raw = authorization.trim();

        // 2. 判断 Basic Auth
        // 格式: Basic <base64>
        if (isPrefix(raw, PREFIX_BASIC)) {
            return BASIC;
        }

        // 3. 判断 DPoP
        // 格式: DPoP <token>
        if (isPrefix(raw, PREFIX_DPOP)) {
            return DPOP;
        }

        // 4. 判断 Bearer 体系 (JWT / AES / OPAQUE)
        // 格式: Bearer <token>
        if (isPrefix(raw, PREFIX_BEARER)) {
            final String tokenPart = raw.substring(PREFIX_BEARER.length()).trim();
            if (tokenPart.isEmpty()) {
                return null;
            }
            return sniffBearerType(tokenPart);
        }

        // 5. 未知格式
        return null;
    }

    /**
     * 忽略大小写的前缀判断 (兼容部分不规范客户端)
     */
    private static boolean isPrefix(final String content, final String prefix) {
        return content.regionMatches(true, 0, prefix, 0, prefix.length());
    }

    /**
     * 嗅探 Bearer Token 的具体载荷类型
     * 核心逻辑：区分 JWT 和 AES
     */
    private static TokenType sniffBearerType(final String token) {
        /*
         * 🟢 JWT 特征判断：
         * 标准 JWT 由三部分组成：Header.Payload.Signature
         * 必须包含且仅包含 2 个 '.' (点号)
         */
        int dotCount = 0;
        for (int i = 0; i < token.length(); i++) {
            if (token.charAt(i) == '.') {
                dotCount++;
                // 如果超过2个点，肯定不是标准 JWT (可能是脏数据)
                if (dotCount > 2) {
                    break;
                }
            }
        }

        if (dotCount == 2) {
            return JWT;
        }

        /*
         * 🟢 AES vs OPAQUE 判断：
         * 在当前架构下，非 JWT 的 Bearer Token 默认为 AES。
         *
         * 如果未来需要区分 OPAQUE (如 GitHub 的 token)，建议引入特定前缀规则。
         * 例如：AES token 总是以 "aes:" 开头，或者 OPAQUE 总是 UUID 格式。
         *
         * 目前基于你的 "三合一" 需求，直接返回 AES。
         */
        return AES;
    }
}
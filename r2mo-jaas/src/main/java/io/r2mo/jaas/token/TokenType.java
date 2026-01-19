package io.r2mo.jaas.token;

import java.util.regex.Pattern;

/**
 * <pre>
 * 🟢 Token 类型嗅探器 (增强版)
 *
 * 1. 🌐 全局说明
 * 用于在网关层 (`UnifiedAuthenticationHandler`) 快速识别 HTTP 请求头中的 Token 类型。
 * 采用 "特征优先 + 正则匹配" 的混合策略，大幅提高识别准确率。
 *
 * 2. 🎯 核心策略 (Sniffing Strategy)
 * - P0 (最高优先级): 显式前缀匹配 (如 "r2a_", "Basic ").
 * - P1 (高优先级): JWT 魔术头匹配 ("eyJ").
 * - P2 (中优先级): JWT 结构正则匹配 (Base64Url + Dot 分隔).
 * - P3 (兜底): 默认为 AES/Opaque.
 * </pre>
 *
 * @author lang : 2025-11-12
 */
public enum TokenType {
    JWT,
    AES,
    BASIC,
    OPAQUE,
    DPOP;

    // ================== 常量定义 ==================

    /**
     * AES Token 的特定前缀 (最高权重)
     */
    public static final String TOKEN_PREFIX_AES = "r2a_";

    /**
     * JWT 的魔术头 (Base64Url 编码的 '{"' )
     */
    private static final String MAGIC_JWT = "eyJ";

    private static final String PREFIX_BASIC = "Basic ";
    private static final String PREFIX_BEARER = "Bearer ";
    private static final String PREFIX_DPOP = "DPoP ";

    /**
     * JWT 增强正则检查
     * 规则：
     * 1. 由 Base64Url 字符组成 (A-Z, a-z, 0-9, -, _)
     * 2. 通过点号 (.) 分隔
     * 3. 至少有 2 部分 (Header.Payload)，通常 3 部分，JWE 可能 5 部分
     * * Regex 解释:
     * ^              : 开始
     * [\\w-]+        : 第一部分 (Header)
     * \\.            : 点分隔
     * [\\w-]+        : 第二部分 (Payload)
     * (?:\\.[\\w-]*)?: 可选的第三部分 (Signature，可能为空)
     * (?:\\.[\\w-]+)*: 兼容 JWE 等更多部分
     * $              : 结束
     */
    private static final Pattern PATTERN_JWT = Pattern.compile("^[\\w-]+\\.[\\w-]+(?:\\.[\\w-]*)*(?:\\.[\\w-]+)*$");

    // ================== 公开方法 ==================

    /**
     * 根据 HTTP Authorization 头判断 Token 类型
     */
    public static TokenType fromString(final String authorization) {
        if (authorization == null || authorization.isEmpty()) {
            return null;
        }

        final String raw = authorization.trim();

        // 1. Basic Auth
        if (isPrefix(raw, PREFIX_BASIC)) {
            return BASIC;
        }

        // 2. DPoP
        if (isPrefix(raw, PREFIX_DPOP)) {
            return DPOP;
        }

        // 3. Bearer 体系 (核心分流)
        if (isPrefix(raw, PREFIX_BEARER)) {
            final String tokenPart = raw.substring(PREFIX_BEARER.length()).trim();
            if (tokenPart.isEmpty()) {
                return null;
            }
            return sniffBearerType(tokenPart);
        }

        return null;
    }

    // ================== 私有核心逻辑 ==================

    private static boolean isPrefix(final String content, final String prefix) {
        return content.regionMatches(true, 0, prefix, 0, prefix.length());
    }

    /**
     * 🟢 深度嗅探 Bearer 载荷
     */
    private static TokenType sniffBearerType(final String token) {
        // Step 1: ⚡ 绝对特征匹配 (AES 前缀)
        // 如果以 "r2a_" 开头，100% 是 AES，无需后续检查
        if (token.startsWith(TOKEN_PREFIX_AES)) {
            return AES;
        }

        // Step 2: ⚡ JWT 魔术头匹配 (快速通道)
        // 99.9% 的 JWT Header 都是 {"alg":... 开头
        // Base64Url 编码后必然以 "eyJ" 开头
        if (token.startsWith(MAGIC_JWT)) {
            return JWT;
        }

        // Step 3: 🔍 JWT 结构正则校验 (兼容通道)
        // 处理那些不以 "eyJ" 开头（极少见，如压缩 Header）但符合 JWT 结构的 Token
        // 或者处理你提供的这种 "缺少签名部分" 的 Token
        if (isJwtStructure(token)) {
            return JWT;
        }

        // Step 4: 🏳️ 兜底策略
        // 既没有 AES 前缀，也不像 JWT，那它只能是 AES (自定义加密串) 或 Opaque
        return AES;
    }

    /**
     * 检查是否符合 JWT 的字符集和结构
     * 能够识别：
     * - a.b.c (标准 JWT)
     * - a.b   (无签名 JWT / 截断 JWT) -> 你的例子符合这个
     * - a.b.c.d.e (JWE)
     */
    private static boolean isJwtStructure(final String token) {
        // 性能优化：先简单判断是否有点号，没有点号肯定不是 JWT，避免正则开销
        if (token.indexOf('.') == -1) {
            return false;
        }
        return PATTERN_JWT.matcher(token).matches();
    }
}
package io.r2mo.jaas.token;

/**
 * <pre>
 * 🟢 Token 类型嗅探器
 *
 * 1. 🌐 全局说明
 *    用于在网关层 (`UnifiedAuthenticationHandler`) 快速识别 HTTP 请求头中的 Token 类型。
 *    作为分流策略的核心依据，决定后续将请求路由给哪个具体的认证组件处理。
 *
 * 2. 🎯 核心功能
 *    - 格式识别：基于 HTTP Authorization 头的前缀（Basic/Bearer/DPoP）。
 *    - 深度嗅探：对于 Bearer 类型的 Token，进一步根据 payload 特征（如前缀、点号数量）区分 JWT、AES 或 Opaque。
 *
 * 3. 🧩 支持类型
 *    - JWT: 标准 JSON Web Token。
 *    - AES: 自定义对称加密令牌。
 *    - BASIC: HTTP 基础认证。
 *    - OPAQUE: 不透明令牌（如 OAuth2 引用令牌）。
 *    - DPOP: 应用层证明令牌。
 * </pre>
 *
 * @author lang : 2025-11-12
 */
public enum TokenType {
    /**
     * <pre>
     * 🛡️ JSON Web Token (JWT)
     * - Header: Bearer
     * - Format: xxxx.yyyy.zzzz (Base64Url, 2 dots)
     * - Usage: 无状态自包含认证。
     * </pre>
     */
    JWT,

    /**
     * <pre>
     * 🛡️ AES Symmetric Encryption Token
     * - Header: Bearer
     * - Format: r2a_xxxx... (Hex/Base64, no structure)
     * - Usage: 系统内部轻量级加密令牌。
     * </pre>
     */
    AES,

    /**
     * <pre>
     * 🛡️ HTTP Basic Authentication
     * - Header: Basic
     * - Format: base64(username:password)
     * - Usage: 简单的用户名密码认证。
     * </pre>
     */
    BASIC,

    /**
     * <pre>
     * 🛡️ Opaque Token (Transparent/Reference)
     * - Header: Bearer
     * - Format: Random string (no structure)
     * - Usage: OAuth2 引用令牌，需查库验证。
     * </pre>
     */
    OPAQUE,

    /**
     * <pre>
     * 🛡️ Demonstration of Proof-of-Possession (DPoP)
     * - Header: DPoP / Bearer
     * - Usage: 增强安全性的令牌绑定机制。
     * </pre>
     */
    DPOP;

    /**
     * AES Token 的特定前缀标识，用于快速区分 JWT 与 AES。
     */
    public static final String TOKEN_PREFIX_AES = "r2a_";

    /**
     * 静态常量：Basic 前缀
     */
    private static final String PREFIX_BASIC = "Basic ";
    /**
     * 静态常量：Bearer 前缀
     */
    private static final String PREFIX_BEARER = "Bearer ";
    /**
     * 静态常量：DPoP 前缀
     */
    private static final String PREFIX_DPOP = "DPoP ";

    /**
     * <pre>
     * 🟢 静态工厂：Token 类型解析
     *
     * 1. 🌐 使用场景
     *    接收原始的 HTTP Authorization Header 值，自动推断其 Token 类型。
     *
     * 2. 🧬 识别逻辑 (Pipeline)
     *    - Step 1: 预处理 (Trim & Null Check)。
     *    - Step 2: 匹配 `Basic` 前缀 -> {@link #BASIC}。
     *    - Step 3: 匹配 `DPoP` 前缀 -> {@link #DPOP}。
     *    - Step 4: 匹配 `Bearer` 前缀 -> 进入深度嗅探 {@link #sniffBearerType(String)}。
     *
     * 3. ⚖️ 判决依据
     *    区分 JWT 和 AES/Opaque 是难点，主要依赖 payload 的特征（. 的数量或特定前缀）。
     * </pre>
     *
     * @param authorization HTTP Authorization Header 的完整值 (e.g., "Bearer eyJhbGci...")
     * @return 识别出的 {@link TokenType}；若格式无法识别或输入无效则返回 null
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
     * <pre>
     * 🟢 内部工具：前缀匹配
     *
     * 忽略大小写地检查字符串前缀，以兼容非标准的客户端实现
     * (例如部分客户端可能发 "bearer " 小写)。
     * </pre>
     *
     * @param content 待检查的内容
     * @param prefix  预期的前缀
     * @return true 如果 content 以 prefix 开头 (无视大小写)
     */
    private static boolean isPrefix(final String content, final String prefix) {
        return content.regionMatches(true, 0, prefix, 0, prefix.length());
    }

    /**
     * <pre>
     * 🟢 内部工具：Bearer 载荷深度嗅探
     *
     * 用于区分Bearer后面的字符串具体是哪种 Token。
     *
     * 1. 🕵️‍♂️ 嗅探策略
     *    - 优先检查 AES 前缀 ({@link #TOKEN_PREFIX_AES}) -> {@link #AES}。
     *    - 检查 JWT 结构特征 (必须包含 2 个点号 `.`) -> {@link #JWT}。
     *    - 否则兜底为 {@link #AES} (视作不透明字符串或自定义加密串)。
     *
     * 2. ⚠️ 注意事项
     *    - 兜底策略选择 AES 是基于 Zero 系统假设。
     *    - 如果引入了 Opaque Token (Redis 存储)，此处可能需要调整返回 {@link #OPAQUE}。
     * </pre>
     *
     * @param token 去除 Bearer 前缀后的纯 Token 字符串
     * @return 最可能的 TokenType
     */
    private static TokenType sniffBearerType(final String token) {
        // 🟢 优先判断 AES (基于特定前缀)
        // 你的 TokenAESGenerator 定义了 "r2a_" 前缀
        if (token.startsWith(TOKEN_PREFIX_AES)) {
            return AES;
        }

        // 🟢 判断 JWT (标准：Header.Payload.Signature，共 2 个点)
        int dotCount = 0;
        for (int i = 0; i < token.length(); i++) {
            if (token.charAt(i) == '.') {
                dotCount++;
                if (dotCount > 2) { // 超过2个点肯定不是标准 JWT
                    break;
                }
            }
        }
        if (dotCount == 2) {
            return JWT;
        }

        // 🟢 剩下的归类为 OPAQUE (或者默认为 AES，取决于你的业务约定)
        // 考虑到你的 Gateway 逻辑中 Bearer 分流给了 AES，这里如果无法识别为 JWT 且无 AES 前缀，
        // 可以返回 OPAQUE 或 null。
        // *如果你的 AES Token 有旧版本没有 r2a_ 前缀，可以在这里做兼容逻辑*
        return AES; // 或者 OPAQUE
    }
}
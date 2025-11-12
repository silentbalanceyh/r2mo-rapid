package io.r2mo.spring.security.jwt.token;

import cn.dev33.satoken.exception.SaTokenException;
import cn.dev33.satoken.jwt.SaJwtUtil;
import io.r2mo.spring.security.config.ConfigSecurity;
import io.r2mo.spring.security.config.ConfigSecurityJwt;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 与 sa-token 集成后的 JWT 工具类（可编译版）
 * 依赖：sa-token-jwt
 *
 * @author lang
 */
@Component
@Slf4j
public class JwtTokenGenerator {

    // 标准 Claim
    private static final String NAME_SUBJECT = "sub";
    private static final String NAME_ISSUER = "iss";
    private static final String NAME_AUDIENCE = "aud";
    private static final String NAME_EXPIRE = "exp"; // 秒级时间戳
    private static final String NAME_ISSUED_AT = "iat"; // 秒级时间戳

    // 自定义扩展字段
    private static final String NAME_ADDON_DATA = "ext";
    private static final String NAME_LOGIN_TYPE = "default";

    @Autowired
    private ConfigSecurity config;

    /**
     * 生成 JWT Token
     *
     * @param identifier 作为 subject (sub) 的唯一标识
     * @param data       附加数据，放入 ext
     *
     * @return token 字符串；未启用 JWT 时返回 null
     */
    public String tokenGenerate(final String identifier, final Map<String, Object> data) {
        final ConfigSecurityJwt jwt = this.getConfiguration();
        if (Objects.isNull(jwt)) {
            return null;
        }
        // 组装 payload（注意 SaJwtUtil 会从全局配置中读取 jwtSecretKey 完成签名）
        final long nowSec = System.currentTimeMillis() / 1000;
        final long expSec = nowSec + (jwt.msExpiredAt() / 1000);

        final Map<String, Object> payload = new HashMap<>(8);
        payload.put(NAME_SUBJECT, identifier);
        payload.put(NAME_ISSUED_AT, nowSec);
        payload.put(NAME_EXPIRE, expSec);

        if (jwt.getIssuer() != null && !jwt.getIssuer().isEmpty()) {
            payload.put(NAME_ISSUER, jwt.getIssuer());
        }
        if (jwt.getAudience() != null && !jwt.getAudience().isEmpty()) {
            payload.put(NAME_AUDIENCE, jwt.getAudience());
        }
        if (data != null && !data.isEmpty()) {
            payload.put(NAME_ADDON_DATA, data);
        }

        try {
            return SaJwtUtil.createToken(payload, jwt.getSecretKey());
        } catch (final Exception e) {
            throw new RuntimeException("Failed to create JWT token", e);
        }
    }

    /**
     * 验证 Token
     *
     * @param token token 字符串
     *
     * @return true 有效；false 无效或未启用
     */
    public boolean tokenValidate(final String token) {
        final ConfigSecurityJwt jwt = this.getConfiguration();
        if (Objects.isNull(jwt)) {
            return false;
        }
        if (token == null || token.trim().isEmpty()) {
            return false;
        }

        try {
            // getPayloads 会校验签名与 exp
            final Map<String, Object> payload = SaJwtUtil.getPayloads(token, NAME_LOGIN_TYPE, jwt.getSecretKey());
            if (payload == null) {
                return false;
            }

            // 额外校验 issuer / audience（如配置）
            if (jwt.getIssuer() != null) {
                final Object tokenIssuer = payload.get(NAME_ISSUER);
                if (!jwt.getIssuer().equals(tokenIssuer)) {
                    return false;
                }
            }
            if (jwt.getAudience() != null) {
                final Object tokenAudience = payload.get(NAME_AUDIENCE);
                return Objects.equals(jwt.getAudience(), tokenAudience);
            }
            return true;
        } catch (final SaTokenException | IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * 提取 sub
     */
    public String tokenSubject(final String token) {
        final ConfigSecurityJwt jwt = this.getConfiguration();
        if (Objects.isNull(jwt)) {
            return null;
        }
        try {
            final Map<String, Object> payload = SaJwtUtil.getPayloads(token, NAME_LOGIN_TYPE, jwt.getSecretKey());
            if (payload == null) {
                return null;
            }
            final Object sub = payload.get(NAME_SUBJECT);
            return sub == null ? null : sub.toString();
        } catch (final Exception e) {
            return null;
        }
    }

    /**
     * 提取 ext
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> tokenData(final String token) {
        final ConfigSecurityJwt jwt = this.getConfiguration();
        if (Objects.isNull(jwt)) {
            return Map.of();
        }
        try {
            final Map<String, Object> payload = SaJwtUtil.getPayloads(token, NAME_LOGIN_TYPE, jwt.getSecretKey());
            if (payload == null) {
                return null;
            }
            final Object ext = payload.get(NAME_ADDON_DATA);
            if (ext instanceof Map<?, ?>) {
                // 安全转换
                return (Map<String, Object>) ext;
            }
            return null;
        } catch (final Exception e) {
            return null;
        }
    }

    private ConfigSecurityJwt getConfiguration() {
        if (Objects.isNull(this.config)) {
            return null;
        }
        if (!this.config.isJwt()) {
            return null;
        }
        return this.config.getJwt();
    }
}

package io.r2mo.spring.security.jwt;

import cn.dev33.satoken.jwt.StpLogicJwtForSimple;
import cn.dev33.satoken.stp.StpLogic;
import io.r2mo.jaas.element.MSUser;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * JWT Token 服务类 (基于 Sa-Token 实现)
 *
 * @author lang : 2025-11-11
 */
@Service
public class JwtTokenService {
    
    private final StpLogic stpLogic = new StpLogicJwtForSimple("login");

    /**
     * 生成 JWT Token
     *
     * @param user 用户信息
     * @return JWT Token
     */
    public String generateToken(MSUser user) {
        // 使用 Sa-Token 生成 JWT Token
        stpLogic.login(user.getId());
        return stpLogic.getTokenValue();
    }

    /**
     * 解析 JWT Token 获取用户ID
     *
     * @param token JWT Token
     * @return 用户ID
     */
    public UUID parseToken(String token) {
        try {
            // 使用 Sa-Token 解析 JWT Token
            Object loginId = stpLogic.getLoginIdByToken(token);
            if (loginId != null) {
                return UUID.fromString(loginId.toString());
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * 验证 JWT Token 是否有效
     *
     * @param token JWT Token
     * @return 是否有效
     */
    public boolean validateToken(String token) {
        try {
            // 使用 Sa-Token 验证 JWT Token
            return stpLogic.isLogin(token);
        } catch (Exception e) {
            return false;
        }
    }
}
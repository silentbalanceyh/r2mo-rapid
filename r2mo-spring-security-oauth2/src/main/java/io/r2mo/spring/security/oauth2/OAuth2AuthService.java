package io.r2mo.spring.security.oauth2;

import io.r2mo.jaas.auth.LoginRequest;
import io.r2mo.jaas.auth.LoginResponse;
import io.r2mo.jaas.element.MSUser;
import io.r2mo.jaas.enums.UserIDType;
import io.r2mo.spring.security.auth.AuthProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.stereotype.Service;

/**
 * OAuth2 认证服务实现
 *
 * @author lang : 2025-11-11
 */
@Service
public class OAuth2AuthService implements AuthProvider {
    
    @Autowired
    private OAuth2TokenService oauth2TokenService;
    
    // 这里应该注入实际的用户服务
    // private UserService userService;

    @Override
    public LoginResponse login(LoginRequest loginRequest) throws AuthenticationException {
        // 实际项目中这里应该调用用户服务验证用户凭据
        // MSUser user = userService.authenticate(loginRequest);
        
        // 临时创建一个测试用户
        MSUser user = new MSUser();
        user.setId(java.util.UUID.randomUUID());
        user.setUsername(loginRequest.getId());
        
        // 生成 OAuth2 Token
        OAuth2AccessToken accessToken = oauth2TokenService.generateAccessToken(user.getId());
        OAuth2RefreshToken refreshToken = oauth2TokenService.generateRefreshToken(user.getId());
        
        LoginResponse response = new LoginResponse();
        response.setToken(accessToken.getTokenValue());
        response.setRefreshToken(refreshToken.getTokenValue());
        response.setUserId(user.getId());
        response.setUsername(user.getUsername());
        response.setExpiredAt(accessToken.getExpiresAt().toEpochMilli());
        
        return response;
    }

    @Override
    public boolean supports(LoginRequest loginRequest) {
        // OAuth2 认证支持所有类型的登录请求，但通常需要特定的标识
        // 这里可以根据实际需求调整
        return true;
    }
}
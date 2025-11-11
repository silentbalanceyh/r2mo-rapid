package io.r2mo.spring.security.jwt;

import io.r2mo.jaas.auth.LoginRequest;
import io.r2mo.jaas.auth.LoginResponse;
import io.r2mo.jaas.element.MSUser;
import io.r2mo.jaas.enums.UserIDType;
import io.r2mo.spring.security.auth.AuthProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

/**
 * JWT 认证服务实现
 *
 * @author lang : 2025-11-11
 */
@Service
public class JwtAuthService implements AuthProvider {
    
    @Autowired
    private JwtTokenService jwtTokenService;
    
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
        
        // 生成 JWT Token
        String token = jwtTokenService.generateToken(user);
        
        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setUserId(user.getId());
        response.setUsername(user.getUsername());
        response.setExpiredAt(System.currentTimeMillis() + 86400000L); // 24小时后过期
        
        return response;
    }

    @Override
    public boolean supports(LoginRequest loginRequest) {
        // JWT 认证支持所有类型的登录请求，但通常需要特定的标识
        // 这里可以根据实际需求调整
        return true;
    }
}
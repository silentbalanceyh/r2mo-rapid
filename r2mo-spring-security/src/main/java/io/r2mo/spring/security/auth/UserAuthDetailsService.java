package io.r2mo.spring.security.auth;

import io.r2mo.jaas.session.UserAt;
import io.r2mo.typed.enums.TypeLogin;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @author lang : 2025-11-12
 */
@Service
@Slf4j
public class UserAuthDetailsService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(final String uniqueId) throws UsernameNotFoundException {
        // -- 关键：从上下文获取策略
        TypeLogin type = UserAuthContext.getStrategy();
        if (Objects.isNull(type)) {
            // -- 默认直接走账号密码
            type = TypeLogin.PASSWORD;
        }
        // -- 根据 type 查找匹配的 UserAtService
        final ServiceUserAt userProvider = ServiceFactory.of().userProvider(type);
        // -- 根据 标识符加载用户信息
        final UserAt userAt = userProvider.loadLogged(uniqueId);
        return new UserAuthDetails(userAt);
    }
}

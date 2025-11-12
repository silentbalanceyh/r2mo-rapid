package io.r2mo.spring.security.auth;

import io.r2mo.jaas.enums.TypeID;
import io.r2mo.jaas.session.UserAt;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @author lang : 2025-11-12
 */
@Service
public class UserDetailsCommon implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(final String uniqueId) throws UsernameNotFoundException {
        // -- 关键：从上下文获取策略
        TypeID type = UserDetailsContext.getStrategy();
        if (Objects.isNull(type)) {
            // -- 默认直接走账号密码
            type = TypeID.PASSWORD;
        }
        // -- 根据 type 查找匹配的 UserAtService
        final ServiceUserAt userProvider = ServiceFactory.of().userProvider(type);
        // -- 根据 标识符加载用户信息
        final UserAt userAt = userProvider.loadLogged(uniqueId);
        return new AuthUserDetail(userAt);
    }
}

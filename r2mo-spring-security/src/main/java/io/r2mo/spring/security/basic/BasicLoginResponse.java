package io.r2mo.spring.security.basic;

import io.r2mo.jaas.element.MSUser;
import io.r2mo.jaas.enums.TypeToken;
import io.r2mo.jaas.session.UserAt;
import io.r2mo.spring.security.token.TokenBuilder;
import io.r2mo.spring.security.token.TokenBuilderManager;
import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

/**
 * @author lang : 2025-11-11
 */
@Data
public class BasicLoginResponse implements Serializable {
    private String token;
    private UUID id;
    private String username;

    public BasicLoginResponse(final UserAt userAt) {
        final MSUser user = userAt.logged();
        this.id = user.getId();
        this.username = user.getUsername();
        // Token 构造器
        final TokenBuilder builder = TokenBuilderManager.of().getOrCreate(TypeToken.BASIC);
        this.token = builder.build(userAt);
    }
}

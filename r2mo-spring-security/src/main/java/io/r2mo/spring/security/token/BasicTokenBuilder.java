package io.r2mo.spring.security.token;

import io.r2mo.jaas.element.MSUser;
import io.r2mo.jaas.session.UserAt;
import io.r2mo.jaas.token.TokenBuilderBase;
import io.r2mo.typed.webflow.Akka;
import io.r2mo.typed.webflow.AkkaOf;

import java.util.Base64;

/**
 * @author lang : 2025-11-12
 */
public class BasicTokenBuilder extends TokenBuilderBase {

    @Override
    public Akka<String> accessOf(final UserAt userAt) {
        final MSUser user = userAt.logged();
        final String token = user.getUsername() + ":" + user.getPassword();
        return AkkaOf.of(Base64.getEncoder().encodeToString(token.getBytes()));
    }
}

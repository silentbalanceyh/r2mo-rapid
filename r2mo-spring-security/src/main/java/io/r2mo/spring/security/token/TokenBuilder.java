package io.r2mo.spring.security.token;

import io.r2mo.jaas.session.UserAt;

/**
 * @author lang : 2025-11-12
 */
public interface TokenBuilder {

    String build(UserAt userAt);
}

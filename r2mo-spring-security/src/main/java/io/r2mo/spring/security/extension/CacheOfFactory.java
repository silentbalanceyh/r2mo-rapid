package io.r2mo.spring.security.extension;

import io.r2mo.jaas.enums.TypeID;
import io.r2mo.jaas.session.UserAt;
import io.r2mo.jaas.session.UserContext;
import io.r2mo.typed.cc.CacheAt;

import java.util.UUID;

/**
 * @author lang : 2025-11-12
 */
public interface CacheOfFactory {

    CacheAt<UUID, UserAt> userAt();

    CacheAt<UUID, UserContext> userContext();

    CacheAt<String, UUID> userVector();

    CacheAt<String, UUID> ofToken();

    CacheAt<String, UUID> ofRefresh();

    CacheAt<String, String> ofAuthorize(TypeID type);
}

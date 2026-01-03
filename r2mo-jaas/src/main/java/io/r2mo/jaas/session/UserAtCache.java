package io.r2mo.jaas.session;

import io.r2mo.jaas.auth.CaptchaArgs;
import io.r2mo.typed.cc.CacheAt;

import java.util.UUID;

/**
 * @author lang : 2025-11-12
 */
public interface UserAtCache {

    CacheAt<UUID, UserAt> userAt();

    CacheAt<UUID, UserContext> userContext();

    CacheAt<String, UUID> userVector();

    CacheAt<String, UUID> ofToken();

    CacheAt<String, UUID> ofRefresh();

    CacheAt<String, String> ofAuthorize(CaptchaArgs configuration);
}

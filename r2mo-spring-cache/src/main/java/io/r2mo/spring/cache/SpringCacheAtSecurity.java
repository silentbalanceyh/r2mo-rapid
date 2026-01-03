package io.r2mo.spring.cache;

import io.r2mo.spring.security.SecurityUserAtCache;
import io.r2mo.typed.annotation.SPID;
import io.r2mo.typed.cc.CacheAt;

/**
 * @author lang : 2025-12-02
 */
@SPID(priority = 300)
public class SpringCacheAtSecurity extends SecurityUserAtCache {
    @Override
    protected <K, V> CacheAt<K, V> create(final String name, final Class<K> keyType, final Class<V> valueType) {
        return new SpringCacheAt<>(name, keyType, valueType);
    }
}

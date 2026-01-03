package io.r2mo.spring.security.extension.cache;

import io.r2mo.spring.security.SecurityUserAtCache;
import io.r2mo.typed.annotation.SPID;
import io.r2mo.typed.cc.CacheAt;

/**
 * @author lang : 2025-11-12
 */
@SPID
public class EhcacheCacheAtSecurity extends SecurityUserAtCache {

    @Override
    protected <K, V> CacheAt<K, V> create(final String name, final Class<K> clazzK, final Class<V> clazzV) {
        return new EhcacheCacheAt<>(name, clazzK, clazzV);
    }
}

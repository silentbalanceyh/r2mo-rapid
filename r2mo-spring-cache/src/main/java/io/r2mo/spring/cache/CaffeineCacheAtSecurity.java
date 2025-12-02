package io.r2mo.spring.cache;

import io.r2mo.spring.security.extension.cache.CacheAtSecurityBase;
import io.r2mo.typed.annotation.SPID;
import io.r2mo.typed.cc.CacheAt;

/**
 * @author lang : 2025-12-02
 */
@SPID(priority = 50)
public class CaffeineCacheAtSecurity extends CacheAtSecurityBase {
    @Override
    protected <K, V> CacheAt<K, V> create(final String name, final Class<K> keyType, final Class<V> valueType) {
        return new CaffeineCacheAt<>(name, keyType, valueType);
    }
}

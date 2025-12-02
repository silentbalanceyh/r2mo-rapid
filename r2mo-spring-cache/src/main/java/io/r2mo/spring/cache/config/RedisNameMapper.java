package io.r2mo.spring.cache.config;

import cn.hutool.core.util.StrUtil;
import org.redisson.api.NameMapper;

/**
 * @author lang : 2025-12-02
 */
public class RedisNameMapper implements NameMapper {
    private final String prefix;

    public RedisNameMapper(final String prefix) {
        this.prefix = StrUtil.isBlank(prefix) ? "" : prefix + ":";
    }

    @Override
    public String map(final String name) {
        if (StrUtil.isBlank(name)) {
            return null;
        }
        if (StrUtil.isNotBlank(this.prefix) && !name.startsWith(this.prefix)) {
            return this.prefix + name;
        }
        return name;
    }

    @Override
    public String unmap(final String name) {
        if (StrUtil.isBlank(name)) {
            return null;
        }
        if (StrUtil.isNotBlank(this.prefix) && !name.startsWith(this.prefix)) {
            return name.substring(this.prefix.length());
        }
        return name;
    }
}

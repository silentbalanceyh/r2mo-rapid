package io.r2mo.dbe.mybatisplus;

import com.github.yulichang.base.MPJBaseMapper;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author lang : 2025-10-23
 */
public class JoinProxy<T> {

    private final MPJBaseMapper<T> baseMapper;
    private final ConcurrentMap<Class<?>, MPJBaseMapper<?>> mapperMap = new ConcurrentHashMap<>();

    public JoinProxy(final MPJBaseMapper<T> baseMapper) {
        this.baseMapper = baseMapper;
    }

    public MPJBaseMapper<T> mapper() {
        return this.baseMapper;
    }

    public MPJBaseMapper<?> mapper(final Class<?> clazz) {
        return this.mapperMap.get(clazz);
    }

    public JoinProxy<T> mapper(final Class<?> entityCls, final MPJBaseMapper<?> mapper) {
        if (Objects.nonNull(mapper) && Objects.nonNull(entityCls)) {
            this.mapperMap.put(entityCls, mapper);
        }
        return this;
    }

    public boolean isReady(final Class<?> entityCls) {
        return this.mapperMap.containsKey(entityCls);
    }
}

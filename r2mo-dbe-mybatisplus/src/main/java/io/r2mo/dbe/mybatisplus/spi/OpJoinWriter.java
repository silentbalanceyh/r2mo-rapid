package io.r2mo.dbe.mybatisplus.spi;

import com.github.yulichang.base.MPJBaseMapper;
import com.github.yulichang.query.MPJQueryWrapper;
import io.r2mo.base.dbe.join.DBRef;
import io.r2mo.typed.json.JObject;

import java.io.Serializable;
import java.util.Set;

/**
 * @author lang : 2025-10-23
 */
class OpJoinWriter<T, M extends MPJBaseMapper<T>> extends OpJoinPre<T> {
    private final M executor;

    OpJoinWriter(final DBRef ref, final M executor) {
        super(ref);
        this.executor = executor;
    }

    public JObject create(final JObject latest) {
        final Set<Class<?>> entitySet = this.metaMap.keySet();
        return null;
    }

    public Boolean removeById(final Serializable id) {
        return null;
    }

    public Boolean removeBy(final MPJQueryWrapper<T> queryWrapper) {
        return null;
    }

    public JObject updateById(final Serializable id, final JObject latest) {
        return null;
    }

    public JObject update(final MPJQueryWrapper<T> queryWrapper, final JObject latest) {
        return null;
    }
}

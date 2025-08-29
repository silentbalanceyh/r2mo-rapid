package io.r2mo.dbe.mybatisplus;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.r2mo.typed.cc.Cc;

/**
 * @author lang : 2025-08-28
 */
public class DBE<T> extends io.r2mo.dbe.common.DBE<QueryWrapper<T>, T, BaseMapper<T>> {

    private static final Cc<String, DBE<?>> CCT_DBE = Cc.openThread();

    private DBE(final Class<T> entityCls, final BaseMapper<T> executor) {
        super(entityCls, executor);
    }

    @SuppressWarnings("all")
    public static <T> DBE<T> of(final Class<T> entityCls, final BaseMapper<T> executor) {
        final String cacheKey = entityCls.getName() + "@" + executor.hashCode();
        return (DBE<T>) CCT_DBE.pick(() -> new DBE<>(entityCls, executor), cacheKey);
    }
}

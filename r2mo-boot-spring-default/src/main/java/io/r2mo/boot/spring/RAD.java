package io.r2mo.boot.spring;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.yulichang.base.MPJBaseMapper;
import io.r2mo.base.dbe.Join;
import io.r2mo.dbe.mybatisplus.DBE;
import io.r2mo.dbe.mybatisplus.DBJ;
import io.r2mo.dbe.mybatisplus.JoinProxy;
import io.r2mo.io.common.HFS;
import io.r2mo.jce.common.HED;
import io.r2mo.typed.cc.Cc;

/**
 * 核心快速工具，用于快速开发，集成常用功能
 * - Rapid Application Development
 *
 * @author lang : 2025-10-27
 */
public class RAD {
    private static final Cc<String, DBE<?>> CCT_DBE = Cc.openThread();
    private static final Cc<String, DBJ<?>> CCT_DBJ = Cc.openThread();

    static {
        HED.initialize();
    }

    public static HFS fs() {
        return HFS.of();
    }

    @SuppressWarnings("unchecked")
    public static <T> DBJ<T> db(final Join meta) {
        return (DBJ<T>) CCT_DBJ.pick(() -> {
            final MPJBaseMapper<T> mapper = RADMapper.findMapper((Class<T>) meta.from());
            final MPJBaseMapper<?> joined = RADMapper.findMapper(meta.to());
            final JoinProxy<T> proxy = new JoinProxy<>(mapper).mapper(meta.to(), joined);
            return DBJ.of(meta, proxy);
        }, String.valueOf(meta.hashCode()));
    }

    @SuppressWarnings("unchecked")
    public static <T> DBE<T> db(final Class<T> entityCls) {
        return (DBE<T>) CCT_DBE.pick(() -> {
            final BaseMapper<T> mapper = RADMapper.findMapper(entityCls);
            return DBE.of(entityCls, mapper);
        }, entityCls.getName());
    }
}

package io.r2mo.boot.spring;

import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.yulichang.base.MPJBaseMapper;
import io.r2mo.base.dbe.Join;
import io.r2mo.dbe.mybatisplus.DBE;
import io.r2mo.dbe.mybatisplus.DBJ;
import io.r2mo.dbe.mybatisplus.JoinProxy;
import io.r2mo.io.common.HFS;
import io.r2mo.jce.common.HED;
import io.r2mo.typed.cc.Cc;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.core.ResolvableType;

import java.util.Collection;

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
            final MPJBaseMapper<T> mapper = findMapper((Class<T>) meta.from());
            final MPJBaseMapper<?> joined = findMapper(meta.to());
            final JoinProxy<T> proxy = new JoinProxy<>(mapper).mapper(meta.to(), joined);
            return DBJ.of(meta, proxy);
        }, String.valueOf(meta.hashCode()));
    }

    @SuppressWarnings("unchecked")
    public static <T> DBE<T> db(final Class<T> entityCls) {
        return (DBE<T>) CCT_DBE.pick(() -> {
            final BaseMapper<T> mapper = findMapper(entityCls);
            return DBE.of(entityCls, mapper);
        }, entityCls.getName());
    }

    @SuppressWarnings("all")
    private static <T> MPJBaseMapper<T> findMapper(final Class<T> entityClass) {
        final SqlSessionFactory factory = SpringUtil.getBean(SqlSessionFactory.class);
        final Configuration cfg = factory.getConfiguration();

        // 所有已知的 Mapper 接口
        final Collection<Class<?>> mappers = cfg.getMapperRegistry().getMappers();

        for (final Class<?> mapperInterface : mappers) {
            // 解析：mapperInterface 是否是 BaseMapper<entityClass>
            final ResolvableType rt = ResolvableType.forClass(mapperInterface).as(MPJBaseMapper.class);
            if (rt.hasGenerics()) {
                final Class<?> generic = rt.getGeneric(0).resolve();
                if (generic != null && generic.equals(entityClass)) {
                    // 找到对应的 Mapper 接口后，交给 Spring 取 Bean（JDK 代理）
                    final Object bean = SpringUtil.getBean(mapperInterface);
                    return (MPJBaseMapper<T>) bean;
                }
            }
        }
        throw new IllegalStateException("[ R2MO ] 未找到实体 " + entityClass.getName() + " 对应的 BaseMapper，请确认已被 @MapperScan 扫描并注册。");
    }
}

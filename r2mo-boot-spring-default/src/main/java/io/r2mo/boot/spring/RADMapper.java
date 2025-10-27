package io.r2mo.boot.spring;

import cn.hutool.extra.spring.SpringUtil;
import com.github.yulichang.base.MPJBaseMapper;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.core.ResolvableType;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings({"unchecked"})
final class RADMapper {

    private static final Map<Class<?>, Class<? extends MPJBaseMapper<?>>> CACHE = new ConcurrentHashMap<>();

    private RADMapper() {
    }

    /** 单数据源入口（容器里仅有一个 SqlSessionFactory 时用） */
    static <T> MPJBaseMapper<T> findMapper(final Class<T> entityClass) {
        // 直接从所有 SqlSessionFactory 中找，命中即可
        final Map<String, SqlSessionFactory> factories = SpringUtil.getBeansOfType(SqlSessionFactory.class);
        if (factories == null || factories.isEmpty()) {
            throw new IllegalStateException("[R2MO] 容器中未发现 SqlSessionFactory，无法定位 MPJBaseMapper。");
        }
        return findMapper(entityClass, factories.values());
    }

    /** 指定数据源入口（多数据源时可传入具体的 SqlSessionFactory） */
    static <T> MPJBaseMapper<T> findMapper(final Class<T> entityClass, final SqlSessionFactory factory) {
        Objects.requireNonNull(factory, "[R2MO] 传入的 SqlSessionFactory 不能为 null");
        return findMapper(entityClass, Collections.singleton(factory));
    }

    /** 多数据源遍历实现 */
    private static <T> MPJBaseMapper<T> findMapper(final Class<T> entityClass, final Collection<SqlSessionFactory> factories) {
        // 先看缓存
        final Class<? extends MPJBaseMapper<?>> cached = CACHE.get(entityClass);
        if (cached != null) {
            return (MPJBaseMapper<T>) SpringUtil.getBean(cached);
        }

        // 遍历所有数据源
        for (final SqlSessionFactory factory : factories) {
            final Configuration cfg = factory.getConfiguration();
            final Collection<Class<?>> mappers = cfg.getMapperRegistry().getMappers();

            for (final Class<?> mapperInterface : mappers) {
                // 关键：把接口解析成 MPJBaseMapper<T>
                final ResolvableType rt = ResolvableType.forClass(mapperInterface).as(MPJBaseMapper.class);
                if (!rt.hasGenerics()) {
                    continue;
                }
                final Class<?> generic = rt.getGeneric(0).resolve();
                if (generic != null && generic.equals(entityClass)) {
                    // 命中：按接口类型从 Spring 取出代理 Bean
                    final MPJBaseMapper<T> bean = (MPJBaseMapper<T>) SpringUtil.getBean(mapperInterface);
                    // 缓存（entityClass -> mapperInterface）
                    CACHE.put(entityClass, (Class<? extends MPJBaseMapper<?>>) mapperInterface);
                    return bean;
                }
            }
        }

        // 未找到，给出更明确的提示
        throw new IllegalStateException(
            "[R2MO] 未找到实体 " + entityClass.getName() +
                " 对应的 MPJBaseMapper，请确认：\n" +
                "1) Mapper 接口已 extends com.github.yulichang.base.MPJBaseMapper<" + entityClass.getSimpleName() + ">\n" +
                "2) Mapper 接口处于 @MapperScan 扫描范围内并成功注册\n" +
                "3) 当前所用 SqlSessionFactory 与该 Mapper 位于同一数据源配置中"
        );
    }
}

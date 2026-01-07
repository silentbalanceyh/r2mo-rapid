package io.r2mo.spring.mybatisplus.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import io.r2mo.spring.common.config.SpringPropertySourceFactory;
import io.r2mo.spring.mybatisplus.handler.InjectionMetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

/**
 * Mybatis-Plus 完备配置类
 * 解决了 dynamic-datasource 在无配置时的启动崩溃问题
 *
 * @author lang : 2025-08-28
 */
@AutoConfiguration
@EnableTransactionManagement(proxyTargetClass = true)
@MapperScan("${mybatis-plus.mapperPackage:io.r2mo.*}")
@PropertySource(value = "classpath:common-mybatis-plus.yml", factory = SpringPropertySourceFactory.class)
@Slf4j
public class MybatisPlusConfiguration {

    private final ObjectProvider<DataSource> dataSourceProvider;

    public MybatisPlusConfiguration(final ObjectProvider<DataSource> dataSourceProvider) {
        this.dataSourceProvider = dataSourceProvider;
    }

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor(
        final ObjectProvider<TenantLineInnerInterceptor> tenantProvider) {

        final MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();

        // 使用 ObjectProvider 优雅处理可选注入
        tenantProvider.ifAvailable(interceptor::addInnerInterceptor);

        // 分页插件
        final PaginationInnerInterceptor paginationInnerInterceptor = new PaginationInnerInterceptor();
        paginationInnerInterceptor.setOverflow(true);
        interceptor.addInnerInterceptor(paginationInnerInterceptor);

        // 乐观锁插件
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());

        // 安全检查逻辑
        final DataSource currentDs = this.dataSourceProvider.getIfAvailable();
        if (currentDs == null) {
            log.error("[ R2MO ] ❌ 致命错误：即使启用了兜底方案，仍未获取到 DataSource！");
        } else {
            log.info("[ R2MO ] Mybatis-Plus 拦截器链加载成功，当前数据源类型: {}", currentDs.getClass().getSimpleName());
        }

        return interceptor;
    }

    @Bean
    public MetaObjectHandler metaObjectHandler() {
        return new InjectionMetaObjectHandler();
    }

    /**
     * 【关键逻辑 2】多层级兜底数据源
     * 使用 H2 替代 Derby，因为 H2 对多数据源启动器的兼容性更好。
     * 标记为 @Primary 确保在存在多个候选时，MyBatis-Plus 能优先识别。
     */
    @Configuration
    @ConditionalOnMissingBean(DataSource.class)
    public static class FallbackDataSourceConfiguration {
        @Bean
        public DataSource dataSource() {
            log.warn("[ R2MO ] 🚀 检测到未配置外部数据源，正在启动 H2 内存数据库进行静默兼容...");
            return new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .setName("r2mo_fallback_db")
                .build();
        }
    }
}
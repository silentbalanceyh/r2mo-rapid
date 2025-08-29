package io.r2mo.spring.mybatisplus.config;

import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import io.r2mo.spring.common.config.PropertySourceYmlFactory;
import io.r2mo.spring.mybatisplus.handler.InjectionMetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Mybatis-Plus配置类
 *
 * @author lang : 2025-08-28
 */
@AutoConfiguration
@EnableTransactionManagement(proxyTargetClass = true)
@MapperScan("${mybatis-plus.mapperPackage}")
@PropertySource(value = "classpath:common-mybatis-plus.yml", factory = PropertySourceYmlFactory.class)
@Slf4j
public class MybatisPlusConfiguration {

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        final MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 多租户插件 必须放到第一位
        try {
            final TenantLineInnerInterceptor tenant = SpringUtil.getBean(TenantLineInnerInterceptor.class);
            interceptor.addInnerInterceptor(tenant);
        } catch (final BeansException ignore) {
        }
        // 分页插件
        {
            final PaginationInnerInterceptor paginationInnerInterceptor = new PaginationInnerInterceptor();
            // 分页合理化
            paginationInnerInterceptor.setOverflow(true);
            interceptor.addInnerInterceptor(paginationInnerInterceptor);
        }
        // 乐观锁
        {
            final OptimisticLockerInnerInterceptor lockerInnerInterceptor = new OptimisticLockerInnerInterceptor();
            interceptor.addInnerInterceptor(lockerInnerInterceptor);
        }

        log.info("[ R2MO ] Config / Mybatis-Plus Interceptor 配置完成！");
        return interceptor;
    }

    @Bean
    public MetaObjectHandler metaObjectHandler() {
        return new InjectionMetaObjectHandler();
    }
}

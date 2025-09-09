package io.r2mo.spring;

import io.r2mo.spring.common.config.SpringResponseAdvice;
import io.r2mo.spring.common.exception.SpringExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author lang : 2025-09-09
 */
@Slf4j
public class SpringApplicationInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(@NonNull final ConfigurableApplicationContext applicationContext) {
        log.info("[ R2MO ] === 注册 Advice 处理组件 ===");

        final String[] advicePackages = {
            // 主要扫描当前包及其子包
            SpringResponseAdvice.class.getPackageName(),
            SpringExceptionHandler.class.getPackageName(),
        };

        if (applicationContext instanceof final GenericApplicationContext genericContext) {
            this.scanAdviceComponents(genericContext, advicePackages);
        }

        log.info("[ R2MO ] === Advice 处理组件注册完成 ===");
    }

    private void scanAdviceComponents(final GenericApplicationContext context, final String[] packages) {
        final ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(context);

        // 扫描 ControllerAdvice 相关注解
        scanner.addIncludeFilter(new AnnotationTypeFilter(ControllerAdvice.class));
        scanner.addIncludeFilter(new AnnotationTypeFilter(RestControllerAdvice.class));

        for (final String packageName : packages) {
            final int count = scanner.scan(packageName);
            log.info("[ R2MO ] 扫描包 [" + packageName + "] 发现 " + count + " 个 Advice 组件");
        }
    }
}

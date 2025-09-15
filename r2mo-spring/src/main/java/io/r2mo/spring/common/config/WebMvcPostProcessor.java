package io.r2mo.spring.common.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

/**
 * @author lang : 2025-09-15
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class WebMvcPostProcessor implements BeanFactoryPostProcessor {

    @Override
    public void postProcessBeanFactory(@NonNull final ConfigurableListableBeanFactory beanFactory) throws BeansException {
        try {
            // 检查是否是 DefaultListableBeanFactory
            if (beanFactory instanceof final DefaultListableBeanFactory defaultBeanFactory) {

                // 移除已存在的 Bean 定义
                if (defaultBeanFactory.containsBeanDefinition("requestMappingHandlerMapping")) {
                    defaultBeanFactory.removeBeanDefinition("requestMappingHandlerMapping");
                }

                // 注册新的 Bean 定义
                final BeanDefinitionBuilder builder = BeanDefinitionBuilder
                    .genericBeanDefinition(WebPriorityRequestMapping.class);

                defaultBeanFactory.registerBeanDefinition("requestMappingHandlerMapping",
                    builder.getBeanDefinition());
            }
        } catch (final Exception e) {
            System.err.println("Failed to register WebPriorityRequestMapping: " + e.getMessage());
        }
    }
}

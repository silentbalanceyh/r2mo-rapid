package io.r2mo.spring.common.config;

import io.r2mo.spring.common.annotation.PriorityMapping;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Component
@Slf4j
public class WebPriorityRequestMapping extends RequestMappingHandlerMapping {
    public WebPriorityRequestMapping() {
        this.setOrder(0);
    }

    private static final Set<Integer> DUPLICATED_REG = new HashSet<>();

    @Override
    protected void registerHandlerMethod(@NonNull final Object handler,
                                         @NonNull final Method method,
                                         @NonNull final RequestMappingInfo mapping) {
        try {
            final HandlerMethod existingHandler = this.getExistingHandlerMethod(mapping);

            if (existingHandler != null && !this.isSameMethod(existingHandler, method)) {
                final HandlerMethod newHandler = this.createHandlerMethod(handler, method);
                final HandlerMethod selectedHandler = this.selectHandlerMethod(existingHandler, newHandler, method);

                if (selectedHandler != existingHandler) {
                    if(!DUPLICATED_REG.contains(mapping.hashCode())){
                        log.info("[ R2MO ] 映射冲突解决: {} 旧方法 {}.{} (优先级:{}) -> 新方法 {}.{} (优先级:{})",
                            mapping,
                            existingHandler.getMethod().getDeclaringClass().getSimpleName(),
                            existingHandler.getMethod().getName(),
                            this.getMethodPriority(existingHandler.getMethod()),
                            selectedHandler.getMethod().getDeclaringClass().getSimpleName(),
                            selectedHandler.getMethod().getName(),
                            this.getMethodPriority(selectedHandler.getMethod()));
                        DUPLICATED_REG.add(mapping.hashCode());
                    }
                    this.unregisterMapping(mapping);
                    super.registerHandlerMethod(handler, method, mapping);
                }
            } else {
                super.registerHandlerMethod(handler, method, mapping);
            }
        } catch (final Exception e) {
            log.error("[ R2MO ] 映射注册错误: {}", e.getMessage());
            super.registerHandlerMethod(handler, method, mapping);
        }
    }

    private HandlerMethod getExistingHandlerMethod(final RequestMappingInfo mapping) {
        try {
            final Map<RequestMappingInfo, HandlerMethod> handlerMethods = this.getHandlerMethods();
            return handlerMethods.get(mapping);
        } catch (final Exception e) {
            log.debug("[ R2MO ] 获取现有处理器方法失败: {}", e.getMessage());
        }
        return null;
    }

    private boolean isSameMethod(final HandlerMethod existing, final Method newMethod) {
        return existing.getMethod().equals(newMethod) &&
            existing.getBeanType().equals(newMethod.getDeclaringClass());
    }

    private HandlerMethod selectHandlerMethod(final HandlerMethod existing, final HandlerMethod newHandler, final Method newMethod) {
        // 获取优先级 - 支持接口注解
        final int existingPriority = this.getMethodPriority(existing.getMethod());
        final int newPriority = this.getMethodPriority(newMethod);

        if (newPriority > existingPriority) {
            return newHandler;
        } else if (newPriority < existingPriority) {
            return existing;
        } else {
            // 优先级相同，检查是否强制覆盖
            final PriorityMapping newAnnotation = this.findMethodAnnotation(newMethod);
            final PriorityMapping existingAnnotation = this.findMethodAnnotation(existing.getMethod());

            if (newAnnotation != null && newAnnotation.force()) {
                return newHandler;
            } else if (existingAnnotation != null && existingAnnotation.force()) {
                return existing;
            } else {
                return existing; // 默认保留现有方法
            }
        }
    }

    private int getMethodPriority(final Method method) {
        final PriorityMapping annotation = this.findMethodAnnotation(method);
        return annotation != null ? annotation.value() : 0;
    }
    /**
     * 查找方法上的 PriorityMapping 注解，包括接口方法上的注解
     */
    private PriorityMapping findMethodAnnotation(final Method method) {
        // 1. 首先检查方法本身
        PriorityMapping annotation = method.getAnnotation(PriorityMapping.class);
        if (annotation != null) {
            log.debug("[ R2MO ] 在方法本身找到注解: {}.{}",
                method.getDeclaringClass().getSimpleName(), method.getName());
            return annotation;
        }

        // 2. 检查接口方法上的注解
        final Class<?> declaringClass = method.getDeclaringClass();
        final Class<?>[] interfaces = declaringClass.getInterfaces();

        for (final Class<?> interfaceClass : interfaces) {
            try {
                // 获取接口中对应的方法
                final Method interfaceMethod = interfaceClass.getMethod(method.getName(), method.getParameterTypes());
                annotation = interfaceMethod.getAnnotation(PriorityMapping.class);
                if (annotation != null) {
                    log.debug("[ R2MO ] 在接口方法找到注解: {}.{}",
                        interfaceClass.getSimpleName(), method.getName());
                    return annotation;
                }
            } catch (final NoSuchMethodException e) {
                // 接口中没有对应的方法，继续检查其他接口
                continue;
            }
        }

        log.debug("[ R2MO ] 未找到 PriorityMapping 注解，返回 null");
        return null;
    }
}
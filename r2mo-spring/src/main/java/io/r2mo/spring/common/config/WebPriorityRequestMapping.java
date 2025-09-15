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
        // 确保配置正确的顺序
        this.setOrder(0);
    }

    private static final Set<Integer> DUPLICATED_REG = new HashSet<>();

    @Override
    protected void registerHandlerMethod(@NonNull final Object handler,
                                         @NonNull final Method method,
                                         @NonNull final RequestMappingInfo mapping) {
        try {
            // 检查是否已存在相同的映射
            final HandlerMethod existingHandler = this.getExistingHandlerMethod(mapping);

            if (existingHandler != null && !this.isSameMethod(existingHandler, method)) {
                // 存在冲突，需要根据优先级决定
                final HandlerMethod newHandler = this.createHandlerMethod(handler, method);
                final HandlerMethod selectedHandler = this.selectHandlerMethod(existingHandler, newHandler, method);
                if (selectedHandler != existingHandler) {
                    // 新方法优先级更高
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
                // 没有冲突或相同方法，正常注册
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
        // 获取优先级
        final int existingPriority = this.getMethodPriority(existing.getMethod());
        final int newPriority = this.getMethodPriority(newMethod);

        // 根据优先级选择
        if (newPriority > existingPriority) {
            return newHandler;
        } else if (newPriority < existingPriority) {
            return existing;
        } else {
            // 优先级相同，检查是否强制覆盖
            final PriorityMapping newAnnotation = newMethod.getAnnotation(PriorityMapping.class);
            if (newAnnotation != null && newAnnotation.force()) {
                return newHandler;
            } else {
                return existing;
            }
        }
    }

    private int getMethodPriority(final Method method) {
        final PriorityMapping annotation = method.getAnnotation(PriorityMapping.class);
        return annotation != null ? annotation.value() : 0; // 默认优先级为 0
    }
}
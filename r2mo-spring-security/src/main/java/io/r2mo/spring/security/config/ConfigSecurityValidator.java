package io.r2mo.spring.security.config;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * @author lang : 2025-11-11
 */
@Component
public class ConfigSecurityValidator implements Validator {
    @Override
    public boolean supports(@NonNull final Class<?> clazz) {
        // 验证配置专用
        return ConfigSecurity.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(@NonNull final Object target, @NonNull final Errors errors) {
    }
}

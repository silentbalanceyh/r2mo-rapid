package io.r2mo.spring.common.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author lang : 2025-09-15
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PriorityMapping {
    /**
     * 优先级，数值越大优先级越高
     * < 0: 使用原有方法
     * = 0: 默认优先级（与原方法相同，可能冲突）
     * > 0: 覆盖原方法
     */
    int value() default 0;

    /**
     * 是否强制覆盖，即使优先级相同
     */
    boolean force() default false;
}

package io.r2mo.typed.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author lang : 2025-09-04
 */
@Target(ElementType.TYPE)
@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface SPID {
    String value() default "";

    /**
     * 优先级处理，默认为 0，数值越大，优先级越高，SPI 组件主要分成三大类
     * <pre>
     *     1. 第一类 -> Zero 核心组件              = 0
     *     2. 第二类 -> Zero Extension 扩展组件    = 1017
     *     3. 第三类 -> Zero 自定义组件            > 2000
     * </pre>
     *
     * @return 计算组件筛选的优先级
     */
    int priority() default 0;
}

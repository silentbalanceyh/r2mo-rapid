package io.r2mo.typed.annotation;

import java.lang.annotation.*;

/**
 * @author lang : 2025-09-04
 */
@Target(ElementType.TYPE)
@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface OneSPI {
    String name() default "";
}

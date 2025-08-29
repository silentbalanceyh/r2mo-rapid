package io.r2mo.typed.annotation;

import java.lang.annotation.*;

/**
 * @author lang : 2025-08-28
 */
@Inherited
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Oneness {
    String value() default "";
}

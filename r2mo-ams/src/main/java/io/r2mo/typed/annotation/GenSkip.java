package io.r2mo.typed.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author lang : 2025-09-10
 */
@Target(ElementType.TYPE)
@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface GenSkip {
}

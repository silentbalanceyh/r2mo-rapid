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
public @interface Identifiers {
    String[] value() default {};

    /**
     * 是否包含 appId
     *
     * @return boolean
     */
    boolean ifApp() default true;

    /**
     * 是否包含 tenantId
     *
     * @return boolean
     */
    boolean ifTenant() default true;

    /**
     * 是否包含 enabled = true 的条件
     *
     * @return boolean
     */
    boolean ifEnabled() default false;
}

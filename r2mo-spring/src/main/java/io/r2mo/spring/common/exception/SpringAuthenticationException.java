package io.r2mo.spring.common.exception;

import io.r2mo.typed.enums.TypeLogin;
import io.r2mo.typed.exception.WebException;
import org.springframework.security.core.AuthenticationException;

/**
 * 自定义认证相关异常，作为 AuthenticationException 的子类，可直接被捕捉，对应不同的场景下的处理流程，且
 * <pre>
 *     ExceptionHandler 在处理过程中如果遇到这种类型异常会执行特殊转换
 * </pre>
 *
 * @author lang : 2025-12-08
 */
public abstract class SpringAuthenticationException extends AuthenticationException {

    protected final String identifier;
    protected final TypeLogin type;

    public SpringAuthenticationException(final String msg, final String identifier, final TypeLogin type) {
        super(msg);
        this.identifier = identifier;
        this.type = type;
    }

    public abstract WebException toFailure();
}

package io.r2mo.spring.security.extension.handler;

import io.r2mo.spring.common.exception.SpringAuthenticationException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author lang : 2025-12-05
 */
@Component
public class SecurityHandler401Unauthorized implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(final HttpServletRequest request, final HttpServletResponse response,
                                        final AuthenticationException authException) throws IOException, ServletException {
        // 直接返回 401 状态码
        if (authException instanceof final SpringAuthenticationException customFailure) {
            SecurityFailure.handleFailure(request, response, customFailure.toFailure());
            return;
        }
        // 转换成统一的 WebException
        final Throwable cause = SecurityFailure.findExceptionAt(authException);
        SecurityFailure.handleFailure(request, response, cause);
    }
}

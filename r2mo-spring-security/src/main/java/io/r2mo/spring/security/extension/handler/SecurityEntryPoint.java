package io.r2mo.spring.security.extension.handler;

import io.r2mo.spring.common.exception.SpringAbortExecutor;
import io.r2mo.typed.exception.WebException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Objects;

/**
 * @author lang : 2025-11-11
 */
@Component
public class SecurityEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(final HttpServletRequest request, final HttpServletResponse response,
                         final AuthenticationException authException)
        throws IOException, ServletException {


        // 转换成统一的 WebException
        Throwable cause = authException.getCause();
        if (Objects.isNull(cause)) {
            cause = authException;
        }
        final WebException transform = SecurityFailure.of()
            .transform(cause, request, response);


        // 直接执行异常处理器
        SpringAbortExecutor.handleFailure(transform, response);
    }
}

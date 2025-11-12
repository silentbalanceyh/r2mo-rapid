package io.r2mo.spring.security.extension.handler;

import io.r2mo.spring.common.exception.SpringAbortExecutor;
import io.r2mo.typed.exception.WebException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author lang : 2025-11-11
 */
@Component
public class SecurityDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(final HttpServletRequest request, final HttpServletResponse response,
                       final AccessDeniedException accessDeniedException)
        throws IOException, ServletException {


        // 转换成统一的 WebException
        final Throwable cause = SecurityFailure.findExceptionAt(accessDeniedException);
        final WebException webException = SecurityFailure.of().transform(cause, request, response);


        // 执行异常处理
        SpringAbortExecutor.handleFailure(webException, response);
    }
}

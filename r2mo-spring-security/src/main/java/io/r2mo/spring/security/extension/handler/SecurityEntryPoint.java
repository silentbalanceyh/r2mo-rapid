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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author lang : 2025-11-11
 */
@Component
public class SecurityEntryPoint implements AuthenticationEntryPoint {

    private final List<Consumer<HttpServletResponse>> waitFor = new ArrayList<>();

    @SafeVarargs
    public static SecurityEntryPoint of(final Consumer<HttpServletResponse>... consumers) {
        final SecurityEntryPoint instance = new SecurityEntryPoint();
        instance.waitFor.addAll(Arrays.asList(consumers));
        return instance;
    }

    @Override
    public void commence(final HttpServletRequest request, final HttpServletResponse response,
                         final AuthenticationException authException)
        throws IOException, ServletException {
        // 执行特定操作
        this.waitFor.forEach(consumer -> consumer.accept(response));


        // 转换成统一的 WebException
        final Throwable cause = SecurityFailure.findExceptionAt(authException);
        final WebException transform = SecurityFailure.of().transform(cause, request, response);


        // 直接执行异常处理器
        SpringAbortExecutor.handleFailure(transform, response);
    }
}

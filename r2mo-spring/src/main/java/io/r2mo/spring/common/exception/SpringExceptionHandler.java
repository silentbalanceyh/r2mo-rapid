package io.r2mo.spring.common.exception;

import com.fasterxml.jackson.core.JsonParseException;
import io.r2mo.base.web.FailOr;
import io.r2mo.spi.SPI;
import io.r2mo.typed.exception.WebException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.io.IOException;

/**
 * @author lang : 2025-09-03
 */
@Slf4j
@RestControllerAdvice
public class SpringExceptionHandler {
    private final FailOr failJvm = SPI.V_ABORT.failJvm();
    private final FailOr failSpring = SPI.V_ABORT.failContainer();

    // -------------- Jvm 类型
    @ExceptionHandler(ServletException.class)
    public void handleServlet(final ServletException ex,
                              final HttpServletRequest request, final HttpServletResponse response) {
        SpringAbortExecutor.handleFailure(
            this.failJvm.transform(ex, request, response), response, request);
    }

    @ExceptionHandler(IOException.class)
    public void handleIO(final IOException ex,
                         final HttpServletRequest request, final HttpServletResponse response) {
        SpringAbortExecutor.handleFailure(
            this.failJvm.transform(ex, request, response), response, request);
    }

    @ExceptionHandler(RuntimeException.class)
    public void handleRuntime(final RuntimeException ex,
                              final HttpServletRequest request, final HttpServletResponse response) {
        SpringAbortExecutor.handleFailure(
            this.failJvm.transform(ex, request, response), response, request);
    }

    @ExceptionHandler(Exception.class)
    public void handleException(final Exception ex,
                                final HttpServletRequest request, final HttpServletResponse response) {
        SpringAbortExecutor.handleFailure(
            this.failJvm.transform(ex, request, response), response, request);
    }

    @ExceptionHandler(JsonParseException.class)
    public void handleJsonParse(final JsonParseException ex,
                                final HttpServletRequest request, final HttpServletResponse response) {
        SpringAbortExecutor.handleFailure(
            this.failJvm.transform(ex, request, response), response, request);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public void handleConstraintViolation(final ConstraintViolationException ex,
                                          final HttpServletRequest request, final HttpServletResponse response) {
        SpringAbortExecutor.handleFailure(
            this.failJvm.transform(ex, request, response), response, request);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public void handleIllegalArgument(final IllegalArgumentException ex,
                                      final HttpServletRequest request, final HttpServletResponse response) {
        SpringAbortExecutor.handleFailure(
            this.failJvm.transform(ex, request, response), response, request);
    }

    // -------------- Spring 类型
    // ❌️ 请求方法不支持异常
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public void handleMethodNotSupported(final HttpRequestMethodNotSupportedException ex,
                                         final HttpServletRequest request, final HttpServletResponse response) {
        SpringAbortExecutor.handleFailure(
            this.failSpring.transform(ex, request, response), response, request);
    }

    // ❌️ 请求路径中缺少必要参数
    @ExceptionHandler(MissingPathVariableException.class)
    public void handleMissingPathVariable(final MissingPathVariableException ex,
                                          final HttpServletRequest request, final HttpServletResponse response) {
        SpringAbortExecutor.handleFailure(
            this.failSpring.transform(ex, request, response), response, request);
    }

    // ❌️ 请求参数类型不匹配
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public void handleMethodArgumentTypeMismatch(final MethodArgumentTypeMismatchException ex,
                                                 final HttpServletRequest request, final HttpServletResponse response) {
        SpringAbortExecutor.handleFailure(
            this.failSpring.transform(ex, request, response), response, request);
    }

    // ❌️ 找不到路由
    @ExceptionHandler(NoHandlerFoundException.class)
    public void handleNoHandlerFound(final NoHandlerFoundException ex,
                                     final HttpServletRequest request, final HttpServletResponse response) {
        SpringAbortExecutor.handleFailure(
            this.failSpring.transform(ex, request, response), response, request);
    }

    // ❌️ 自定义验证
    @ExceptionHandler(BindException.class)
    public void handleBindException(final BindException ex,
                                    final HttpServletRequest request, final HttpServletResponse response) {
        SpringAbortExecutor.handleFailure(
            this.failSpring.transform(ex, request, response), response, request);
    }

    // ❌️ 特殊自定义验证异常
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public void handleMethodArgumentNotValid(final MethodArgumentNotValidException ex,
                                             final HttpServletRequest request, final HttpServletResponse response) {
        SpringAbortExecutor.handleFailure(
            this.failSpring.transform(ex, request, response), response, request);
    }

    // ❌️ 请求体读取异常
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public void handleHttpMessageNotReadable(final HttpMessageNotReadableException ex,
                                             final HttpServletRequest request, final HttpServletResponse response) {
        SpringAbortExecutor.handleFailure(
            this.failSpring.transform(ex, request, response), response, request);
    }

    // ❌️ 媒体类型异常
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public void handleHttpMediaTypeNotSupported(final HttpMediaTypeNotSupportedException ex,
                                                final HttpServletRequest request, final HttpServletResponse response) {
        SpringAbortExecutor.handleFailure(
            this.failSpring.transform(ex, request, response), response, request);
    }

    // ❌️ 自定义异常
    @ExceptionHandler(WebException.class)
    public void handleWebException(final WebException ex,
                                   final HttpServletRequest request, final HttpServletResponse response) {
        SpringAbortExecutor.handleFailure(ex, response, request);
    }
}

package io.r2mo.spring.common.exception;

import io.r2mo.base.web.FailOr;
import io.r2mo.spring.common.exception.web._405MethodBadException;
import io.r2mo.spring.common.exception.web._415MediaNotSupportException;
import io.r2mo.typed.exception.WebException;
import io.r2mo.typed.exception.web._400BadRequestException;
import io.r2mo.typed.exception.web._401UnauthorizedException;
import io.r2mo.typed.exception.web._403ForbiddenException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.HttpRequestMethodNotSupportedException;

import java.util.Objects;

/**
 * @author lang : 2025-09-03
 */
@Slf4j
public class FailOrSpring implements FailOr {
    @Override
    public WebException transform(final Throwable ex, final HttpServletRequest request, final HttpServletResponse response) {
        // org.springframework.web.HttpRequestMethodNotSupportedException
        // ❌️ 请求方法不支持异常
        if (ex instanceof final HttpRequestMethodNotSupportedException e) {
            log.error("[ R2MO ] (S) 请求地址 '{}'，不支持 '{}' 请求 ", request.getRequestURI(), e.getMethod(), e);
            return new _405MethodBadException(e.getMessage());
        }

        // org.springframework.web.bind.MissingPathVariableException
        // ❌️ 缺少路径变量异常
        if (ex instanceof final org.springframework.web.bind.MissingPathVariableException e) {
            log.error("[ R2MO ] (S) 请求地址 '{}'，缺少路径变量 '{}'", request.getRequestURI(), e.getVariableName(), e);
            return new _400BadRequestException(e.getMessage());
        }

        // org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
        // ❌️ 方法参数类型不匹配异常
        if (ex instanceof final org.springframework.web.method.annotation.MethodArgumentTypeMismatchException e) {
            log.error("[ R2MO ] (S) 请求地址 '{}'，方法参数类型不匹配，参数 '{}' 期望类型 '{}'", request.getRequestURI(), e.getName(), e.getRequiredType(), e);
            return new _400BadRequestException(e.getMessage());
        }

        // org.springframework.web.servlet.NoHandlerFoundException
        // ❌️ 未找到处理程序异常，通常是请求的 URL 不存在
        if (ex instanceof final org.springframework.web.servlet.NoHandlerFoundException e) {
            log.error("[ R2MO ] (S) 请求地址 '{}'，未找到处理程序", request.getRequestURI(), e);
            return new _400BadRequestException(e.getMessage());
        }

        // org.springframework.web.bind.MethodArgumentNotValidException
        // ❌️ 方法参数校验异常，通常是请求参数校验失败
        if (ex instanceof final org.springframework.web.bind.MethodArgumentNotValidException e) {
            log.error("[ R2MO ] (S) 请求地址 '{}'，方法参数校验失败", request.getRequestURI(), e);
            final String message = Objects.requireNonNull(e.getBindingResult().getFieldError()).getDefaultMessage();
            return new _400BadRequestException(message);
        }

        // org.springframework.validation.BindException
        // ❌️ 绑定异常，验证参数绑定失败
        if (ex instanceof final org.springframework.validation.BindException e) {
            log.error("[ R2MO ] (S) 请求地址 '{}'，验证参数绑定失败", request.getRequestURI(), e);
            final String message = Objects.requireNonNull(e.getBindingResult().getFieldError()).getDefaultMessage();
            return new _400BadRequestException(message);
        }

        // org.springframework.http.converter.HttpMessageNotReadableException
        // ❌️ HTTP 消息不可读异常，通常是请求体格式错误
        if (ex instanceof final org.springframework.http.converter.HttpMessageNotReadableException e) {
            log.error("[ R2MO ] (S) 请求地址 '{}'，HTTP 消息不可读", request.getRequestURI(), e);
            return new _400BadRequestException(e.getMessage());
        }

        // org.springframework.security.core.AuthenticationException
        // ❌️ Spring Security 认证异常，通常是认证失败
        if (ex instanceof final org.springframework.security.core.AuthenticationException e) {
            log.error("[ R2MO ] (S) 请求地址 '{}'，认证失败", request.getRequestURI(), e);
            return new _401UnauthorizedException(e.getMessage());
        }

        // org.springframework.security.access.AccessDeniedException
        // ❌️ Spring Security 权限拒绝异常，通常是没有权限访问
        if (ex instanceof final org.springframework.security.access.AccessDeniedException e) {
            log.error("[ R2MO ] (S) 请求地址 '{}'，权限拒绝", request.getRequestURI(), e);
            return new _403ForbiddenException(e.getMessage());
        }

        // org.springframework.web.HttpMediaTypeNotSupportedException
        // ❌️ 不支持的媒体类型异常，通常是请求的 Content-Type 不
        if (ex instanceof final org.springframework.web.HttpMediaTypeNotSupportedException e) {
            log.error("[ R2MO ] (S) 请求地址 '{}'，不支持的媒体类型 '{}'", request.getRequestURI(), e.getContentType(), e);
            return new _415MediaNotSupportException(e.getMessage());
        }
        return null;
    }
}

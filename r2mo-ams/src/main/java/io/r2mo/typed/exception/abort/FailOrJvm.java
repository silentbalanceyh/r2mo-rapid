package io.r2mo.typed.exception.abort;

import com.fasterxml.jackson.core.JsonParseException;
import io.r2mo.base.web.FailOr;
import io.r2mo.base.web.ForAbort;
import io.r2mo.spi.SPI;
import io.r2mo.typed.exception.WebException;
import io.r2mo.typed.exception.web._400BadRequestException;
import io.r2mo.typed.exception.web._500ServerInternalException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * @author lang : 2025-09-03
 */
@Slf4j
public class FailOrJvm implements FailOr {
    @Override
    public WebException transform(final Throwable ex,
                                  final HttpServletRequest request, final HttpServletResponse response) {
        // jakarta.servlet.ServletException
        // ❌️ Servlet 异常
        if (ex instanceof final ServletException e) {
            log.error("[ R2MO ] 请求地址 '{}' 未知异常 <-- ServletException", request.getRequestURI(), e);
            return new _500ServerInternalException(e.getMessage());
        }

        // com.fasterxml.jackson.core.JsonParseException
        // ❌️ JSON 解析异常，Jackson 在解析 JSON 格式时出错，有可能是请求格式非法，也可能是反序列化引起
        if (ex instanceof final JsonParseException e) {
            log.error("[ R2MO ] 请求地址 '{}' 未知异常 <-- JsonParseException", request.getRequestURI(), e);
            return new _400BadRequestException(e.getMessage());
        }

        // java.io.IOException
        // ❌️ IO 异常，通常是客户端中断请求、或者其他，这种情况异常十分多
        if (ex instanceof final java.io.IOException e) {
            log.error("[ R2MO ] 请求地址 '{}' 未知异常 <-- IOException", request.getRequestURI(), e);
            return new _500ServerInternalException(e.getMessage());
        }

        // jakarta.validation.ConstraintViolationException
        // ❌️ Bean Validation 校验异常，通常是请求参数校验失败
        if (ex instanceof final jakarta.validation.ConstraintViolationException e) {
            log.error("[ R2MO ] 请求地址 '{}' 未知异常 <-- ConstraintViolationException", request.getRequestURI(), e);
            return new _400BadRequestException(e.getMessage());
        }

        // java.lang.IllegalArgumentException
        // ❌️ 非法参数异常，通常是方法参数不合法
        if (ex instanceof final IllegalArgumentException e) {
            log.error("[ R2MO ] 请求地址 '{}' 未知异常 <-- IllegalArgumentException", request.getRequestURI(), e);
            return new _400BadRequestException(e.getMessage());
        }

        // -------------------- 此处异常之前访问 FailOrApp --------------------
        final ForAbort abort = SPI.SPI_WEB.ofAbort();
        if (Objects.nonNull(abort)) {
            final FailOr failOr = abort.failApp();
            if (Objects.nonNull(failOr)) {
                return failOr.transform(ex, request, response);
            }
        }

        // java.lang.RuntimeException
        // 运行时异常，通常是代码逻辑错误
        if (ex instanceof final RuntimeException e) {
            log.error("[ R2MO ] 请求地址 '{}' 未知异常 <-- RuntimeException", request.getRequestURI(), e);
            return new _500ServerInternalException(e.getMessage());
        }

        // java.lang.Exception
        // 普通异常，通常是代码逻辑错误
        if (ex instanceof final Exception e) {
            log.error("[ R2MO ] 请求地址 '{}' 未知异常 <-- Exception", request.getRequestURI(), e);
            return new _500ServerInternalException(e.getMessage());
        }
        return null;
    }
}

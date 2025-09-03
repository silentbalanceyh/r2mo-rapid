package io.r2mo.spring.common.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.r2mo.typed.exception.WebException;
import io.r2mo.typed.webflow.R;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.util.Objects;

/**
 * @author lang : 2025-09-03
 */
@Slf4j
public class SpringAbortExecutor {
    private SpringAbortExecutor() {
    }

    public static void handleFailure(final WebException ex,
                                     final HttpServletResponse response) {
        handleFailure(ex, response, null);
    }

    public static void handleFailure(final WebException ex,
                                     final HttpServletResponse response, final HttpServletRequest request) {
        Objects.requireNonNull(ex);
        /*
         * 计算响应的 Mime
         */
        handleMime(response, request);


        /*
         * 响应结果处理，得到最终结果反馈
         */
        final R<?> result = R.failure(ex);

        final HttpStatus status = result.getStatus();
        response.setStatus(status.value());
        final ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writeValue(response.getOutputStream(), result);
        } catch (final IOException e) {
            log.error("[ R2MO ] handleFailure 处理 / IO 异常", e);
        }
    }

    public static void handleMime(final HttpServletResponse response, final HttpServletRequest request) {
        // TODO:
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    }
}

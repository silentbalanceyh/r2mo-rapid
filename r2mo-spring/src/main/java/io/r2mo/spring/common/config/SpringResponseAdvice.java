package io.r2mo.spring.common.config;

import io.r2mo.typed.webflow.R;
import io.r2mo.typed.webflow.WebState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.Objects;

/**
 * @author lang : 2025-09-08
 */
@ControllerAdvice
@Slf4j
public class SpringResponseAdvice implements ResponseBodyAdvice<R<?>> {


    @Override
    public boolean supports(@NonNull final MethodParameter returnType,
                            @NonNull final Class<? extends HttpMessageConverter<?>> converterType) {
        return R.class.isAssignableFrom(returnType.getParameterType());
    }

    @Override
    public R<?> beforeBodyWrite(final R<?> body,
                                @NonNull final MethodParameter returnType, @NonNull final MediaType selectedContentType,
                                @NonNull final Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                @NonNull final ServerHttpRequest request, @NonNull final ServerHttpResponse response) {
        if (Objects.nonNull(body) && Objects.nonNull(body.getData())) {
            final WebState state = body.getStatus();
            final HttpStatus status = state.value();
            response.setStatusCode(status);
            log.info("[ R2MO ] 响应代码：{}", status.value());
        }
        return body;
    }
}

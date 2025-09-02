package io.r2mo.typed.process;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.r2mo.spi.SPIConnect;
import io.r2mo.typed.exception.web._500ServerInternalException;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author lang : 2025-08-28
 */
public class R<T> implements SPIConnect, Serializable {

    @JsonIgnore
    private RSuccess<T> success;

    @JsonIgnore
    private RFailed failed;

    // 快速方法处理
    private static <T> R<T> success(final T data, final WebState state) {
        final R<T> r = new R<>();
        r.success = new RSuccess<>(data, state);
        return r;
    }

    private static <T> R<T> failed(final Throwable e) {
        final R<T> r = new R<>();
        r.failed = new RFailed(e);
        return r;
    }

    public static <T> R<T> ok() {
        return success(null, SPIConnect.SPI_WEB.ofSuccess204());
    }

    public static <T> R<T> ok(final T data) {
        return success(data, SPIConnect.SPI_WEB.ofSuccess());
    }

    public static <T> R<T> ok(final T data, final WebState state) {
        return success(data, state);
    }

    public static <T> R<T> failure() {
        return failed(new _500ServerInternalException("[ R2MO ] 500 操作失败"));
    }

    public static <T> R<T> failure(final String message) {
        return failed(new _500ServerInternalException(message));
    }

    public static <T> R<T> failure(final Throwable e) {
        return failed(e);
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("status")
    public <HS extends Enum<HS>> HS getStatus() {
        final WebState state;
        if (Objects.isNull(this.success)) {
            state = Objects.requireNonNull(this.failed).getStatus();
        } else {
            state = this.success.getState();
        }
        return state.value();
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("message")
    public String getMessage() {
        return Objects.isNull(this.failed) ? null : this.failed.getMessage();
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("data")
    public T getData() {
        return Objects.isNull(this.success) ? null : this.success.getData();
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("code")
    public Integer getCode() {
        return Objects.isNull(this.failed) ? null : this.failed.getCode();
    }

    @JsonIgnore
    public boolean hasError() {
        return Objects.nonNull(this.failed);
    }
}

package io.r2mo.spring.common.webflow;

import io.r2mo.spi.SPI;
import io.r2mo.typed.service.ActState;
import io.r2mo.typed.webflow.WebState;
import org.springframework.http.HttpStatus;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author lang : 2025-09-08
 */
class SuperVector {

    private static final ConcurrentMap<ActState, HttpStatus> STATE_MAP = new ConcurrentHashMap<ActState, HttpStatus>() {
        {
            // 1xx Informational - 信息性状态码
            this.put(ActState.INFO_100_CONTINUE, HttpStatus.CONTINUE);
            this.put(ActState.INFO_101_SWITCHING_PROTOCOLS, HttpStatus.SWITCHING_PROTOCOLS);
            this.put(ActState.INFO_102_PROCESSING, HttpStatus.PROCESSING);
            this.put(ActState.INFO_103_EARLY_HINTS, HttpStatus.EARLY_HINTS);

            // 2xx Success - 成功状态码
            this.put(ActState.SUCCESS, HttpStatus.OK);
            this.put(ActState.SUCCESS_201_CREATED, HttpStatus.CREATED);
            this.put(ActState.SUCCESS_202_ACCEPTED, HttpStatus.ACCEPTED);
            this.put(ActState.SUCCESS_204_NO_DATA, HttpStatus.NO_CONTENT);
            this.put(ActState.SUCCESS_205_RESET_CONTENT, HttpStatus.RESET_CONTENT);
            this.put(ActState.SUCCESS_206_PARTIAL_CONTENT, HttpStatus.PARTIAL_CONTENT);
            this.put(ActState.SUCCESS_207_MULTI_STATUS, HttpStatus.MULTI_STATUS);
            this.put(ActState.SUCCESS_208_ALREADY_REPORTED, HttpStatus.ALREADY_REPORTED);
            this.put(ActState.SUCCESS_226_IM_USED, HttpStatus.IM_USED);

            // 3xx Redirection - 重定向状态码
            this.put(ActState.REDIRECT_300_MULTIPLE_CHOICES, HttpStatus.MULTIPLE_CHOICES);
            this.put(ActState.REDIRECT_301_MOVED_PERMANENTLY, HttpStatus.MOVED_PERMANENTLY);
            this.put(ActState.REDIRECT_302_FOUND, HttpStatus.FOUND);
            this.put(ActState.REDIRECT_303_SEE_OTHER, HttpStatus.SEE_OTHER);
            this.put(ActState.REDIRECT_304_NOT_MODIFIED, HttpStatus.NOT_MODIFIED);
            this.put(ActState.REDIRECT_307_TEMPORARY_REDIRECT, HttpStatus.TEMPORARY_REDIRECT);
            this.put(ActState.REDIRECT_308_PERMANENT_REDIRECT, HttpStatus.PERMANENT_REDIRECT);

            // 错误状态默认使用 500
            this.put(ActState.FAILURE, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    };

    static WebState webState(final ActState state) {
        final HttpStatus status = STATE_MAP.getOrDefault(state, HttpStatus.OK);
        return SPI.V_STATUS.ok(status);
    }
}
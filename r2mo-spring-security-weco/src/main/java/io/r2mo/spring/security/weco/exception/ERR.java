package io.r2mo.spring.security.weco.exception;

import io.r2mo.spring.common.exception.SpringE;
import org.springframework.http.HttpStatus;

/**
 * @author lang : 2025-12-09
 */
public interface ERR {
    SpringE _80502 = SpringE.of(-80502).state(HttpStatus.NOT_IMPLEMENTED);
    SpringE _80503 = SpringE.of(-80503).state(HttpStatus.UNAUTHORIZED);
    SpringE _80552 = SpringE.of(-80552).state(HttpStatus.NOT_IMPLEMENTED);
    SpringE _80553 = SpringE.of(-80553).state(HttpStatus.UNAUTHORIZED);
}

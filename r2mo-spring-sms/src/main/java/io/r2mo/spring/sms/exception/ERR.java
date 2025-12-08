package io.r2mo.spring.sms.exception;

import io.r2mo.spring.common.exception.SpringE;
import org.springframework.http.HttpStatus;

/**
 * @author lang : 2025-12-06
 */
interface ERR {
    SpringE _80351 = SpringE.of(-80351).state(HttpStatus.NOT_FOUND);
    SpringE _80352 = SpringE.of(-80352).state(HttpStatus.NOT_FOUND);
    SpringE _80353 = SpringE.of(-80353).state(HttpStatus.NOT_FOUND);
}

package io.r2mo.spring.security.sms.exception;

import io.r2mo.spring.common.exception.SpringE;
import org.springframework.http.HttpStatus;

/**
 * @author lang : 2025-12-06
 */
interface ERR {

    SpringE _80381 = SpringE.of(-80381).state(HttpStatus.BAD_REQUEST);
    SpringE _80382 = SpringE.of(-80382).state(HttpStatus.BAD_REQUEST);
    SpringE _80383 = SpringE.of(-80383).state(HttpStatus.INTERNAL_SERVER_ERROR);
}
